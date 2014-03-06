package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scottrick on 3/5/14.
 */
public class Item {
    private static final String baseItemIconUrl = "http://media.steampowered.com/apps/dota2/images/items/";
    private static final String largeHorizontalSuffix = "_lg.png";

    @SerializedName("name")
    String name;

    @SerializedName("id")
    int itemId;

    private String getBaseUrlString() {
        return baseItemIconUrl + name.substring(5);
    }

    public String getLargeHorizontalPortraitUrl() {
        return getBaseUrlString() + largeHorizontalSuffix;
    }
}
