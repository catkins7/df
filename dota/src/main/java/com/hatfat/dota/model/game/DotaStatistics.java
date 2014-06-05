package com.hatfat.dota.model.game;

import android.content.res.Resources;
import android.util.Log;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.player.AdditionalUnit;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//calculates statistics for a given user for a given set of matches
public class DotaStatistics {

    public enum DotaStatisticsMode {
        ALL_FAVORITES(1),
        RANKED_STATS(2),
        PUBLIC_STATS(3),
        ALL_SUCCESS_STATS(4),
        OTHER_STATS(5),
        ALL_HEROES(6),
        CUSTOM_STATS(7);

        private int statsMode;

        DotaStatisticsMode(int statsMode) {
            this.statsMode = statsMode;
        }

        public int getMode() {
            return statsMode;
        }

        public static DotaStatisticsMode modeFromInt(int statsMode) {
            switch (statsMode) {
                case 1:
                    return ALL_FAVORITES;
                case 2:
                    return RANKED_STATS;
                case 3:
                    return PUBLIC_STATS;
                case 4:
                    return ALL_SUCCESS_STATS;
                case 5:
                    return OTHER_STATS;
                case 6:
                    return ALL_HEROES;
                case 7:
                    return CUSTOM_STATS;
                default:
                    return PUBLIC_STATS;
            }
        }

        public String getModePrefixString(Resources resources) {
            switch (this) {
                case PUBLIC_STATS:
                    return resources.getString(R.string.player_statistics_public_prefix_text);
                case RANKED_STATS:
                    return resources.getString(R.string.player_statistics_ranked_prefix_text);
                case OTHER_STATS:
                    return resources.getString(R.string.player_statistics_other_prefix_text);
                default:
                    return null;
            }
        }
    }

    private static final int MAX_FAVORITE_ITEMS = 5;
    private static final int MAX_FAVORITE_HEROES = 10;
    private static final int MAX_SUCCESS_HEROES = 8;
    private static final int MAX_HERO_FAVORITE_ITEMS = 3;

    private List<Match> matches;
    private SteamUser user;

    private List<ItemStats> favoriteItems;
    private List<HeroStats> favoriteHeroes;
    private List<ModeStats> favoriteModes;

    private List<HeroStats> allHeroes;

    private List<HeroStats> mostSuccessfulHeroes;
    private List<HeroStats> leastSuccessfulHeroes;

    private List<Integer> xpmPerGame;
    private List<Integer> gpmPerGame;
    private List<Integer> lastHitsPerGame;
    private List<Integer> deniesPerGame;
    private List<Integer> csTotalsPerGame;
    private List<Integer> gpmTotalsPerGame;
    private List<Integer> xpmTotalsPerGame;
    private List<Integer> killsPerGame;
    private List<Integer> deathsPerGame;
    private List<Integer> assistsPerGame;
    private List<Integer> durationsPerGame;
    private List<Float> teamworkScorePerGame;

    private int numberOfWins;
    private int numberOfValidMatches;
    private int csScore;
    private int gpmScore;
    private int xpmScore;
    private float teamworkScore;
    private float avgKillsAndAssistsOverDeaths;
    private float avgKillsOverDeaths;
    private float avgLastHits;
    private float avgDenies;
    private float avgKills;
    private float avgDeaths;
    private float avgAssists;
    private float avgGpm;
    private float avgXpm;
    private int avgDuration;

    public DotaStatistics(SteamUser user, List<Match> matches) {
        this.user = user;
        this.matches = matches;

        this.favoriteItems = new LinkedList();
        this.favoriteHeroes = new LinkedList();
        this.favoriteModes = new LinkedList();

        this.allHeroes = new LinkedList();

        this.mostSuccessfulHeroes = new LinkedList();
        this.leastSuccessfulHeroes = new LinkedList();

        this.xpmPerGame = new LinkedList();
        this.gpmPerGame = new LinkedList();
        this.lastHitsPerGame = new LinkedList();
        this.deniesPerGame = new LinkedList();
        this.csTotalsPerGame = new LinkedList();
        this.gpmTotalsPerGame = new LinkedList();
        this.xpmTotalsPerGame = new LinkedList();
        this.killsPerGame = new LinkedList();
        this.deathsPerGame = new LinkedList();
        this.assistsPerGame = new LinkedList();
        this.durationsPerGame = new LinkedList();
        this.teamworkScorePerGame = new LinkedList();

        calculateStatistics();
    }

    private void calculateStatistics() {
        long startTime = System.currentTimeMillis();

        //a hashmap of items with the number of times they've been purchased
        final HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        final HashMap<Hero, HeroStats> heroStatsMap = new HashMap();
        final HashMap<Match.GameMode, ModeStats> modeStatsMap = new HashMap();

        numberOfWins = 0;
        Set<Item> items = new TreeSet();

        for (Match match : matches) {
            items.clear();

            Player player = match.getPlayerForSteamUser(user);
            Hero hero = Heroes.get().getHero(player.getHeroIdString());

            if (hero == null) {
                //no hero for this match, so just skip to the next match
                continue;
            }

            numberOfValidMatches++;
            if (match.getPlayerMatchResultForPlayer(player).equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                numberOfWins++;
            }

            int totalCS = 0;
            int totalGPM = 0;
            int totalXPM = 0;
            for (Player p : match.getPlayers()) {
                totalCS += p.getLastHits() + p.getDenies();
                totalGPM += p.getGoldPerMinute();
                totalXPM += p.getXpPerMinute();
            }

            csTotalsPerGame.add(totalCS);
            gpmTotalsPerGame.add(totalGPM);
            xpmTotalsPerGame.add(totalXPM);
            gpmPerGame.add(player.getGoldPerMinute());
            xpmPerGame.add(player.getXpPerMinute());
            lastHitsPerGame.add(player.getLastHits());
            deniesPerGame.add(player.getDenies());
            killsPerGame.add(player.getKills());
            deathsPerGame.add(player.getDeaths());
            assistsPerGame.add(player.getAssists());
            durationsPerGame.add(match.getDuration());

            float kills = 0.0f;
            float assists = 0.0f;

            if (player.isRadiantPlayer()) {
                kills = match.getRadiantTotalKillCount();
                assists = match.getRadiantTotalAssistCount();
            }
            else if (player.isDirePlayer()) {
                kills = match.getDireTotalKillCount();
                assists = match.getDireTotalAssistCount();
            }

            teamworkScorePerGame.add((kills + assists) / kills);

            //calculate mode stats
            ModeStats modeStats = modeStatsMap.get(match.getGameMode());

            if (modeStats == null) {
                modeStats = new ModeStats(match.getGameMode());
                modeStatsMap.put(match.getGameMode(), modeStats);
            }

            modeStats.addMatch(match.getMatchId());

            //calculate hero stats
            HeroStats heroStats = heroStatsMap.get(hero);

            if (heroStats == null) {
                heroStats = new HeroStats(hero);
                heroStatsMap.put(hero, heroStats);
            }

            heroStats.addMatch(match.getMatchId());

            if (match.getPlayerMatchResultForPlayer(player).equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                modeStats.winCount++;
                heroStats.winCount++;
            }

            //add all the items for this hero
            for (int i = 0; i < 6; i++) {
                Item item = player.getItem(i);
                updateItemStatsWithItem(item, match, player, heroStats, items, itemStatsMap);
            }

            if (player.hasAdditionalUnitsWeWantToShow()) {
                AdditionalUnit unit = player.getAdditionalUnits().get(0);

                //make sure we include items on any additional units we care about (aka SPIRIT BEAR)
                for (int i = 0; i < 6; i++) {
                    Item item = unit.getItem(i);
                    updateItemStatsWithItem(item, match, player, heroStats, items, itemStatsMap);
                }
            }
        }

        //ALL HEROES SORTING
        allHeroes = new LinkedList(heroStatsMap.values());
        Collections.sort(allHeroes, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                //just sort alphabetically
                return lhs.hero.getLocalizedName().compareTo(rhs.hero.getLocalizedName());
            }
        });

        //FAVORITE HERO SORTING
        List<HeroStats> popularHeroStats = new LinkedList(heroStatsMap.values());
        Collections.sort(popularHeroStats, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                return rhs.getMatchCount() - lhs.getMatchCount();
            }
        });

        int maxFavoriteHeroes = Math.min(popularHeroStats.size(), MAX_FAVORITE_HEROES);
        favoriteHeroes = new LinkedList(popularHeroStats.subList(0, maxFavoriteHeroes));

        //HERO SUCCESS SORTING
        //only consider heroes that have been played in at least 1% of your games
        int gameCountThreshold = (int)((float)matches.size() * 0.01f);
        gameCountThreshold = Math.max(gameCountThreshold, 2); //require at least 2 games, minimum

        List<HeroStats> successHeroStats = new LinkedList(heroStatsMap.values());
        List<HeroStats> filteredSuccessHeroStats = new LinkedList();

        for (HeroStats heroStats : successHeroStats) {
            if (heroStats.getMatchCount() >= gameCountThreshold) {
                filteredSuccessHeroStats.add(heroStats);
            }
        }

        if (filteredSuccessHeroStats.size() < 2) {
            //no heroes were played enough!! just use all of them...
            filteredSuccessHeroStats = successHeroStats;
        }

        Collections.sort(filteredSuccessHeroStats, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                float diff = rhs.getWinPercent() - lhs.getWinPercent();
                if (diff < 0.0f) {
                    return -1;
                } else if (diff > 0.0f) {
                    return 1;
                } else {
                    //tie-breaker is the hero count
                    int tieBreaker = rhs.getMatchCount() - lhs.getMatchCount();

                    if (tieBreaker == 0) {
                        return lhs.hero.getLocalizedName().compareTo(rhs.hero.getLocalizedName());
                    } else {
                        return tieBreaker;
                    }
                }
            }
        });

        int maxMostHeroes = Math.min(filteredSuccessHeroStats.size() / 2, MAX_SUCCESS_HEROES);
        int maxLeastHeroes = Math.min(filteredSuccessHeroStats.size() / 2, MAX_SUCCESS_HEROES);

        this.mostSuccessfulHeroes = new LinkedList(filteredSuccessHeroStats.subList(0, maxMostHeroes));

        //filter out any heroes that lost 100% of the games (since they aren't very successful)
        LinkedList<HeroStats> finalMost = new LinkedList();
        for (HeroStats heroStats : mostSuccessfulHeroes) {
            if (heroStats.getWinPercent() > 0.0f) {
                finalMost.add(heroStats);
            }
        }

        //make the most successful list
        this.mostSuccessfulHeroes = finalMost;

        //resort for the least successful list (can't just use the end, since the order isn't exactly the same
        Collections.sort(filteredSuccessHeroStats, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                float diff = rhs.getWinPercent() - lhs.getWinPercent();
                if (diff < 0.0f) {
                    return 1;
                }
                else if (diff > 0.0f) {
                    return -1;
                }
                else {
                    //tie-breaker is the hero count
                    int tieBreaker = rhs.getMatchCount() - lhs.getMatchCount();

                    if (tieBreaker == 0) {
                        return rhs.hero.getLocalizedName().compareTo(lhs.hero.getLocalizedName());
                    }
                    else {
                        return tieBreaker;
                    }
                }
            }
        });

        this.leastSuccessfulHeroes = new LinkedList(filteredSuccessHeroStats.subList(0, maxLeastHeroes));

        //filter out any heroes that won 100% of the games (since they are pretty successful)
        LinkedList<HeroStats> finalLeast = new LinkedList();
        for (HeroStats heroStats : leastSuccessfulHeroes) {
            if (heroStats.getWinPercent() < 1.0f) {
                finalLeast.add(heroStats);
            }
        }

        //make the least successful list
        this.leastSuccessfulHeroes = finalLeast;

        //CALCULATE ALL HERO's FAVORED ITEMS HERE
        for (HeroStats heroStats : heroStatsMap.values()) {
            heroStats.sort();
        }

        //FAVORITE ITEM SORTING
        List<ItemStats> sortedItemStats = new LinkedList(itemStatsMap.values());
        Collections.sort(sortedItemStats, new Comparator<ItemStats>() {
            @Override
            public int compare(ItemStats lhs, ItemStats rhs) {
                return rhs.getTotalCost() - lhs.getTotalCost();
            }
        });

        int maxFavoriteItems = Math.min(sortedItemStats.size(), MAX_FAVORITE_ITEMS);
        favoriteItems = new LinkedList(sortedItemStats.subList(0, maxFavoriteItems));

        //FAVORITE MODE SORTING
        List<ModeStats> sortedModeStats = new LinkedList(modeStatsMap.values());
        Collections.sort(sortedModeStats, new Comparator<ModeStats>() {
            @Override
            public int compare(ModeStats lhs, ModeStats rhs) {
                return rhs.getGameCount() - lhs.getGameCount();
            }
        });

        favoriteModes = sortedModeStats;

        calculateAverages();

        long endTime = System.currentTimeMillis();
        Log.v("DotaStatistics", "calculateStatistics runtime " + (endTime - startTime) + " milliseconds for " + matches.size() + " matches.");
    }

    private void updateItemStatsWithItem(Item item, Match match, Player player, HeroStats heroStats, Set<Item> items, HashMap<Item, ItemStats> itemStatsMap) {
        if (item != null) {
            boolean itemAddedAlready = items.contains(item);
            items.add(item);

            ItemStats itemStats = itemStatsMap.get(item);

            if (itemStats == null) {
                itemStats = new ItemStats(item);
                itemStatsMap.put(item, itemStats);
            }

            itemStats.purchaseCount++;

            if (!itemAddedAlready) {
                Match.PlayerMatchResult result = match
                        .getPlayerMatchResultForPlayer(player);
                if (result == Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY) {
                    itemStats.winCount++;
                }

                itemStats.gameCount++;
            }

            heroStats.addItem(item);
        }
    }

    private void calculateAverages() {
        //trim outliers from the cs/gpm/xpm score calculations
        csTotalsPerGame = sortAndTrimIntegerList(csTotalsPerGame);
        gpmTotalsPerGame = sortAndTrimIntegerList(gpmTotalsPerGame);
        xpmTotalsPerGame = sortAndTrimIntegerList(xpmTotalsPerGame);
        teamworkScorePerGame = sortAndTrimFloatList(teamworkScorePerGame);

        //calculate all the average values

        //TEAMWORK SCORE
        teamworkScore = 0.0f;
        if (teamworkScorePerGame.size() > 0) {
            for (float f : teamworkScorePerGame) {
                teamworkScore += f;
            }
            teamworkScore /= teamworkScorePerGame.size();
        }

        //CS SCORE
        csScore = 0;
        if (csTotalsPerGame.size() > 0) {
            for (int i : csTotalsPerGame) {
                csScore += i;
            }
            csScore /= csTotalsPerGame.size();
        }

        //GPM SCORE
        gpmScore = 0;
        if (gpmTotalsPerGame.size() > 0) {
            for (int i : gpmTotalsPerGame) {
                gpmScore += i;
            }
            gpmScore /= gpmTotalsPerGame.size();
        }

        //XPM SCORE
        xpmScore = 0;
        if (xpmTotalsPerGame.size() > 0) {
            for (int i : xpmTotalsPerGame) {
                xpmScore += i;
            }
            xpmScore /= xpmTotalsPerGame.size();
        }

        //AVERAGE XPM
        avgXpm = 0.0f;
        if (xpmPerGame.size() > 0) {
            for (int i : xpmPerGame) {
                avgXpm += i;
            }
            avgXpm /= (float) xpmPerGame.size();
        }

        //AVERAGE GPM
        avgGpm = 0.0f;
        if (gpmPerGame.size() > 0) {
            for (int i : gpmPerGame) {
                avgGpm += i;
            }
            avgGpm /= (float) gpmPerGame.size();
        }

        //AVERAGE LAST HITS
        avgLastHits = 0.0f;
        if (lastHitsPerGame.size() > 0) {
            for (int i : lastHitsPerGame) {
                avgLastHits += i;
            }
            avgLastHits /= (float) lastHitsPerGame.size();
        }

        //AVERGAGE DENIES
        avgDenies = 0.0f;
        if (deniesPerGame.size() > 0) {
            for (int i : deniesPerGame) {
                avgDenies += i;
            }
            avgDenies /= (float) deniesPerGame.size();
        }

        //AVERAGE KILLS
        avgKills = 0.0f;
        if (killsPerGame.size() > 0) {
            for (int i : killsPerGame) {
                avgKills += i;
            }
            avgKills /= (float) killsPerGame.size();
        }

        //AVERAGE DEATHS
        avgDeaths = 0.0f;
        if (deathsPerGame.size() > 0) {
            for (int i : deathsPerGame) {
                avgDeaths += i;
            }
            avgDeaths /= (float) deathsPerGame.size();
        }

        //AVERAGE ASSISTS
        avgAssists = 0.0f;
        if (assistsPerGame.size() > 0) {
            for (int i : assistsPerGame) {
                avgAssists += i;
            }
            avgAssists /= (float) assistsPerGame.size();
        }

        //AVERAGE K/D
        avgKillsOverDeaths = avgKills / avgDeaths;

        //AVERAGE K+A/D
        avgKillsAndAssistsOverDeaths = (avgKills + avgAssists) / avgDeaths;

        //AVERAGE DURATION
        avgDuration = 0;
        if (durationsPerGame.size() > 0) {
            for (int i : durationsPerGame) {
                avgDuration += i;
            }
            avgDuration /= durationsPerGame.size();
        }
    }

    private List<Integer> sortAndTrimIntegerList(List<Integer> list) {
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        //we want to chop off the top and bottom 10% to reduce the effect outliers have on the calculations
        int numToChop = list.size() / 10;
        list = list.subList(numToChop, list.size());
        list = list.subList(0, list.size() - numToChop);

        return list;
    }

    private List<Float> sortAndTrimFloatList(List<Float> list) {
        Collections.sort(list, new Comparator<Float>() {
            @Override
            public int compare(Float lhs, Float rhs) {
                return Float.compare(lhs, rhs);
            }
        });

        //we want to chop off the top and bottom 10% to reduce the effect outliers have on the calculations
        int numToChop = list.size() / 10;
        list = list.subList(numToChop, list.size());
        list = list.subList(0, list.size() - numToChop);

        return list;
    }

    public List<HeroStats> getAllHeroes() {
        return allHeroes;
    }

    public List<ItemStats> getFavoriteItems() {
        return favoriteItems;
    }

    public List<HeroStats> getFavoriteHeroes() {
        return favoriteHeroes;
    }

    public List<HeroStats> getMostSuccessfulHeroes() {
        return mostSuccessfulHeroes;
    }

    public List<HeroStats> getLeastSuccessfulHeroes() {
        return leastSuccessfulHeroes;
    }

    public List<ModeStats> getFavoriteGameModes() {
        return favoriteModes;
    }

    public String getFavoriteGameModeString() {
        ModeStats favorite = favoriteModes.size() > 0 ? favoriteModes.get(0) : null;
        if (favorite != null) {
            return favorite.mode.getGameModeName();
        }
        else {
            return null;
        }
    }

    public int getCsScore() {
        return csScore;
    }

    public int getGpmScore() {
        return gpmScore;
    }

    public int getXpmScore() {
        return xpmScore;
    }

    public int getGameCount() {
        return matches.size();
    }

    public String getAverageXpmString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_one_decimal), avgXpm);
    }

    public String getAverageGpmString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_one_decimal), avgGpm);
    }

    public String getAverageLastHitsString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_one_decimal),
                avgLastHits);
    }

    public String getAverageDeniesString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_one_decimal), avgDenies);
    }

    public String getAverageKillsOverDeathsString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_two_decimal), avgKillsOverDeaths);
    }

    public String getAverageKillsAndAssistsOverDeathsString(Resources resources) {
        return String.format(
                resources.getString(R.string.player_statistics_single_float_two_decimal), avgKillsAndAssistsOverDeaths);
    }

    public String getWinPercentString(Resources resources) {
        return String.format(resources.getString(R.string.player_statistics_single_float_one_decimal_with_percent), (float)numberOfWins / (float)numberOfValidMatches * 100.0f);
    }

    public String getTeamworkScoreString(Resources resources) {
        return String.format(resources.getString(R.string.player_statistics_single_float_two_decimal), teamworkScore);
    }

    public String getGameCountString() {
        return String.valueOf(getGameCount());
    }

    public String getCsScoreString() {
        return String.valueOf(csScore);
    }

    public String getGpmScoreString() {
        return String.valueOf(gpmScore);
    }

    public String getXpmScoreString() {
        return String.valueOf(xpmScore);
    }

    public String getCompositeScoreString() {
        return String.valueOf(xpmScore + gpmScore);
    }

    public float getAvgKills() {
        return avgKills;
    }

    public float getAvgDeaths() {
        return avgDeaths;
    }

    public float getAvgAssists() {
        return avgAssists;
    }

    public int getAvgDuration() {
        return avgDuration;
    }

    public SteamUser getSteamUser() {
        return user;
    }

    public String getAvgKDAString(Resources resources) {
        String string = resources.getString(R.string.player_statistics_avg_kda_text);
        return String.format(string, avgKills, avgDeaths, avgAssists);
    }

    public String getAvgDurationString(Resources resources) {
        int hours = avgDuration / 60 / 60;
        int minutes = avgDuration / 60 % 60;
        int seconds = avgDuration % 60;

        if (hours < 1) {
            return String.format("%d:%02d", minutes, seconds);
        }
        else {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public static class ItemStats {
        public Item item;

        public int gameCount;
        public int winCount;
        public int purchaseCount;

        public ItemStats(Item item) {
            this.item = item;
            gameCount = 0;
            winCount = 0;
            purchaseCount = 0;
        }

        public boolean isPurchaseCountGreaterThanOne() {
            return purchaseCount > 1;
        }

        public int getTotalCost() {
            return item.getItemCost() * purchaseCount;
        }

        public String getWinString(Resources resources) {
            float percent = (float)winCount / (float)gameCount * 100.0f;
            return resources.getString(R.string.player_statistics_single_float_one_decimal_with_percent, percent);
        }
    }

    public static class ModeStats {
        public Match.GameMode mode;
        public List<String> matchIds;
        public int winCount;

        public ModeStats(Match.GameMode mode) {
            this.mode = mode;
            matchIds = new LinkedList();
            winCount = 0;
        }

        public void addMatch(String matchId) {
            matchIds.add(matchId);
        }

        public int getGameCount() {
            return matchIds.size();
        }

        public List<String> getMatchIds() {
            return matchIds;
        }

        public String getGameCountString() {
            return String.valueOf(getGameCount());
        }

        public String getWinPercentString(Resources resources) {
            return String.format(resources.getString(R.string.player_statistics_single_float_one_decimal_with_percent), (float)winCount / (float)getGameCount() * 100.0f);
        }

        public String getSummaryString(Resources resources) {
            if (getGameCount() <= 1) {
                return String.format(resources.getString(R.string.player_statistics_mode_info_summary_text_single), getGameCount(), (float)winCount / (float)getGameCount() * 100.0f);
            }
            else {
                return String.format(resources.getString(R.string.player_statistics_mode_info_summary_text_plural), getGameCount(), (float)winCount / (float)getGameCount() * 100.0f);
            }
        }
    }

    public static class HeroStats {
        public Hero hero;
        public List<String> matchIds;
        public int winCount;

        public HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        public List<ItemStats> favoriteHeroItems = new LinkedList();

        public HeroStats(Hero hero) {
            this.hero = hero;
            matchIds = new LinkedList();
        }

        public void addMatch(String matchId) {
            matchIds.add(matchId);
        }

        public void addItem(Item item) {
            ItemStats itemStats = itemStatsMap.get(item);

            if (itemStats == null) {
                itemStats = new ItemStats(item);
                itemStatsMap.put(item, itemStats);
            }

            itemStats.purchaseCount++;
        }

        public int getMatchCount() {
            return matchIds.size();
        }

        public List<String> getMatchIds() {
            return matchIds;
        }

        public boolean isMatchCountGreaterThanOne() {
            return getMatchCount() > 1;
        }

        public float getWinPercent() {
            return (float)winCount / (float)getMatchCount();
        }

        public String getMatchCountString(Resources resources) {
            return String.valueOf(getMatchCount());
        }

        public String getWinPercentString(Resources resources) {
            float percent = (float)winCount / (float)getMatchCount() * 100.0f;
            return String.format(resources.getString(R.string.player_statistics_single_float_one_decimal_with_percent), percent);
        }

        public void sort() {
            List<ItemStats> sortedItemStats = new LinkedList(itemStatsMap.values());
            Collections.sort(sortedItemStats, new Comparator<ItemStats>() {
                @Override
                public int compare(ItemStats lhs, ItemStats rhs) {
                    return rhs.getTotalCost() - lhs.getTotalCost();
                }
            });

            int maxFavoriteItems = Math.min(sortedItemStats.size(), MAX_HERO_FAVORITE_ITEMS);
            favoriteHeroItems = new LinkedList(sortedItemStats.subList(0, maxFavoriteItems));

//            Log.i("Dota2Friend", "-------------------------------------------");
//            Log.i("Dota2Friend", hero.getLocalizedName());
//            for (ItemStats stat : favoriteHeroItems) {
//                Log.i("Dota2Friend", "---> " + stat.item.getName() + "   purchaseCount: " + stat.purchaseCount + "   cost: " + stat.item.getItemCost() + "   totalCost: " + stat.getTotalCost());
//            }
        }
    }
}

