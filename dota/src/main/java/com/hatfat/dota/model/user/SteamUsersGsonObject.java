package com.hatfat.dota.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 3/15/14.
 */
public class SteamUsersGsonObject {
    @SerializedName("users")
    List<SteamUser> users;
}
