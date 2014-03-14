package com.hatfat.dota.model.match;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by scottrick on 2/14/14.
 */
public class Matches {

    private static Matches singleton;

    private HashMap<String, Match> matches; //matchId --> match

    public static Matches get() {
        if (singleton == null) {
            singleton = new Matches();
        }

        return singleton;
    }

    private Matches() {
        matches = new HashMap<>();

        loadFromDisk();
    }

    public void init() {}

    private void loadFromDisk() {

    }

    public void addMatch(Match match) {
        if (match == null) {
            return;
        }

        if (matches.containsKey(match.matchId)) {
            matches.get(match.matchId).updateWithMatch(match);
        }
        else {
            matches.put(match.matchId, match);
        }
    }

    public void addMatches(List<Match> newMatches) {
        if (newMatches == null) {
            return;
        }

        for (Match match : newMatches) {
            addMatch(match);
        }
    }

    public Match getMatch(String matchId) {
        return matches.get(matchId);
    }

    public Collection<Match> getAllMatches() {
        return matches.values();
    }
}
