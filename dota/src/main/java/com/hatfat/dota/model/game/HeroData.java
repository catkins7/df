package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeroData {
    @SerializedName("heroes")
    List<Hero> heroes;
}
