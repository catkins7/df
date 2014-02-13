package com.hatfat.dota.services;

import android.util.Log;
import com.hatfat.dota.model.Match;
import com.hatfat.dota.model.MatchHistory;
import com.hatfat.dota.model.SteamUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class MatchHistoryFetcher
{
    public static void fetchMatches(SteamUser user, final Callback<List<Match>> matchHistoryCallback) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getMatchHistory(user.getDotaAccountId(), new Callback<MatchHistory>() {
            @Override
            public void success(MatchHistory matchHistory, Response response) {
                Log.e("catfat", "success!");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("catfat", "failure: " + retrofitError.getMessage());
            }
        });
    }
}
