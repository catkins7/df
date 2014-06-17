package com.hatfat.dota.model.game;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemData {
    @SerializedName("items")
    List<Item> items;
}
