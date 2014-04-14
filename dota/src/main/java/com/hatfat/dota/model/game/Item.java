package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scottrick on 3/5/14.
 */
public class Item implements Comparable<Item> {
    private static final String baseItemIconUrl = "http://media.steampowered.com/apps/dota2/images/items/";
    private static final String largeHorizontalSuffix = "_lg.png";

    @SerializedName("name")
    String name;

    @SerializedName("id")
    int itemId;

    @SerializedName("itemCost")
    int itemCost;

    private String getBaseUrlString() {
        return baseItemIconUrl + name.substring(5);
    }

    public String getLargeHorizontalPortraitUrl() {
        return getBaseUrlString() + largeHorizontalSuffix;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemCost() {
        return itemCost;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Item) {
            Item otherItem = (Item)o;
            return itemId == otherItem.itemId;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return itemId;
    }

    @Override
    public int compareTo(Item another) {
        return itemId - another.itemId;
    }
}
