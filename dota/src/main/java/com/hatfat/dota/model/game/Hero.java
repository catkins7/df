package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

public class Hero {
    private static final String tinyHorizontalSuffix = "_eb.png"; //doesn't seem to exist, but was in documentation
    private static final String smallHorizontalSuffix = "_sb.png";
    private static final String largeHorizontalSuffix = "_lg.png";
    private static final String fullHorizontalSuffix = "_full.png";
    private static final String fullVerticalSuffix = "_vert.jpg";
    private static final String baseHeroIconUrl = "http://media.steampowered.com/apps/dota2/images/heroes/";

    @SerializedName("name")
    public String name;

    @SerializedName("localized_name")
    public String localizedName;

    @SerializedName("id")
    public int heroId;

    public String getLocalizedName() {
        return localizedName;
    }

    public String getHeroIdString() {
        return String.valueOf(heroId);
    }

    private String getBaseUrlString() {
        return baseHeroIconUrl + name.substring(14);
    }

//    public String getTinyHorizontalPortraitUrl() {
//        return getBaseUrlString() + tinyHorizontalSuffix;
//    }

    public String getSmallHorizontalPortraitUrl() {
        return getBaseUrlString() + smallHorizontalSuffix;
    }

    public String getLargeHorizontalPortraitUrl() {
        return getBaseUrlString() + largeHorizontalSuffix;
    }

    public String getFullHorizontalPortraitUrl() {
        return getBaseUrlString() + fullHorizontalSuffix;
    }

    public String getFullVerticalPortraitUrl() {
        return getBaseUrlString() + fullVerticalSuffix;
    }
}
