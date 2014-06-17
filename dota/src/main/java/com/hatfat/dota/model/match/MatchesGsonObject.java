package com.hatfat.dota.model.match;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchesGsonObject {
    @SerializedName("version")
    int version;

    @SerializedName("matches")
    List<Match> matches;

    public MatchesGsonObject() {
        version = 0;
    }

    public MatchesGsonObject(int version) {
        this.version = version;
    }
}
