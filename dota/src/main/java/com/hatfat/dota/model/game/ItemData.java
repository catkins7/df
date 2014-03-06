package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 3/5/14.
 */
public class ItemData {
    @SerializedName("items")
    List<Item> items;
}
