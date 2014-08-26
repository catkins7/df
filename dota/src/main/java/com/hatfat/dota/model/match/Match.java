package com.hatfat.dota.model.match;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.annotations.SerializedName;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Item;
import com.hatfat.dota.model.player.AdditionalUnit;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.services.MatchFetcher;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Match implements Comparable {

    public static final String MATCH_UPDATED = "MatchUpdated";
    public static final String MATCH_UPDATED_ID_KEY = "MatchUpdated_MatchId_Key";

    public enum MatchResult {
        MATCH_RESULT_UNKNOWN,
        MATCH_RESULT_RADIANT_VICTORY,
        MATCH_RESULT_DIRE_VICTORY;

        public int getDescriptionStringResourceId() {
            switch (this) {
                case MATCH_RESULT_RADIANT_VICTORY:
                    return R.string.match_result_radiant_victory;
                case MATCH_RESULT_DIRE_VICTORY:
                    return R.string.match_result_dire_victory;
                case MATCH_RESULT_UNKNOWN:
                default:
                    return R.string.match_result_unknown;
            }
        }

        public int getBackgroundResourceId() {
            switch (this) {
                case MATCH_RESULT_RADIANT_VICTORY:
                    return R.drawable.radiant_background;
                case MATCH_RESULT_DIRE_VICTORY:
                    return R.drawable.dire_background;
                case MATCH_RESULT_UNKNOWN:
                default:
                    return R.drawable.unselectable_background;
            }
        }
    }
    public enum PlayerMatchResult {
        PLAYER_MATCH_RESULT_UNKNOWN,
        PLAYER_MATCH_RESULT_VICTORY,
        PLAYER_MATCH_RESULT_DEFEAT;

        public int getDescriptionStringResourceId() {
            switch (this) {
                case PLAYER_MATCH_RESULT_VICTORY:
                    return R.string.match_result_player_victory;
                case PLAYER_MATCH_RESULT_DEFEAT:
                    return R.string.match_result_player_defeat;
                default:
                    return R.string.match_result_unknown;
            }
        }
    }
    public enum LobbyType {
        PUBLIC_MATCHMAKING(R.string.lobby_type_public_matchmaking),
        PRACTICE(R.string.lobby_type_practice),
        TOURNAMENT(R.string.lobby_type_tournament),
        TUTORIAL(R.string.lobby_type_tutorial),
        CO_OP_BOTS(R.string.lobby_type_coop_bots),
        TEAM_MATCH(R.string.lobby_type_team_match),
        SOLO_QUEUE(R.string.lobby_type_solo_queue),
        RANKED(R.string.lobby_type_ranked),
        CASUAL_1V1(R.string.lobby_type_casual_1v1),
        UNKNOWN(R.string.lobby_type_unknown);

        private int stringResourceId;

        LobbyType(int resourceId) {
            this.stringResourceId = resourceId;
        }

        public static LobbyType fromInt(int type) {
            switch (type) {
                case 0:
                    return PUBLIC_MATCHMAKING;
                case 1:
                    return PRACTICE;
                case 2:
                    return TOURNAMENT;
                case 3:
                    return TUTORIAL;
                case 4:
                    return CO_OP_BOTS;
                case 5:
                    return TEAM_MATCH;
                case 6:
                    return SOLO_QUEUE;
                case 7:
                    return RANKED;
                case 8:
                    return CASUAL_1V1;
                default:
                    return UNKNOWN;
            }
        }
        public String getLobbyTypeString(Resources resources) {
            return resources.getString(stringResourceId);
        }
    }

//    https://github.com/SteamRE/SteamKit/blob/master/Resources/Protobufs/dota/dota_gcmessages_common.proto
    public enum GameMode {
        None(R.string.game_mode_none),  //games played before the game mode was added to the data
        AllPick(R.string.game_mode_all_pick),
        CaptainsMode(R.string.game_mode_captains_mode),
        RandomDraft(R.string.game_mode_random_draft),
        SingleDraft(R.string.game_mode_single_draft),
        AllRandom(R.string.game_mode_all_random),
        IntroDeath(R.string.game_mode_intro_death),
        Diretide(R.string.game_mode_diretide),
        ReverseCaptainsMode(R.string.game_mode_reverse_cm),
        Greeviling(R.string.game_mode_greeviling),
        Tutorial(R.string.game_mode_tutorial),
        MidOnly(R.string.game_mode_mid_only),
        LeastPlayed(R.string.game_mode_least_played),
        LimitedHeroes(R.string.game_mode_limited_heroes),
        ForcedHeroes(R.string.game_mode_compendium),
        CustomGame(R.string.game_mode_custom_game),
        CaptainsDraft(R.string.game_mode_captains_draft),
        BalancedDraft(R.string.game_mode_balanced_draft),
        AbilityDraft(R.string.game_mode_ability_draft),
        Event(R.string.game_mode_event),
        AllRandomDeathMatch(R.string.game_mode_ardm),
        Mid1v1(R.string.game_mode_1v1_mid),

        NoMatchDetails(R.string.game_mode_no_match_details),
        AllModes(R.string.game_mode_all_modes),
        Unknown(R.string.game_mode_unknown);

        private int stringResourceId;

        GameMode(int resourceId) {
            this.stringResourceId = resourceId;
        }

        public static GameMode fromInt(int type) {
            switch (type) {
                case 0:
                    return None;
                case 1:
                    return AllPick;
                case 2:
                    return CaptainsMode;
                case 3:
                    return RandomDraft;
                case 4:
                    return SingleDraft;
                case 5:
                    return AllRandom;
                case 6:
                    return IntroDeath;
                case 7:
                    return Diretide;
                case 8:
                    return ReverseCaptainsMode;
                case 9:
                    return Greeviling;
                case 10:
                    return Tutorial;
                case 11:
                    return MidOnly;
                case 12:
                    return LeastPlayed;
                case 13:
                    return LimitedHeroes;
                case 14:
                    return ForcedHeroes;
                case 15:
                    return CustomGame;
                case 16:
                    return CaptainsDraft;
                case 17:
                    return BalancedDraft;
                case 18:
                    return AbilityDraft;
                case 19:
                    return Event;
                case 20:
                    return AllRandomDeathMatch;
                case 21:
                    return Mid1v1;
                default:
                    return Unknown;
            }
        }
        public String getGameModeString(Resources resources) {
            return resources.getString(stringResourceId);
        }
    }

    @SerializedName("radiant_win")
    boolean radiantWin;

    @SerializedName("duration")
    int duration;

    @SerializedName("tower_status_radiant")
    int towerStatusRadiant;

    @SerializedName("tower_status_dire")
    int towerStatusDire;

    @SerializedName("barracks_status_radiant")
    int barracksStatusRadiant;

    @SerializedName("barracks_status_dire")
    int barracksStatusDire;

    @SerializedName("cluster")
    int cluster;

    @SerializedName("first_blood_time")
    int firstBloodTime;

    @SerializedName("human_players")
    int humanPlayers;

    @SerializedName("leagueid")
    int leagueId;

    @SerializedName("positive_votes")
    int positiveVotes;

    @SerializedName("negative_votes")
    int negativeVotes;

    @SerializedName("game_mode")
    int gameMode;

    @SerializedName("has_match_details")
    boolean hasMatchDetails;

    @SerializedName("match_id")
    String matchId;

    @SerializedName("match_seq_num")
    long matchSeqNumber;

    @SerializedName("start_time")
    long startTime; //seconds from 1970

    @SerializedName("lobby_type")
    int lobbyType;

    @SerializedName("players")
    List<Player> players;

    private transient Item itemOfTheMatch;
    private transient Player playerOfTheMatch;

    public Match(String matchId) {
        this.matchId = matchId;
    }

    public Match() {

    }

    public String getMatchId() {
        return matchId;
    }
    public MatchResult getMatchResult() {
        if (!hasMatchDetails) {
            return MatchResult.MATCH_RESULT_UNKNOWN;
        }
        else if (radiantWin) {
            return MatchResult.MATCH_RESULT_RADIANT_VICTORY;
        }
        else {
            return MatchResult.MATCH_RESULT_DIRE_VICTORY;
        }
    }
    public Date getStartTimeDate() {
        return new Date(startTime);
    }
    public long getStartTime() {
        return startTime;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public LobbyType getLobbyType() {
        return LobbyType.fromInt(lobbyType);
    }
    public GameMode getGameMode() {
        if (!hasMatchDetails) {
            return GameMode.NoMatchDetails;
        }

        return GameMode.fromInt(gameMode);
    }
    public String getMatchTypeString(Resources resources) {
        if (getLobbyType() == LobbyType.PUBLIC_MATCHMAKING || getLobbyType() == LobbyType.RANKED) {
            return getGameMode().getGameModeString(resources);
        }
        else {
            return getLobbyType().getLobbyTypeString(resources) + " " + getGameMode()
                    .getGameModeString(resources);
        }
    }
    public boolean shouldBeUsedInRealStatistics() {
        if (!isPublicMatchmaking() && !isRankedMatchmaking()) {
            //only ranked and public matches are used in statistics
            return false;
        }

        switch (getGameMode()) {
            case None:
            case AllPick:
            case CaptainsMode:
            case RandomDraft:
            case SingleDraft:
            case AllRandom:
            case ReverseCaptainsMode:
            case LeastPlayed:
            case LimitedHeroes:
            case CaptainsDraft:
            case BalancedDraft:
                //only use games that are at least 5 minutes long
                return duration >= 5 * 60;

            default:
                return false;
        }
    }
    public boolean shouldSavePlayerAbilities() {
        return isAbilityDraft();
    }
    public boolean isAbilityDraft() {
        return getGameMode() == GameMode.AbilityDraft;
    }
    public void setHasMatchDetails(boolean hasMatchDetails) {
        this.hasMatchDetails = hasMatchDetails;
    }
    public boolean hasMatchDetails() {
        return hasMatchDetails;
    }
    public String toString() {
        return super.toString() + "[matchId: " + matchId + "]";
    }

    public boolean isRankedMatchmaking() {
        return getLobbyType().equals(LobbyType.RANKED);
    }
    public boolean isPublicMatchmaking() {
        return getLobbyType().equals(LobbyType.PUBLIC_MATCHMAKING);
    }
    public PlayerMatchResult getPlayerMatchResultForPlayer(Player player) {
        if (players == null || !players.contains(player)) {
            return PlayerMatchResult.PLAYER_MATCH_RESULT_UNKNOWN;
        }

        MatchResult matchResult = getMatchResult();

        if (matchResult == MatchResult.MATCH_RESULT_UNKNOWN) {
            return PlayerMatchResult.PLAYER_MATCH_RESULT_UNKNOWN;
        }
        else if (player.isRadiantPlayer() && matchResult == MatchResult.MATCH_RESULT_RADIANT_VICTORY) {
            return PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY;
        }
        else if (player.isDirePlayer() && matchResult == MatchResult.MATCH_RESULT_DIRE_VICTORY) {
            return PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY;
        }
        else {
            return PlayerMatchResult.PLAYER_MATCH_RESULT_DEFEAT;
        }
    }

    public int getMatchResultStringResourceIdForPlayer(Player player) {
        return getPlayerMatchResultForPlayer(player).getDescriptionStringResourceId();
    }

    public int getMatchResultBackgroundResourceIdForPlayer(Player player) {
        PlayerMatchResult result = getPlayerMatchResultForPlayer(player);

        switch (result) {
            case PLAYER_MATCH_RESULT_VICTORY:
                return R.drawable.match_result_win_background;
            case PLAYER_MATCH_RESULT_DEFEAT:
                return R.drawable.match_result_loss_background;
            default:
                return R.drawable.unselectable_background;
        }
    }

    public boolean isSteamUserInMatch(SteamUser user) {
        if (players != null) {
            for (Player player : players) {
                if (player.getAccountId() == user.getAccountIdLong()) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getTimeAgoString(Resources resources) {
        long currentTime = new Date().getTime() / 1000; //current time in seconds
        long timeAgo = currentTime - startTime;

        int timeStringResourceId;

        timeAgo /= 60; //minutes ago

        if (timeAgo <= 1) {
            timeStringResourceId = R.string.match_summary_time_ago_minute;
        }
        else if (timeAgo < 60) {
            timeStringResourceId = R.string.match_summary_time_ago_minutes;
        }
        else {
            timeAgo /= 60; // hours ago

            if (timeAgo <= 1) {
                timeStringResourceId = R.string.match_summary_time_ago_hour;
            }
            else if (timeAgo < 24) {
                timeStringResourceId = R.string.match_summary_time_ago_hours;
            }
            else {
                timeAgo /= 24; //days ago

                if (timeAgo <= 1) {
                    timeStringResourceId = R.string.match_summary_time_ago_day;
                }
                else {
                    timeStringResourceId = R.string.match_summary_time_ago_days;
                }
            }
        }

        return String.format(resources.getString(timeStringResourceId), timeAgo);
    }
    public String getDurationString() {
        if (!hasMatchDetails) {
            return "";
        }

        int hours = duration / 60 / 60;
        int minutes = duration / 60 % 60;
        int seconds = duration % 60;

        if (hours < 1) {
            return String.format("%d:%02d", minutes, seconds);
        }
        else {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }
    public int getDuration() {
        return duration;
    }

    public int getRadiantTotalKillCount() {
        int killCount = 0;

        for (Player player : players) {
            if (player.isRadiantPlayer()) {
                killCount += player.getKills();
            }
        }

        return killCount;
    }
    public int getDireTotalKillCount() {
        int killCount = 0;

        for (Player player : players) {
            if (player.isDirePlayer()) {
                killCount += player.getKills();
            }
        }

        return killCount;
    }
    public int getRadiantTotalDeathCount() {
        int deathCount = 0;

        for (Player player : players) {
            if (player.isRadiantPlayer()) {
                deathCount += player.getDeaths();
            }
        }

        return deathCount;
    }
    public int getDireTotalDeathCount() {
        int deathCount = 0;

        for (Player player : players) {
            if (player.isDirePlayer()) {
                deathCount += player.getDeaths();
            }
        }

        return deathCount;
    }
    public int getRadiantTotalAssistCount() {
        int assistCount = 0;

        for (Player player : players) {
            if (player.isRadiantPlayer()) {
                assistCount += player.getAssists();
            }
        }

        return assistCount;
    }
    public int getDireTotalAssistCount() {
        int assistCount = 0;

        for (Player player : players) {
            if (player.isDirePlayer()) {
                assistCount += player.getAssists();
            }
        }

        return assistCount;
    }

    public Player getPlayerForSteamUser(SteamUser user) {
        if (players == null) {
            return null;
        }

        for (Player player : players) {
            if (player.getAccountId() == user.getAccountIdLong()) {
                return player;
            }
        }

        return null;
    }

    void updateWithMatch(Match match) {
        boolean shouldBroadcast = false;
        boolean originallyHadMatchDetails = hasMatchDetails;

        if (!hasMatchDetails && match.hasMatchDetails) {
            shouldBroadcast = true;

            radiantWin = match.radiantWin;
            duration = match.duration;
            towerStatusRadiant = match.towerStatusRadiant;
            towerStatusDire = match.towerStatusDire;
            barracksStatusRadiant = match.barracksStatusRadiant;
            barracksStatusDire = match.barracksStatusDire;
            cluster = match.cluster;
            firstBloodTime = match.firstBloodTime;
            humanPlayers = match.humanPlayers;
            leagueId = match.leagueId;
            positiveVotes = match.positiveVotes;
            negativeVotes = match.negativeVotes;
            gameMode = match.gameMode;
            hasMatchDetails = match.hasMatchDetails;
            players = match.players;
        }

        if (originallyHadMatchDetails && match.hasMatchDetails) {
            //compare each player and prefer either which has the real player as opposed to an anonymous one
            if (players == null) {
                shouldBroadcast = true;
                players = match.players;
            }
            else {
                if (players.size() != match.players.size()) {
                    throw new RuntimeException("sizes not equal!!");
                }

                for (int i = 0; i < players.size(); i++) {
                    Player oldPlayer = players.get(i);
                    Player newPlayer = match.players.get(i);

                    if (oldPlayer.isAnonymous() && !newPlayer.isAnonymous()) {
                        //old player is anonymous, but the new one isn't!
                        //we have the real data, so lets replace it
                        players.remove(i);
                        players.add(i, newPlayer);

                        shouldBroadcast = true;
                    }
                }
            }
        }

        matchId = match.matchId;
        matchSeqNumber = match.matchSeqNumber;
        startTime = match.startTime;
        lobbyType = match.lobbyType;

        if (shouldBroadcast) {
            broadcastMatchChanged();
        }
    }

    public void getMatchDetailsIfNeeded() {
        if (!hasMatchDetails) {
            MatchFetcher.fetchMatchDetails(getMatchId());
        }
    }

    public void getMatchDetailsIfNeededForUser(SteamUser user) {
        boolean isUserInMatch = isSteamUserInMatch(user);

        if (!hasMatchDetails || !isUserInMatch) {
            MatchFetcher.fetchMatchDetails(getMatchId());
        }
    }

    public Item getItemOfTheMatch() {
        if (itemOfTheMatch != null) {
            return itemOfTheMatch;
        }

        //need to calculate
        HashMap<Item, Integer> itemPurchaseMap = new HashMap();

        for (Player player : players) {
            for (int i = 0; i < 6; i++) {
                Item item = player.getItem(i);

                if (item == null) {
                    continue;
                }

                if (!itemPurchaseMap.containsKey(item)) {
                    itemPurchaseMap.put(item, 1);
                }
                else {
                    int currentValue = itemPurchaseMap.get(item);
                    itemPurchaseMap.put(item, currentValue + 1);
                }
            }

            if (player.hasAdditionalUnitsWeWantToShow()) {
                AdditionalUnit unit = player.getAdditionalUnits().get(0);

                //make sure we include items on any additional units we care about (aka SPIRIT BEAR)
                for (int i = 0; i < 6; i++) {
                    Item item = unit.getItem(i);

                    if (item == null) {
                        continue;
                    }

                    if (!itemPurchaseMap.containsKey(item)) {
                        itemPurchaseMap.put(item, 1);
                    }
                    else {
                        int currentValue = itemPurchaseMap.get(item);
                        itemPurchaseMap.put(item, currentValue + 1);
                    }
                }
            }
        }

        int highestCost = 0;

        for (Item item : itemPurchaseMap.keySet()) {
            int count = itemPurchaseMap.get(item);
            int newCost = count * item.getItemCost();

            if (newCost > highestCost) {
                highestCost = newCost;
                itemOfTheMatch = item;
            }
        }

        return itemOfTheMatch;
    }

    public Player getPlayerOfTheMatch() {
        if (playerOfTheMatch != null) {
            return playerOfTheMatch;
        }

        if (duration < 5 * 60) {
            //game was less than 5 minutes long, so there will be no player of the match
            return null;
        }

        if (players.size() < 10) {
            //no player of the game for games with less than 10 people
            return null;
        }

        for (Player player : players) {
            if (playerOfTheMatch == null) {
                playerOfTheMatch = player;
            }
            else {
                if (player.getPlayerOfTheMatchScore() > playerOfTheMatch.getPlayerOfTheMatchScore()) {
                    playerOfTheMatch = player;
                }
            }
        }

        return playerOfTheMatch;
    }

    private void broadcastMatchChanged() {
        Intent intent = new Intent(MATCH_UPDATED);
        intent.putExtra(MATCH_UPDATED_ID_KEY, matchId);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private static Comparator<Match> comparator;
    private static Comparator<String> matchIdComparator;

    public static Comparator<Match> getComparator() {
        if (comparator == null) {
            comparator = new Comparator<Match>() {
                @Override
                public int compare(Match match, Match match2) {
                    return match2.matchId.compareToIgnoreCase(match.matchId);
                }
            };
        }

        return comparator;
    }

    public static Comparator<String> getMatchIdComparator() {
        if (matchIdComparator == null) {
            matchIdComparator = new Comparator<String>() {
                @Override
                public int compare(String matchId, String matchId2) {
                    long firstNum = Long.valueOf(matchId);
                    long secondNum = Long.valueOf(matchId2);

                    if (firstNum == secondNum) {
                        return 0;
                    }

                    if (firstNum > secondNum) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            };
        }

        return matchIdComparator;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Match) {
            Match otherMatch = (Match)o;
            if (matchId != null && matchId.length() > 0) {
                return matchId.equals(otherMatch.matchId);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(matchId);
    }


    @Override
    public int compareTo(Object o) {
        //Sorts matches by date, so the newest match is first
        if (o != null && o instanceof Match) {
            Match otherMatch = (Match)o;
            long timeDiff = otherMatch.getStartTime() - startTime;

            if (timeDiff > 0) {
                return 1;
            }
            else if (timeDiff < 0) {
                return -1;
            }

            //start times were the same, so compare match ids
            return matchId.compareTo(otherMatch.matchId);
        }

        return 0;
    }
}
