package com.hatfat.dota.model.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.hatfat.dota.DotaFriendApplication;

import java.util.Random;

public class Friends {

    private final static String FRIENDS_PREF_FILE = "FRIENDS_PREF_FILE";
    private final static String SELECTED_FRIEND_KEY = "SELECTED_FRIEND_KEY";

    public final static String FRIENDS_LOADED_NOTIFICATION = "FRIENDS_LOADED_NOTIFICATION";

    public enum Friend {
        NO_FRIEND(0),
        CHARLTON(1),

        OGRE_MAGI(2),
        ENCHANTRESS(3);

        public static final int numberOfRandomableFriends = 2;
        private int type;

        Friend(int type) {
            this.type = type;
        }

        public static Friend fromInt(int type) {
            switch (type) {
                case 0:
                    return NO_FRIEND;
                case 1:
                    return CHARLTON;
                case 2:
                    return OGRE_MAGI;
                case 3:
                    return ENCHANTRESS;
                default:
                    return NO_FRIEND;
            }
        }

        public int getType() {
            return type;
        }
    }

    private boolean isLoaded;

    private Friend currentFriend;

    private static Friends singleton;

    public static Friends get() {
        if (singleton == null) {
            singleton = new Friends();
        }

        return singleton;
    }

    private Friends() {

    }

    public Friend getCurrentFriend() {
        return currentFriend;
    }

    public void load(Context context) {
        if (isLoaded) {
            broadcastUsersLoadedFromDisk();
        }
        else {
            loadFromDisk(context);
        }
    }

    private void broadcastUsersLoadedFromDisk() {
        Intent intent = new Intent(FRIENDS_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private void loadFromDisk(Context context) {
        isLoaded = true;

        SharedPreferences settings = context.getSharedPreferences(FRIENDS_PREF_FILE, 0);
        int friendType = settings.getInt(SELECTED_FRIEND_KEY, Friend.NO_FRIEND.getType());

        currentFriend = Friend.fromInt(friendType);

        if (currentFriend == Friend.NO_FRIEND) {
            //no friend selected yet!  lets select a random one
            Random rand = new Random();
            int randomNum = rand.nextInt(Friend.numberOfRandomableFriends) + 2;
            currentFriend = Friend.fromInt(randomNum);

            saveFriendSelection(context);
        }

        broadcastUsersLoadedFromDisk();
    }

    private void saveFriendSelection(Context context) {
        SharedPreferences settings = context.getSharedPreferences(FRIENDS_PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(SELECTED_FRIEND_KEY, currentFriend.getType());
        editor.commit();
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
