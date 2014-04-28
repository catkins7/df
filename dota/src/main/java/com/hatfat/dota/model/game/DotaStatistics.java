package com.hatfat.dota.model.game;

import android.content.res.Resources;
import android.util.Log;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
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

    private static final int MAX_FAVORITE_ITEMS = 3;
    private static final int MAX_FAVORITE_HEROES = 6;
    private static final int MAX_HERO_FAVORITE_ITEMS = 3;

    private List<Match> matches;
    private SteamUser user;

    private List<ItemStats> favoriteItems;
    private List<HeroStats> favoriteHeroes;

    private List<Integer> csTotalsPerGame;
    private List<Integer> gpmTotalsPerGame;
    private List<Integer> xpmTotalsPerGame;
    private List<Integer> killsPerGame;
    private List<Integer> deathsPerGame;
    private List<Integer> assistsPerGame;
    private List<Integer> durationsPerGame;

    private int csScore;
    private int gpmScore;
    private int xpmScore;
    private float avgKills;
    private float avgDeaths;
    private float avgAssists;
    private int avgDuration;

    public DotaStatistics(SteamUser user, List<Match> matches) {
        this.user = user;
        this.matches = matches;

        this.favoriteItems = new LinkedList();
        this.favoriteHeroes = new LinkedList();

        this.csTotalsPerGame = new LinkedList();
        this.gpmTotalsPerGame = new LinkedList();
        this.xpmTotalsPerGame = new LinkedList();
        this.killsPerGame = new LinkedList();
        this.deathsPerGame = new LinkedList();
        this.assistsPerGame = new LinkedList();
        this.durationsPerGame = new LinkedList();

        calculateStatistics();
    }

    private void calculateStatistics() {
        long startTime = System.currentTimeMillis();

        //a hashmap of items with the number of times they've been purchased
        final HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        final HashMap<Hero, HeroStats> heroStatsMap = new HashMap();

        Set<Item> items = new TreeSet();

        for (Match match : matches) {
            items.clear();

            Player player = match.getPlayerForSteamUser(user);
            Hero hero = Heroes.get().getHero(player.getHeroIdString());

            if (hero == null) {
                //no hero for this match, so just skip to the next match
                continue;
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
            killsPerGame.add(player.getKills());
            deathsPerGame.add(player.getDeaths());
            assistsPerGame.add(player.getAssists());
            durationsPerGame.add(match.getDuration());

            HeroStats heroStats = heroStatsMap.get(hero);

            if (heroStats == null) {
                heroStats = new HeroStats(hero);
                heroStatsMap.put(hero, heroStats);
            }

            heroStats.heroCount++;

            for (int i = 0; i < 6; i++) {
                Item item = player.getItem(i);
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
        }

        List<HeroStats> sortedHeroStats = new LinkedList(heroStatsMap.values());
        Collections.sort(sortedHeroStats, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                return rhs.heroCount - lhs.heroCount;
            }
        });

        int maxFavoriteHeroes = Math.min(sortedHeroStats.size(), MAX_FAVORITE_HEROES);
        favoriteHeroes = new LinkedList(sortedHeroStats.subList(0, maxFavoriteHeroes));

        for (HeroStats heroStats : favoriteHeroes) {
            heroStats.sort();
        }

        List<ItemStats> sortedItemStats = new LinkedList(itemStatsMap.values());
        Collections.sort(sortedItemStats, new Comparator<ItemStats>() {
            @Override
            public int compare(ItemStats lhs, ItemStats rhs) {
                return rhs.getTotalCost() - lhs.getTotalCost();
            }
        });

        int maxFavoriteItems = Math.min(sortedItemStats.size(), MAX_FAVORITE_ITEMS);
        favoriteItems = new LinkedList(sortedItemStats.subList(0, maxFavoriteItems));

        calculateAverages();

        long endTime = System.currentTimeMillis();
        Log.v("DotaStatistics", "calculateStatistics runtime " + (endTime - startTime) + " milliseconds for " + matches.size() + " matches.");
    }

    private void calculateAverages() {
        //trim outliers from the cs/gpm/xpm score calculations
        csTotalsPerGame = sortAndTrimIntegerList(csTotalsPerGame);
        gpmTotalsPerGame = sortAndTrimIntegerList(gpmTotalsPerGame);
        xpmTotalsPerGame = sortAndTrimIntegerList(xpmTotalsPerGame);

        //calculate all the average values

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

    public List<ItemStats> getFavoriteItems() {
        return favoriteItems;
    }

    public List<HeroStats> getFavoriteHeroes() {
        return favoriteHeroes;
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

    public String getAvgKDAString(Resources resources) {
        String string = resources.getString(R.string.player_statistics_avg_kda_text);
        return String.format(string, avgKills, avgAssists, avgDeaths);
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
            purchaseCount = 0;
        }

        public int getTotalCost() {
            return item.getItemCost() * purchaseCount;
        }

        public String getWinString() {
            float percent = (float)winCount / (float)gameCount * 100.0f;
            return String.format("%.1f", percent) + "%";
        }
    };

    public static class HeroStats {
        public Hero hero;
        public int heroCount;

        public HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        public List<ItemStats> favoriteHeroItems = new LinkedList();

        public HeroStats(Hero hero) {
            this.hero = hero;
            heroCount = 0;
        }

        public void addItem(Item item) {
            ItemStats itemStats = itemStatsMap.get(item);

            if (itemStats == null) {
                itemStats = new ItemStats(item);
                itemStatsMap.put(item, itemStats);
            }

            itemStats.purchaseCount++;
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
        }
    }
}

