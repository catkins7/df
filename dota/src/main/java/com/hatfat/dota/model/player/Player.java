package com.hatfat.dota.model.player;

import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

/**
 * Created by scottrick on 2/13/14.
 */
public class Player {
    long accountId;
    byte playerSlot;
    int heroId;

    public SteamUser getSteamUser() {
        return SteamUsers.get().getByAccountId("" + accountId);
    }

    public int getHeroId() {
        return heroId;
    }
    public long getAccountId() {
        return accountId;
    }

    public String getHeroIdString() {
        return String.valueOf(heroId);
    }

    public boolean isDirePlayer() {
        return playerSlot >> 7 > 0;
    }

    public boolean isRadiantPlayer() {
        return playerSlot >> 7 == 0;
    }
}
