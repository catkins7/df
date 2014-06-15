package com.hatfat.dota.model.game;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaGson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

public class Abilities {
    //abilities json downloaded from here:
    //https://github.com/kronusme/dota2-api/blob/master/data/abilities.json
    private final static String ABILITY_FILE_NAME = "abilities.json";

    public final static String ABILITY_DATA_LOADED_NOTIFICATION = "ABILITY_DATA_LOADED_NOTIFICATION";

    private static Abilities singleton;

    public static Abilities get() {
        if (singleton == null) {
            singleton = new Abilities();
        }

        return singleton;
    }

    private HashMap<String, Ability> abilities; //string abilityId --> ability object

    private Abilities() {
        abilities = new HashMap();
    }

    public void load(Resources resources) {
        if (abilities != null && abilities.size() > 0) {
            //already loaded
            return;
        }

        //abilities are not available from the API, so parse local abilities.json file
        InputStream inputStream = resources.openRawResource(R.raw.abilities);
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = DotaGson.getDotaGson();
        AbilityData abilityData = gson.fromJson(reader, AbilityData.class);

        setNewAbilityList(abilityData.abilities);
    }

    private void setNewAbilityList(List<Ability> abilityList) {
        abilities.clear();

        for (Ability ability : abilityList) {
            abilities.put(ability.id, ability);
        }

        broadcastAbilitiesLoaded();
    }

    public Ability getAbility(String abilityId) {
        return abilities.get(abilityId);
    }

    private void broadcastAbilitiesLoaded() {
        Intent intent = new Intent(ABILITY_DATA_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }
}
