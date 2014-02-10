package com.hatfat.dota.services;

import com.hatfat.dota.model.PlayerSummaries;
import com.hatfat.dota.model.SteamUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUserFetcher
{
    public static void getSteamUsers(List<String> steamIds, final Callback<List<SteamUser>> steamUserCallback) {
        String ids = "";
        for (int i = 0; i < steamIds.size(); i++) {
            String id = steamIds.get(i);
            ids += id;

            if (i < steamIds.size() - 1) {
                //not the last one, so lets add a comma
                ids += ",";
            }
        }

        SteamUserService steamUserService = DotaRestAdapter.createRestAdapter().create(SteamUserService.class);
        steamUserService.getPlayerSummaries(ids, new Callback<PlayerSummaries>() {
            @Override
            public void success(PlayerSummaries playerSummaries, Response response) {
                steamUserCallback.success(playerSummaries.getUsers(), response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                steamUserCallback.failure(retrofitError);
            }
        });
    }

    public static void getSteamUser(String steamId, final Callback<SteamUser> steamUserCallback) {
        SteamUserService steamUserService = DotaRestAdapter.createRestAdapter().create(SteamUserService.class);
        steamUserService.getPlayerSummaries(steamId, new Callback<PlayerSummaries>() {
            @Override
            public void success(PlayerSummaries playerSummaries, Response response) {
                if (playerSummaries.getUsers().size() > 0) {
                    steamUserCallback.success(playerSummaries.getUsers().get(0), response);
                }
                else {
                    steamUserCallback.success(null, response);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                steamUserCallback.failure(retrofitError);
            }
        });
    }
}
