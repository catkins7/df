package com.hatfat.dota.model.player;

import com.google.gson.annotations.SerializedName;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

/**
 * Created by scottrick on 2/13/14.
 */
public class Player {

    @SerializedName("account_id")
    long accountId;

    @SerializedName("player_slot")
    int playerSlot;

    @SerializedName("hero_id")
    int heroId;

    @SerializedName("item_0")
    int item0;

    @SerializedName("item_1")
    int item1;

    @SerializedName("item_2")
    int item2;

    @SerializedName("item_3")
    int item3;

    @SerializedName("item_4")
    int item4;

    @SerializedName("item_5")
    int item5;

    @SerializedName("kills")
    int kills;

    @SerializedName("deaths")
    int deaths;

    @SerializedName("assists")
    int assists;

    @SerializedName("leaver_status")
    int leaverStatus;

    @SerializedName("gold")
    int gold;

    @SerializedName("last_hits")
    int lastHits;

    @SerializedName("denies")
    int denies;

    @SerializedName("gold_per_min")
    int goldPerMinute;

    @SerializedName("xp_per_min")
    int xpPerMinute;

    @SerializedName("gold_spent")
    int goldSpent;

    @SerializedName("hero_damage")
    int heroDamage;

    @SerializedName("tower_damage")
    int towerDamage;

    @SerializedName("hero_healing")
    int heroHealing;

    @SerializedName("level")
    int level;

    public SteamUser getSteamUser() {
        return SteamUsers.get().getByAccountId(String.valueOf(accountId));
    }

    public int getHeroId() {
        return heroId;
    }
    public int getPlayerSlot() { return playerSlot; }
    public long getAccountId() {
        return accountId;
    }

    public String getHeroIdString() {
        return String.valueOf(heroId);
    }

    public int getKills() {
        return kills;
    }
    public int getDeaths() {
        return deaths;
    }
    public int getAssists() {
        return assists;
    }

    public String getKdaString() {
        return String.valueOf(kills) + " / " + String.valueOf(deaths) + " / " + String.valueOf(assists);
    }

    public boolean isDirePlayer() {
        return playerSlot >> 7 != 0;
    }

    public boolean isRadiantPlayer() {
        return playerSlot >> 7 == 0;
    }
}
