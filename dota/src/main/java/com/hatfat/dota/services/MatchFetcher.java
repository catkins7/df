package com.hatfat.dota.services;

import android.util.Log;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/12/14.
 */
public class MatchFetcher
{
    public static void fetchMatches(final SteamUser user) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistory(user.getAccountId(), new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> result, Response response) {
                MatchHistory matchHistory = result.result;

                Matches.get().addMatches(matchHistory.getMatches());
                user.addMatches(matchHistory.getMatches());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MatchFetcher", retrofitError.getMessage());
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
                Log.e("MatchFetcher", retrofitError.getMessage());
            }
        });
    }
}
