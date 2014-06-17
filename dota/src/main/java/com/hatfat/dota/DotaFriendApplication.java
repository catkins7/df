package com.hatfat.dota;

import android.app.Application;
import android.content.Context;

public class DotaFriendApplication extends Application {
    public static Context CONTEXT;

    @Override public void onCreate() {
        CONTEXT = getApplicationContext();
    }
}
