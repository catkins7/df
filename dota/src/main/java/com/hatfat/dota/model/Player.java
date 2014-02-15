package com.hatfat.dota.model;

/**
 * Created by scottrick on 2/13/14.
 */
public class Player {
    long accountId;
    int playerSlot;
    int heroId;

    public SteamUser getSteamUser() {
        return SteamUsers.get().getByAccountId("" + accountId);
    }
}
