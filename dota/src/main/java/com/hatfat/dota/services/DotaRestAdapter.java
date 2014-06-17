package com.hatfat.dota.services;

import com.hatfat.dota.model.DotaGson;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class DotaRestAdapter {

    public static RestAdapter createRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint("http://api.steampowered.com")
                .setConverter(new GsonConverter(DotaGson.getDotaGson()))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
    }
}
