package com.hatfat.dota.model.dotabuff;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scottrick on 3/15/14.
 */
public class DotaBuffSearchResult {
    @SerializedName("type")
    String type;

    @SerializedName("name")
    String name;

    @SerializedName("icon")
    String icon;

    @SerializedName("url")
    String url;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayer() {
        return type.toLowerCase().equals("player");
    }

    public boolean isTeam() {
        return type.toLowerCase().equals("team");
    }

    public String getAccountId() {
        if (isPlayer()) {
            int lastIndex = url.lastIndexOf("/");
            return url.substring(lastIndex + 1);
        }
        else {
            return null;
        }
    }
}
