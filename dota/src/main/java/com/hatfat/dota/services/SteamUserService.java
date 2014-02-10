package com.hatfat.dota.services;

import com.hatfat.dota.model.PlayerSummaries;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by scottrick on 2/10/14.
 */
public interface SteamUserService {

    final static String STEAM_DEV_KEY = "B3A7049A360B80408EEC3A9D97153AAF";

    @GET("/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_DEV_KEY)
    public void getPlayerSummaries(@Query("steamIds") String steamIdsFormattedString, Callback<PlayerSummaries> steamUserCallback);
}
