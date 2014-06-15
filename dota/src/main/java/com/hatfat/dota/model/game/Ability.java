package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

public class Ability {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    public boolean isStats() {
        return Integer.valueOf(id) == 5002;
    }

    public String getImageUrlString() {
        return "http://media.steampowered.com/apps/dota2/images/abilities/" + name + ".png";
    }
}
