package com.hatfat.dota.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SteamUsersGsonObject {
    @SerializedName("users")
    List<SteamUser> users;
}
