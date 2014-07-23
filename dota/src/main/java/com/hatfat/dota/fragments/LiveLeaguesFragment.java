package com.hatfat.dota.fragments;

import android.content.Context;

import com.hatfat.dota.R;
import com.hatfat.dota.model.league.League;
import com.hatfat.dota.services.LeagueFetcher;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 7/23/14.
 */
public class LiveLeaguesFragment extends CharltonFragment {

    @Override
    public void onResume() {
        super.onResume();

        LeagueFetcher.fetchLiveLeagues(new Callback<List<League>>() {
            @Override
            public void success(List<League> leagues, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public String getCharltonMessageText(Context context) {
        return context.getString(R.string.live_leagues_fragment_charlton_text);
    }
}
