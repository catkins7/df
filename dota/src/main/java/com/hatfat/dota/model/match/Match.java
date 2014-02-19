package com.hatfat.dota.model.match;

import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class Match {
    String matchId;
    long matchSeqNumber;
    long startTime; //seconds from 1970
    int lobbyType;

    List<Player> players;

    public String getMatchId() {
        return matchId;
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
