package com.hatfat.dota.model.match;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by scottrick on 3/15/14.
 */
public class MatchesGsonObject {
    @SerializedName("matches")
    List<Match> matches;
}
