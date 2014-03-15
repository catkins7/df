package com.hatfat.dota.model.match;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 *
 * The result of a GetMatchHistory request
 *
 */
public class MatchHistory {

    @SerializedName("status")
    int status;

    @SerializedName("num_results")
    int numResults;

    @SerializedName("total_results")
    int totalResults;

    @SerializedName("results_remaining")
    int resultsRemaining;

    @SerializedName("matches")
    List<Match> matches;

    public List<Match> getMatches() {
        return matches;
    }

    public int getResultsRemaining() {
        return resultsRemaining;
    }
}
