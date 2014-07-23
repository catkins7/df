package com.hatfat.dota.services;

import android.util.Log;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.league.League;
import com.hatfat.dota.model.league.LiveLeagueList;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 7/23/14.
 */
public class LeagueFetcher {
    public static void fetchLiveLeagues(final Callback<List<League>> callback) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);

        charltonService.getLeagueListing(DotaFriendApplication.CONTEXT.getString(R.string.language_param), new Callback<DotaResult<LiveLeagueList>>() {
            @Override
            public void success(DotaResult<LiveLeagueList> leagueListResult, Response response) {
                List<League> leagues = leagueListResult.result.leagues;
                callback.success(leagues, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
}
