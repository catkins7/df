package com.hatfat.dota.model.player;

import com.google.gson.annotations.SerializedName;

public class AbilityUpgrade {

    @SerializedName("ability")
    int abilityId;

    @SerializedName("time")
    int time;

    @SerializedName("level")
    int level;

    public int getAbilityId() {
        return abilityId;
    }

    public int getTime() {
        return time;
    }

    public int level() {
        return level;
    }
}
