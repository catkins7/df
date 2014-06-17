package com.hatfat.dota.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DotaGson {
    private static Gson gson;

    public static Gson getDotaGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .create();
        }

        return gson;
    }
}
