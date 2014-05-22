package com.hatfat.dota.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchDiskAdapter;
import com.hatfat.dota.model.match.MatchesGsonObject;
import com.hatfat.dota.model.match.MatchesGsonObjectAdapter;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.player.PlayerDiskAdapter;

public class DotaDiskGson {
    private static Gson gsonVersion1;
    private static Gson gsonVersion2;

    public static Gson getDotaDiskGsonVersion1() {
        if (gsonVersion1 == null) {
            gsonVersion1 = new GsonBuilder()
                    .registerTypeAdapter(MatchesGsonObject.class, new MatchesGsonObjectAdapter())
                    .registerTypeAdapter(Match.class, new MatchDiskAdapter())
                    .registerTypeAdapter(Player.class, new PlayerDiskAdapter())
                    .create();
        }

        return gsonVersion1;
    }

    public static Gson getDotaDiskGsonVersion2() {
        if (gsonVersion2 == null) {
            gsonVersion2 = new GsonBuilder()
                    .registerTypeAdapter(MatchesGsonObject.class, new MatchesGsonObjectAdapter())
                    .registerTypeAdapter(Match.class, new MatchDiskAdapter())
                    .registerTypeAdapter(Player.class, new PlayerDiskAdapter())
                    .create();
        }

        return gsonVersion2;
    }
}
