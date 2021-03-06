package com.hatfat.dota.services;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.game.HeroData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HeroFetcher {
    public static void fetchHeroes(final Callback<HeroData> callback) {
        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
        charltonService.getHeroes(DotaFriendApplication.CONTEXT.getString(R.string.language_param), new Callback<DotaResult<HeroData>>() {
            @Override
            public void success(DotaResult<HeroData> result, Response response) {
                callback.success(result.result, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);
            }
        });
    }
}
