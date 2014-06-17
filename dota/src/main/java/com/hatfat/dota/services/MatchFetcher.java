package com.hatfat.dota.services;

import android.util.Log;

import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;

import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MatchFetcher
{
    public static void fetchMatches(final SteamUser user, final Callback<List<Match>> callback) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistory(user.getAccountId(), new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> result, Response response) {
                MatchHistory matchHistory = result.result;
                List<Match> matches;

                boolean onlyAddRecentTwentyMatches = true;

                if (onlyAddRecentTwentyMatches) {
                    //we only want to add their last 20 games
                    int numberToGet = Math.min(matchHistory.getMatches().size(), 20);
                    matches = new LinkedList(matchHistory.getMatches().subList(0, numberToGet));
                }
                else {
                    matches = matchHistory.getMatches();
                }

                Matches.get().addMatches(matches);
                user.addMatches(matches);

                callback.success(matches, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);

                Log.e("MatchFetcher", "" + retrofitError.getMessage());
            }
        });
    }

    public static void fetchMatchDetails(final String matchId) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchDetails(matchId, new Callback<DotaResult<Match>>() {
            @Override
            public void success(DotaResult<Match> result, Response response) {
                Match match = result.result;
                match.setHasMatchDetails(true);

                Matches.get().addMatch(match);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MatchFetcher", "" + retrofitError.getMessage());
            }
        });
    }
}
