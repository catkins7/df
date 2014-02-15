package com.hatfat.dota.services;

import android.util.Log;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.HeroData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/15/14.
 */
public class HeroFetcher {
    public static void fetchHeroes(final Callback<HeroData> callback) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getHeroes(DotaFriendApplication.CONTEXT.getString(R.string.language_param), new Callback<HeroData>() {
            @Override
            public void success(HeroData heroData, Response response) {
                callback.success(heroData, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);
            }
        });
    }
}
