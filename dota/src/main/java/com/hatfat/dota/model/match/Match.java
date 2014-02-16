package com.hatfat.dota.model.match;

import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;

import java.util.Comparator;
import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class Match {
    String matchId;
    long matchSeqNumber;
    long startTime;
    int lobbyType;

    List<Player> players;

    public String getMatchId() {
        return matchId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String toString() {
        return super.toString() + "[matchId: " + matchId + "]";
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
