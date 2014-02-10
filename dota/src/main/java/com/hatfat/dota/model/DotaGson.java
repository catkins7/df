package com.hatfat.dota.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaGson {
    private static Gson gson;

    public static Gson createGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(PlayerSummaries.class, new PlayerSummariesAdapter())
                    .registerTypeAdapter(SteamUser.class, new SteamUserAdapter())
                    .create();
        }

        return gson;
    }

}
