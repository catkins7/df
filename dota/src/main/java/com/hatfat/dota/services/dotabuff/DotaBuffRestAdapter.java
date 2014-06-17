package com.hatfat.dota.services.dotabuff;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class DotaBuffRestAdapter {
    public static RestAdapter createRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint("http://dotabuff.com")
                .setConverter(new DotaBuffConverter())
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
    }
}
