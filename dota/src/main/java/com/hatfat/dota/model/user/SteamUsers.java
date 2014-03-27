package com.hatfat.dota.model.user;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.services.SteamUserFetcher;
import com.hatfat.dota.util.FileUtil;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUsers {

    private final static String STARRED_USERS_FILE_NAME = "starredUsers.json";

    public final static String STEAM_STARRED_USERS_USER_LIST_CHANGED = "SteamUsers_StarredUsersListChanged_Notification";
    public final static String STEAM_USERS_LOADED_FROM_DISK = "STEAM_USERS_LOADED_FROM_DISK";

    private boolean isLoaded;

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
    }

    public void load() {
        if (isLoaded) {
            broadcastUsersLoadedFromDisk();
        }
        else {
            loadFromDisk();
        }
    }

    public Collection<SteamUser> getAllUsers() {
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

    private void saveToDisk() {
        LinkedList usersList = new LinkedList(starredUsers.values());
        SteamUsersGsonObject obj = new SteamUsersGsonObject();
        obj.users = usersList;

        FileUtil.saveObjectToDisk(STARRED_USERS_FILE_NAME, obj);
    }

    private void loadFromDisk() {
        SteamUsersGsonObject obj = FileUtil.loadObjectFromDisk(STARRED_USERS_FILE_NAME, SteamUsersGsonObject.class);

        if (obj != null) {
            addSteamUsers(obj.users);

            for (SteamUser user : obj.users) {
                starredUsers.put(user.steamId, user);
            }
        }

        { //add the Anonymous steam user
            String anonId = "76561202255233023";
            SteamUser anonUser = new SteamUser(anonId);
            anonUser.personaName = "Anonymous";
            anonUser.isFakeUser = true;
            this.users.put(anonUser.steamId, anonUser);
        }

        { //add the Bot steam user
            String botId = "76561197960265728";
            SteamUser botUser = new SteamUser(botId);
            botUser.personaName = "[Bot]";
            botUser.isFakeUser = true;
            this.users.put(botUser.steamId, botUser);
        }

        isLoaded = true;
        broadcastUsersLoadedFromDisk();
    }

    private void loadHackUsers() {
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

            starredUsers.put(newUser.steamId, newUser);
        }

        addSteamUsers(defaultUsers);
        fetchUsers(ids);
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
        for (SteamUser user : newUsers) {
            SteamUser existingUser = users.get(user.steamId);

            if (existingUser != null) {
                existingUser.updateWithSteamUser(user);
            }
            else {
                users.put(user.steamId, user);
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
            starredUsersChanged();
        }
    }

    public void removeSteamUserFromStarredList(SteamUser user) {
        SteamUser removedUser = starredUsers.remove(user.steamId);

        if (removedUser != null) {
            starredUsersChanged();
        }
    }

    private void starredUsersChanged() {
        //send broadcast
        Intent intent = new Intent(STEAM_STARRED_USERS_USER_LIST_CHANGED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);

        saveToDisk();
    }

    private void broadcastUsersLoadedFromDisk() {
        Intent intent = new Intent(STEAM_USERS_LOADED_FROM_DISK);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }
}
