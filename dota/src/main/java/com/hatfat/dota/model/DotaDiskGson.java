package com.hatfat.dota.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchDiskAdapter;
import com.hatfat.dota.model.match.MatchesGsonObject;
import com.hatfat.dota.model.match.MatchesGsonObjectAdapter;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.player.PlayerDiskAdapter;

import java.util.HashMap;

public class DotaDiskGson {
    private static HashMap<Integer, Gson> gsonMap = new HashMap();

    public static Gson getDotaDiskGson(int version) {
        Gson gson = gsonMap.get(Integer.valueOf(version));

        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(MatchesGsonObject.class, new MatchesGsonObjectAdapter())
                    .registerTypeAdapter(Match.class, new MatchDiskAdapter())
                    .registerTypeAdapter(Player.class, new PlayerDiskAdapter(version))
                    .create();

            gsonMap.put(Integer.valueOf(version), gson);
        }

        return gson;
    }

    public static Gson getDefaultDotaDiskGson() {
        return getDotaDiskGson(1);
    }
}
