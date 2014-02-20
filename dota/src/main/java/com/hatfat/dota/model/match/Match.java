package com.hatfat.dota.model.match;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;

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

    MatchResult matchResult;
    int duration;
    int towerStatusRadiant;
    int towerStatusDire;
    int barracksStatusRadiant;
    int barracksStatusDire;
    int cluster;
    int firstBloodTime;
    int humanPlayers;
    int leagueId;
    int positiveVotes;
    int negativeVotes;
    int gameMode;
    boolean hasMatchDetails;

    String matchId;
    long matchSeqNumber;
    long startTime; //seconds from 1970
    LobbyType lobbyType;

    List<Player> players;

    public Match() {
        matchResult = MatchResult.MATCH_RESULT_UNKNOWN;
    }

    public String getMatchId() {
        return matchId;
    }
    public MatchResult getMatchResult() {
        return matchResult;
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
        return lobbyType;
    }
    public String getLobbyTypeString() {
        return lobbyType.getLobbyTypeName();
    }
    public void setHasMatchDetails(boolean hasMatchDetails) {
        this.hasMatchDetails = hasMatchDetails;
    }

    public String toString() {
        return super.toString() + "[matchId: " + matchId + "]";
    }

    public String getTimeAgoString() {
        long currentTime = new Date().getTime() / 1000; //current time in seconds
        long timeAgo = currentTime - startTime;

        timeAgo /= 60; //minutes ago
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

    public Player getPlayerForSteamUser(SteamUser user) {
        for (Player player : players) {
            if (player.getAccountId() == user.getAccountIdLong()) {
                return player;
            }
        }

        return null;
    }

    void updateWithMatch(Match match) {
        if (match.hasMatchDetails) {
            matchResult = match.matchResult;
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
}
