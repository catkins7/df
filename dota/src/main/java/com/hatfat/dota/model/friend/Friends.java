package com.hatfat.dota.model.friend;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hatfat.dota.DotaFriendApplication;

public class Friends {

    public final static String FRIENDS_LOADED_NOTIFICATION = "FRIENDS_LOADED_NOTIFICATION";

    public enum Friend {
        NO_FRIEND(0),
        CHARLTON(1),
        OGRE_MAGI(2);

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

    public void load() {
        if (isLoaded) {
            broadcastUsersLoadedFromDisk();
        }
        else {
            loadFromDisk();
        }
    }

    private void broadcastUsersLoadedFromDisk() {
        Intent intent = new Intent(FRIENDS_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private void loadFromDisk() {
        isLoaded = true;

        currentFriend = Friend.CHARLTON;

        broadcastUsersLoadedFromDisk();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

}
