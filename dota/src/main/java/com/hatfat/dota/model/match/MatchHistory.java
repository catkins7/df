package com.hatfat.dota.model.match;

import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 *
 * The result of a GetMatchHistory request
 *
 */
public class MatchHistory {
    int status;
    int numResults;
    int totalResults;
    int resultsRemaining;

    List<Match> matches;

    public List<Match> getMatches() {
        return matches;
    }
}
