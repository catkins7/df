package com.hatfat.dota.services.dotabuff;

import com.hatfat.dota.model.DotaGson;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by scottrick on 3/15/14.
 */
public class DotaBuffRestAdapter {

    public static RestAdapter createRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint("http://dotabuff.com")
                .setConverter(new GsonConverter(DotaGson.getDotaGson()))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
    }
}
