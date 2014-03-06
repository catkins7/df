package com.hatfat.dota.model.user;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
    public final static String STEAM_STARRED_USERS_USER_LIST_CHANGED = "SteamUsers_StarredUsersListChanged_Notification";

    private static SteamUsers singleton;

    public static SteamUsers get() {
        if (singleton == null) {
            singleton = new SteamUsers();
        }

        return singleton;
    }

    private HashMap<String, SteamUser> users;
    private HashMap<String, SteamUser> starredUsers;

    private SteamUsers() {
        users = new HashMap<>();
        starredUsers = new HashMap<>();

        fetch();
    }

    public void init() {}

    private Collection<SteamUser> getUsers() {
        return users.values();
    }

    public Collection<SteamUser> getStarredUsers() {
        return starredUsers.values();
    }

    public SteamUser getBySteamId(String steamId) {
        if (users.containsKey(steamId)) {
            return users.get(steamId);
        }
        else {
            SteamUser newUser = new SteamUser(steamId);
            users.put(steamId, newUser);
            fetchUser(steamId);

            return newUser;
        }
    }

    public SteamUser getByAccountId(String accountId) {
        return getBySteamId(SteamUser.getSteamIdFromAccountId(accountId));
    }

    private void fetch() {
        LinkedList<String> ids = new LinkedList<>();
        ids.add("76561198020436232"); //scottrick
        ids.add("76561198040015660"); //joe
        ids.add("76561198019735326"); //mike
        ids.add("76561198053768056"); //kyle
        ids.add("76561197980883683"); //fatty
        ids.add("76561198000718505"); //bluth
        ids.add("76561198000376719"); //paul
        ids.add("76561197976570648"); //sarge

        LinkedList<SteamUser> defaultUsers = new LinkedList<>();
        for (String id : ids) {
            SteamUser newUser = new SteamUser(id);
            defaultUsers.add(newUser);

            addSteamUserToStarredList(newUser);
        }

        addSteamUsers(defaultUsers);
        fetchUsers(ids);

        //add the Anonymous steam user
        String anonId = "76561202255233023";
        SteamUser anonUser = new SteamUser(anonId);
        anonUser.personaName = "Anonymous";
        anonUser.isAnonymous = true;
        this.users.put(anonUser.steamId, anonUser);
    }

    private void fetchUser(String steamId) {
        SteamUserFetcher.getSteamUser(steamId, new Callback<SteamUser>() {
            @Override
            public void success(SteamUser steamUser, Response response) {
                if (steamUser != null) {
                    LinkedList<SteamUser> users = new LinkedList<>();
                    users.add(steamUser);
                    addSteamUsers(users);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void refreshUser(SteamUser user) {
        List<String> ids = new LinkedList<>();
        ids.add(user.getSteamId());

        fetchUsers(ids);
    }

    public void refreshUsers(List<SteamUser> users) {
        List<String> ids = new LinkedList<>();
        for (SteamUser user : users) {
            ids.add(user.getSteamId());
        }

        fetchUsers(ids);
    }

    private void fetchUsers(List<String> steamIds) {
        SteamUserFetcher.getSteamUsers(steamIds, new Callback<List<SteamUser>>() {
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
    }

    public boolean isUserStarred(SteamUser user) {
        return starredUsers.containsKey(user.steamId);
    }

    public void addSteamUserToStarredList(SteamUser user) {
        SteamUser existingUser = starredUsers.get(user.steamId);

        if (existingUser == null) {
            starredUsers.put(user.steamId, user);
            broadcastStarredUsersChanged();
        }
    }

    public void removeSteamUserFromStarredList(SteamUser user) {
        SteamUser removedUser = starredUsers.remove(user.steamId);

        if (removedUser != null) {
            broadcastStarredUsersChanged();
        }
    }

    private void broadcastStarredUsersChanged() {
        Intent intent = new Intent(STEAM_STARRED_USERS_USER_LIST_CHANGED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }
}
