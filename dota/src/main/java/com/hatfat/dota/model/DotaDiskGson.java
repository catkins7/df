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
    private static Gson gson;

    public static Gson getDotaDiskGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(MatchesGsonObject.class, new MatchesGsonObjectAdapter())
                    .registerTypeAdapter(Match.class, new MatchDiskAdapter())
                    .registerTypeAdapter(Player.class, new PlayerDiskAdapter())
                    .create();
        }

        return gson;
    }
}
