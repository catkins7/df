package com.hatfat.dota;

import android.app.Application;
import android.content.Context;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaFriendApplication extends Application {
    public static Context CONTEXT;

    @Override public void onCreate() {
        CONTEXT = getApplicationContext();
    }
}
