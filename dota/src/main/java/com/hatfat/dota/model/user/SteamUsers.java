package com.hatfat.dota.model.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.services.SteamUserFetcher;
import com.hatfat.dota.util.FileUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SteamUsers {

    public final static String ANONYMOUS_ID = "76561202255233023";
    public final static String BOT_ID = "76561197960265728";

    public final static String STEAM_STARRED_USERS_USER_LIST_CHANGED = "SteamUsers_StarredUsersListChanged_Notification";
    public final static String STEAM_USERS_LOADED_FROM_DISK = "STEAM_USERS_LOADED_FROM_DISK";

    private final static String STARRED_USERS_FILE_NAME = "starredUsers.json";

    private boolean isLoaded;

    private static SteamUsers singleton;

    public static SteamUsers get() {
        if (singleton == null) {
            singleton = new SteamUsers();
        }

        return singleton;
    }

    private HashMap<String, SteamUser> users; //master map of steamIds to the SteamUser objects
    private List<String> starredUsers; //starred users steamIds

    private SteamUsers() {
        users = new HashMap();
        starredUsers = new LinkedList();
    }

    public void load(Resources resources) {
        if (isLoaded) {
            broadcastUsersLoadedFromDisk();
        }
        else {
            loadFromDisk(resources);
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public Collection<SteamUser> getAllUsers() {
        return users.values();
    }

    public Collection<SteamUser> getStarredUsers() {
        LinkedList<SteamUser> stars = new LinkedList<>();
        for (String steamId : starredUsers) {
            SteamUser star = getBySteamId(steamId);
            stars.add(star);
        }

        return stars;
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

    //same as above, but it doesn't fetch the user if it doesn't exist
    public SteamUser getExistingUserBySteamId(String steamId) {
        return users.get(steamId);
    }

    public SteamUser getExistingUserByAccountId(String accountId) {
        return getExistingUserBySteamId(SteamUser.getSteamIdFromAccountId(accountId));
    }

    public SteamUser getByAccountId(String accountId) {
        return getBySteamId(SteamUser.getSteamIdFromAccountId(accountId));
    }

    public void requestStarredUsersSave() {
        saveToDisk();
    }

    private void saveToDisk() {
        LinkedList<SteamUser> usersList = new LinkedList<>();
        for (String userId : starredUsers) {
            SteamUser user = getBySteamId(userId);
            usersList.add(user);
        }

        SteamUsersGsonObject obj = new SteamUsersGsonObject();
        obj.users = usersList;

        FileUtil.saveObjectToDisk(STARRED_USERS_FILE_NAME, obj);
    }

    private void loadFromDisk(Resources resources) {
        SteamUsersGsonObject obj = FileUtil.loadObjectFromDisk(STARRED_USERS_FILE_NAME, SteamUsersGsonObject.class);

        if (obj != null) {
            addSteamUsers(obj.users);

            for (SteamUser user : obj.users) {
                starredUsers.add(user.getSteamId());
            }
        }

        { //add the Anonymous steam user
            SteamUser anonUser = new SteamUser(ANONYMOUS_ID);
            anonUser.personaName = resources.getString(R.string.anonymous_name);
            anonUser.isFakeUser = true;
            this.users.put(anonUser.steamId, anonUser);
        }

        { //add the Bot steam user
            SteamUser botUser = new SteamUser(BOT_ID);
            botUser.personaName = resources.getString(R.string.bot_name);
            botUser.isFakeUser = true;
            this.users.put(botUser.steamId, botUser);
        }

        //delete any old match files that are still floating around
        File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();
        fileDir.mkdirs();

        for (File file : fileDir.listFiles()) {
            if (shouldFileBeDeleted(file)) {
                if (!file.delete()) {
                    Log.v(getClass().getSimpleName(), "Unable to delete matches file " + file.getName());
                }
            }
        }

        isLoaded = true;
        broadcastUsersLoadedFromDisk();
    }

    private boolean shouldFileBeDeleted(File file) {
        String fileName = file.getName();
        boolean isMatchesFile = fileName.contains(Matches.USER_MATCHES_FILE_EXTENSION);

        if (!isMatchesFile) {
            return false;
        }

        int index = fileName.indexOf(Matches.USER_MATCHES_FILE_EXTENSION);
        String matchesUserId = fileName.substring(0, index);

        SteamUser user = getByAccountId(matchesUserId);

        return !isUserStarred(user);
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
        if (!user.isRealUser()) {
            return;
        }

        List<String> ids = new LinkedList<>();
        ids.add(user.getSteamId());

        fetchUsers(ids);
    }

    public void refreshUsers(List<SteamUser> users) {
        List<String> ids = new LinkedList<>();
        for (SteamUser user : users) {
            if (user.isRealUser()) {
                ids.add(user.getSteamId());
            }
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

    public void toggleStarForUser(final SteamUser user, final Context context) {
        if (isUserStarred(user)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.player_summary_remove_friend_title_text);
            builder.setMessage(R.string.player_summary_remove_friend_message_text);
            builder.setPositiveButton(R.string.player_summary_remove_friend_remove_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    removeSteamUserFromStarredList(user);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            addSteamUserToStarredList(user);

            Toast.makeText(context, R.string.player_summary_added_friend_text, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isUserStarred(SteamUser user) {
        return starredUsers.contains(user.steamId);
    }

    public void addSteamUserToStarredList(SteamUser user) {
        if (!starredUsers.contains(user.steamId)) {
            starredUsers.add(user.getSteamId());
            starredUsersChanged();
        }
    }

    public void removeSteamUserFromStarredList(SteamUser user) {
        if (starredUsers.contains(user.steamId)) {
            starredUsers.remove(user.steamId);
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
