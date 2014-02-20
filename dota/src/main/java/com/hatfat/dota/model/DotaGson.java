package com.hatfat.dota.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hatfat.dota.model.game.Hero;
import com.hatfat.dota.model.game.HeroAdapter;
import com.hatfat.dota.model.game.HeroData;
import com.hatfat.dota.model.game.HeroDataAdapter;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchAdapter;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.MatchHistoryAdapter;
import com.hatfat.dota.model.player.PlayerSummaries;
import com.hatfat.dota.model.player.PlayerSummariesAdapter;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUserAdapter;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaGson {
    private static Gson gson;

    public static Gson createGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(HeroData.class, new HeroDataAdapter())
                    .registerTypeAdapter(Hero.class, new HeroAdapter())
                    .registerTypeAdapter(MatchHistory.class, new MatchHistoryAdapter())
                    .registerTypeAdapter(PlayerSummaries.class, new PlayerSummariesAdapter())
                    .registerTypeAdapter(SteamUser.class, new SteamUserAdapter())
                    .registerTypeAdapter(Match.class, new MatchAdapter())
                    .create();
        }

        return gson;
    }

}
