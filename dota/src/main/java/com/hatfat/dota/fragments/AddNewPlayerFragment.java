package com.hatfat.dota.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.services.CharltonService;
import com.hatfat.dota.services.DotaRestAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by scottrick on 2/12/14.
 */
public class AddNewPlayerFragment extends CharltonFragment {

    private HashSet<Match> matchSearchResults;
    private String searchString = "Merlini";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_player, null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        matchSearchResults = new HashSet<>();
        doPlayerSearch(searchString);
    }

    private void doPlayerSearch(final String playerName) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistoryByPlayerName(playerName, new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
                finishedSearchWithMatches(matchHistoryDotaResult.result, playerName);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void doPlayerSearchFromMatchId(final String playerName, final String matchId) {
        if (matchId == null) {
            doPlayerSearch(playerName);
            return;
        }

        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistoryByPlayerNameAtMatchId(playerName, matchId, new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
                finishedSearchWithMatches(matchHistoryDotaResult.result, playerName);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void finishedSearchWithMatches(MatchHistory matchHistory, String playerName) {
        matchSearchResults.addAll(matchHistory.getMatches());

        if (matchHistory.getResultsRemaining() > 0) {
            doPlayerSearchFromMatchId(playerName, matchHistory.getMatches().get(matchHistory.getMatches().size() - 1).getMatchId());
        }
        else {
            //no more matches to fetch!  we are done here...
            Log.e("catfat", "done fetching matches!!  We found: " + matchSearchResults.size());

            LinkedList<Match> matches = new LinkedList<>(matchSearchResults);

            //add the match search results to the matches singleton
            Matches.get().addMatches(matches);

            MatchListFragment matchListFragment = MatchListFragment.newInstance(matches);
            getCharltonActivity().pushCharltonFragment(matchListFragment);
        }
    }

    @Override
    public String getCharltonText() {
        return "Searching for '" + searchString + "'";
    }
}
