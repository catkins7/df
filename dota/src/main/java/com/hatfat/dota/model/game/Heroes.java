package com.hatfat.dota.model.game;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.services.HeroFetcher;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashMap;

/**
 * Created by scottrick on 2/15/14.
 */
public class Heroes {

    public final static String HERO_DATA_UPDATED_NOTIFICATION = "HeroDataUpdatedNotification";

    private static Heroes singleton;

    public static Heroes get() {
        if (singleton == null) {
            singleton = new Heroes();
        }

        return singleton;
    }

    private HashMap<String, Hero> heroes; //string heroId --> hero object

    private Heroes() {
        heroes = new HashMap<>();

        fetch();
    }

    public void init() {}

    private void setHeroData(HashMap<String, Hero> heroData) {
        heroes = heroData;

        broadcastUsersChanged();
    }

    public Hero getHero(String heroIdString) {
        return heroes.get(heroIdString);
    }

    private void broadcastUsersChanged() {
        Intent intent = new Intent(HERO_DATA_UPDATED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private void fetch() {
        HeroFetcher.fetchHeroes(new Callback<HeroData>() {
            @Override
            public void success(HeroData heroData, Response response) {
                setHeroData(heroData.heroes);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
