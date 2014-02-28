package com.hatfat.dota.services;

import com.hatfat.dota.model.DotaGson;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaRestAdapter {

    public static RestAdapter createRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint("http://api.steampowered.com")
                .setConverter(new GsonConverter(DotaGson.createGson()))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
    }
}
