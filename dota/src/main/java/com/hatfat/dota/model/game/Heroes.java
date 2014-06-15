package com.hatfat.dota.model.game;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.services.HeroFetcher;
import com.hatfat.dota.util.FileUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/15/14.
 */
public class Heroes {

    private final static String HEROES_FILE_NAME = "heroes.json";

    public final static String HERO_DATA_LOADED_NOTIFICATION = "HERO_DATA_LOADED_NOTIFICATION";

    private boolean isLoadedFromNetwork;

    private static Heroes singleton;

    public static Heroes get() {
        if (singleton == null) {
            singleton = new Heroes();
        }

        return singleton;
    }

    private HashMap<String, Hero> heroes; //string heroId --> hero object

    private Heroes() {
        heroes = new HashMap();
    }

    public void load() {
        if (isLoadedFromNetwork) {
            broadcastHeroesLoaded();
        }
        else {
            //heroes not loaded from network, so lets try to
            fetch();
        }
    }

    public boolean isLoaded() {
        return heroes.size() > 0;
    }

    private void setNewHeroList(List<Hero> heroList) {
        heroes.clear();

        for (Hero hero : heroList) {
            heroes.put(String.valueOf(hero.heroId), hero);
        }

        broadcastHeroesLoaded();
    }

    public Hero getHero(String heroIdString) {
        return heroes.get(heroIdString);
    }

    private void broadcastHeroesLoaded() {
        Intent intent = new Intent(HERO_DATA_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private void fetch() {
        HeroFetcher.fetchHeroes(new Callback<HeroData>() {
            @Override
            public void success(HeroData heroData, Response response) {
                setNewHeroList(heroData.heroes);
                isLoadedFromNetwork = true;

                saveToDisk(); //update the disk cache for the hero data
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                loadFromDisk(); //try to load old heroes from disk
            }
        });
    }

    private void saveToDisk() {
        LinkedList heroesList = new LinkedList(heroes.values());
        HeroData obj = new HeroData();
        obj.heroes = heroesList;

        FileUtil.saveObjectToDisk(HEROES_FILE_NAME, obj);
    }

    private void loadFromDisk() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HeroData heroData = FileUtil.loadObjectFromDisk(HEROES_FILE_NAME, HeroData.class);

                if (heroData != null) {
                    setNewHeroList(heroData.heroes);
                }
                else {
                    //no heroes, but we finished loading...
                    broadcastHeroesLoaded();
                }

                return null;
            }
        }.execute();
    }
}
