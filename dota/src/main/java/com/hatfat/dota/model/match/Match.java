package com.hatfat.dota.model.match;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.gson.annotations.SerializedName;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.services.MatchFetcher;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class Match {

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

        public int getColorResourceId() {
            switch (this) {
                case MATCH_RESULT_RADIANT_VICTORY:
                    return R.color.radiant_green;
                case MATCH_RESULT_DIRE_VICTORY:
                    return R.color.dire_red;
                case MATCH_RESULT_UNKNOWN:
                default:
                    return R.color.off_white;
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
        PUBLIC_MATCHMAKING("Unranked"),
        PRACTICE("Practice"),
        TOURNAMENT("Tournament"),
        TUTORIAL("Tutorial"),
        CO_OP_BOTS("Co-op Bots"),
        TEAM_MATCH("Team Matchmaking"),
        SOLO_QUEUE("Solo Queue"),
        RANKED("Ranked"),
        UNKNOWN("Unknown");

        private String lobbyTypeName;

        LobbyType(String typeName) {
            this.lobbyTypeName = typeName;
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
                default:
                    return UNKNOWN;
            }
        }
        public String getLobbyTypeName() {
            return lobbyTypeName;
        }
    }

    public enum GameMode {
        None("None"),
        AllPick("All Pick"),
        CaptainsMode("Captains Mode"),
        RandomDraft("Random Draft"),
        SingleDraft("Single Draft"),
        AllRandom("All Random"),
        IntroDeath("-"),
        Diretide("Diretide"),
        ReverseCaptainsMode("Reverse CM"),
        Greeviling("Greeviling"),
        Tutorial("Tutorial"),
        MidOnly("Mid Only"),
        LeastPlayed("Least Played"),
        LimitedHeroes("Limited Heroes"),
        FH("FH"),
        CustomGame("Custom Game"),
        CaptainsDraft("Captains Draft"),
        BalancedDraft("Balanced Draft"),
        AbilityDraft("Ability Draft"),
        Unknown("");

        private String gameModeName;

        GameMode(String modeName) {
            this.gameModeName = modeName;
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
                    return FH;
                case 15:
                    return CustomGame;
                case 16:
                    return CaptainsDraft;
                case 17:
                    return BalancedDraft;
                case 18:
                    return AbilityDraft;
                default:
                    return Unknown;
            }
        }
        public String getGameModeName() {
            return gameModeName;
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

    public Match(String matchId) {
        this.matchId = matchId;
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
    public String getLobbyTypeString() {
        return getLobbyType().getLobbyTypeName();
    }
    public GameMode getGameMode() {
        if (!hasMatchDetails) {
            return GameMode.Unknown;
        }

        return GameMode.fromInt(gameMode);
    }
    public String getGameModeString() {
        if (!hasMatchDetails) {
            return GameMode.Unknown.getGameModeName();
        }

        return GameMode.fromInt(gameMode).getGameModeName();
    }
    public String getLobbyModeString() {
        if (getLobbyType() == LobbyType.PUBLIC_MATCHMAKING) {
            return getGameModeString();
        }
        else {
            return getGameModeString() + " (" + getLobbyTypeString() + ")";
        }
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
        if (!players.contains(player)) {
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

    public int getMatchResultColorResourceIdForPlayer(Player player) {
        PlayerMatchResult result = getPlayerMatchResultForPlayer(player);

        switch (result) {
            case PLAYER_MATCH_RESULT_VICTORY:
                return R.color.radiant_green;
            case PLAYER_MATCH_RESULT_DEFEAT:
                return R.color.dire_red;
            default:
                return R.color.off_white;
        }
    }

    public String getTimeAgoString() {
        long currentTime = new Date().getTime() / 1000; //current time in seconds
        long timeAgo = currentTime - startTime;

        timeAgo /= 60; //minutes ago

        if (timeAgo == 1){
            return String.valueOf(timeAgo) + " minute ago";
        }
        else if (timeAgo < 60) {
            return String.valueOf(timeAgo) + " minutes ago";
        }

        timeAgo /= 60; // hours ago

        if (timeAgo >= 24) {
            timeAgo /= 24; //days ago

            if (timeAgo == 1) {
                return String.valueOf(timeAgo) + " day ago";
            }
            else {
                return String.valueOf(timeAgo) + " days ago";
            }
        }
        if (timeAgo == 1) {
            return String.valueOf(timeAgo) + " hour ago";
        }
        else {
            return String.valueOf(timeAgo) + " hours ago";
        }
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
        if (match.hasMatchDetails) {
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

        matchId = match.matchId;
        matchSeqNumber = match.matchSeqNumber;
        startTime = match.startTime;
        lobbyType = match.lobbyType;

        if (players == null) {
            players = match.players;
        }

        broadcastMatchChanged();
    }

    public void getMatchDetailsIfNeeded() {
        if (!hasMatchDetails) {
            MatchFetcher.fetchMatchDetails(getMatchId());
        }
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
                    return matchId2.compareToIgnoreCase(matchId);
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
}
