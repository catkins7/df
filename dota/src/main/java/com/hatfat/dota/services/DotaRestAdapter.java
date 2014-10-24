package com.hatfat.dota.services;

import com.hatfat.dota.model.DotaGson;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class DotaRestAdapter {

    public static RestAdapter createRestAdapter() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        httpClient.setReadTimeout(12, TimeUnit.SECONDS);

        return new RestAdapter.Builder()
                .setClient(new OkClient(httpClient))
                .setEndpoint("http://api.steampowered.com")
                .setConverter(new GsonConverter(DotaGson.getDotaGson()))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
    }
}
