package com.hatfat.dota.services;

import com.hatfat.dota.model.SteamUser;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.Matches;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/12/14.
 */
public class MatchHistoryFetcher
{
    public static void fetchMatches(final SteamUser user) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistory(user.getAccountId(), new Callback<MatchHistory>() {
            @Override
            public void success(MatchHistory matchHistory, Response response) {
                Matches.get().addMatches(matchHistory.getMatches());
                user.addMatches(matchHistory.getMatches());
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
