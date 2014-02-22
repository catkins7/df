package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 2/15/14.
 */
public class HeroData {

    @SerializedName("heroes")
    public List<Hero> heroes;
}
