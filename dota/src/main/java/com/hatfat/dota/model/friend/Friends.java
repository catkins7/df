package com.hatfat.dota.model.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.CharltonActivity;
import com.hatfat.dota.model.DotaGson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
public class Friends {

    private final static String FRIENDS_PREF_FILE   = "FRIENDS_PREF_FILE";
    private final static String SELECTED_FRIEND_PREFS_KEY = "CURRENT_FRIEND_KEY";

    public final static String FRIENDS_LOADED_NOTIFICATION = "FRIENDS_LOADED_NOTIFICATION";
    public final static String CURRENT_FRIEND_CHANGED_NOTIFICATION = "CURRENT_FRIEND_CHANGED_NOTIFICATION";

    private HashMap<String, Friend> friends; //string friendId --> friend object

    private Friend currentFriend;
    private Friend dummyFriend;

    private static Friends singleton;

    public static Friends get() {
        if (singleton == null) {
            singleton = new Friends();
        }

        return singleton;
    }

    private Friends() {
        friends = new HashMap();

        dummyFriend = new Friend();
    }

    public void load(Context context) {
        if (friends != null && friends.size() > 0) {
            //already loaded
            return;
        }

        Resources resources = context.getResources();

        //parse local json file
        InputStream inputStream = resources.openRawResource(R.raw.friends);
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = DotaGson.getDotaGson();
        FriendData friendData = gson.fromJson(reader, FriendData.class);

        setNewFriendData(friendData);

        SharedPreferences settings = context.getSharedPreferences(FRIENDS_PREF_FILE, 0);
        String currentFriendId = settings.getString(SELECTED_FRIEND_PREFS_KEY, null);

        currentFriend = getFriend(currentFriendId);

        if (currentFriend == null) {
            //no friend selected yet!  lets select a random one
            List<Friend> randomableFriends = getRandomableFriendsList();

            if (randomableFriends.size() <= 0) {
                randomableFriends = getSortedFriendsList();
            }

            int randomFriendIndex = CharltonActivity.getSharedRandom().nextInt(randomableFriends.size());
            currentFriend = randomableFriends.get(randomFriendIndex);
            saveFriendSelection(context);
        }

        broadcastFriendsLoadedFromDisk(context);
    }

    private void setNewFriendData(FriendData data) {
        friends.clear();

        for (Friend friend : data.friends) {
            friends.put(friend.getFriendId(), friend)
            ;
        }
    }

    public Friend getFriend(String friendId) {
        return friends.get(friendId);
    }

    private void broadcastFriendsLoadedFromDisk(Context context) {
        Intent intent = new Intent(FRIENDS_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void broadcastCurrentFriendChanged(Context context) {
        Intent intent = new Intent(CURRENT_FRIEND_CHANGED_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public Friend getCurrentFriend() {
        if (currentFriend != null) {
            return currentFriend;
        }
        else {
            return dummyFriend;
        }
    }

    public List<Friend> getSortedFriendsList() {
        List<Friend> friendsList = new LinkedList(friends.values());
        Collections.sort(friendsList);

        return friendsList;
    }

    public List<Friend> getRandomableFriendsList() {
        List<Friend> randomableFriends = new LinkedList();

        for (Friend friend : friends.values()) {
            if (friend.canBeRandomed()) {
                randomableFriends.add(friend);
            }
        }

        return randomableFriends;
    }

    public void selectNewFriend(Friend newFriend, Context context) {
        if (((Object)newFriend).equals(currentFriend)) {
            return;
        }

        currentFriend = newFriend;
        saveFriendSelection(context);
    }

    private void saveFriendSelection(Context context) {
        SharedPreferences settings = context.getSharedPreferences(FRIENDS_PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(SELECTED_FRIEND_PREFS_KEY, currentFriend.getFriendId());
        editor.commit();

        broadcastCurrentFriendChanged(context);
    }
}
