package com.hatfat.dota.model.league;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 7/23/14.
 */
public class LiveLeagueList {
    @SerializedName("leagues")
    public List<League> leagues;
}
