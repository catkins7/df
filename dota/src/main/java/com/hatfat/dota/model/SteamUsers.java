package com.hatfat.dota.model;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.services.SteamUserFetcher;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUsers {
    public final static String STEAM_USERS_NEW_USERS_UPDATED = "SteamUsers_NewUsers_Notification";

    private static SteamUsers singleton;

    public static SteamUsers get() {
        if (singleton == null) {
            singleton = new SteamUsers();
        }

        return singleton;
    }

    private HashMap<String, SteamUser> users;

    public SteamUsers() {
        users = new HashMap<>();

        fetch();
    }

    public void init() {}

    public Collection<SteamUser> getUsers() {
        return users.values();
    }

    private void fetch() {
        LinkedList<String> ids = new LinkedList<>();
        ids.add("76561198020436232"); //scottrick
        ids.add("76561198040015660"); //joe
        ids.add("76561198019735326"); //mike
        ids.add("76561198053768056"); //kyle
        ids.add("76561197980883683"); //fatty

        SteamUserFetcher.getSteamUsers(ids, new Callback<List<SteamUser>>() {
            @Override
            public void success(List<SteamUser> steamUsers, Response response) {
                addSteamUsers(steamUsers);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void addSteamUsers(List<SteamUser> newUsers) {
        boolean addedNewUser = false;

        Log.e("catfat", "addSteamUsers: " + newUsers.size() + " users");

        for (SteamUser user : newUsers) {
            SteamUser existingUser = users.get(user.steamId);

            if (existingUser != null) {
                existingUser.updateWithSteamUser(user);
            }
            else {
                users.put(user.steamId, user);
                addedNewUser = true;
            }
        }

        if (addedNewUser) {
            broadcastUsersChanged();
        }
    }

    private void broadcastUsersChanged() {
        Intent intent = new Intent(STEAM_USERS_NEW_USERS_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }
}
