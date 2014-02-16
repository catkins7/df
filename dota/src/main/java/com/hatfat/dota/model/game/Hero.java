package com.hatfat.dota.model.game;

/**
 * Created by scottrick on 2/15/14.
 */
public class Hero {
    private static final String tinyHorizontalSuffix = "_eb.png"; //doesn't seem to exist, but was in documentation
    private static final String smallHorizontalSuffix = "_sb.png";
    private static final String largeHorizontalSuffix = "_lg.png";
    private static final String fullHorizontalSuffix = "_full.png";
    private static final String fullVerticalSuffix = "_vert.jpg";
    private static final String baseHeroIconUrl = "http://media.steampowered.com/apps/dota2/images/heroes/";

    String name;
    String localizedName;
    int heroId;

    public String getLocalizedName() {
        return localizedName;
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
