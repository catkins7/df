package com.hatfat.dota.model.player;

import com.google.gson.annotations.SerializedName;
import com.hatfat.dota.model.user.SteamUser;

import java.util.List;

/**
 * Created by scottrick on 2/10/14.
 *
 * wrapper for the ISteamUser/GetPlayerSummaries request response
 */
public class PlayerSummaries {

    @SerializedName("players")
    List<SteamUser> users;

    public List<SteamUser> getUsers() {
        return users;
    }
}
