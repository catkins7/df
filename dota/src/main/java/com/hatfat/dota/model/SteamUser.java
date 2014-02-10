package com.hatfat.dota.model;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.hatfat.dota.DotaFriendApplication;

import java.util.Comparator;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUser {

    public static final String STEAM_USER_UPDATED = "SteamUserUpdated_Notification";
    public static final String STEAM_USER_UPDATED_ID_KEY = "SteamUserUpdated_UserId_Key";

    private static Comparator<SteamUser> comparator;

    String steamId;
    int communityVisibilityState;
    int profileState;
    String personaName;
    long lastLogoff;
    String profileUrl;
    String avatarUrl;
    String avatarMediumUrl;
    String avatarFullUrl;
    int personaState;
    String realName;
    String primaryClanId;
    long timeCreated;
    int personaStateFlags;
    String locCountryCode;
    String locStateCode;
    String locCityId;

    public String getPersonaName() {
        return personaName;
    }

    public String toString() {
        return "\nSteamUser[" + super.toString() + "]:"
                + "\n\tsteamId: " + steamId
//                + "\n\tcommunityVisibilityState: " + communityVisibilityState
//                + "\n\tprofileState: " + profileState
                + "\n\tpersonaName: " + personaName
//                + "\n\tlastLogoff: " + lastLogoff
//                + "\n\tprofileUrl: " + profileUrl
//                + "\n\tavatarUrl: " + avatarUrl
//                + "\n\tavatarMediumUrl: " + avatarMediumUrl
//                + "\n\tavatarFullUrl: " + avatarFullUrl
//                + "\n\tpersonaState: " + personaState
//                + "\n\trealName: " + realName
//                + "\n\tprimaryClanId: " + primaryClanId
//                + "\n\ttimeCreated: " + timeCreated
//                + "\n\tpersonaStateFlags: " + personaStateFlags
//                + "\n\tlocCountryCode: " + locCountryCode
//                + "\n\tlocStateCode: " + locStateCode
//                + "\n\tlocCityId: " + locCityId
                ;
    }

    public void updateWithSteamUser(SteamUser user) {
        steamId = user.steamId;
        communityVisibilityState = user.communityVisibilityState;
        profileState = user.profileState;
        personaName = user.personaName;
        lastLogoff = user.lastLogoff;
        profileUrl = user.profileUrl;
        avatarUrl = user.avatarUrl;
        avatarMediumUrl = user.avatarMediumUrl;
        avatarFullUrl = user.avatarFullUrl;
        personaState = user.personaState;
        realName = user.realName;
        primaryClanId = user.primaryClanId;
        timeCreated = user.timeCreated;
        personaStateFlags = user.personaStateFlags;
        locCountryCode = user.locCountryCode;
        locStateCode = user.locStateCode;
        locCityId = user.locCityId;

        broadcastUserChanged();
    }

    private void broadcastUserChanged() {
        Intent intent = new Intent(STEAM_USER_UPDATED);
        intent.putExtra(STEAM_USER_UPDATED_ID_KEY, steamId);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof SteamUser) {
            SteamUser otherUser = (SteamUser)o;
            if (steamId != null && steamId.length() > 0) {
                return steamId.equals(otherUser.steamId);
            }
        }

        return false;
    }

    public static Comparator<SteamUser> getComparator() {
        if (comparator == null) {
            comparator = new Comparator<SteamUser>() {
                @Override
                public int compare(SteamUser steamUser, SteamUser steamUser2) {
                    return steamUser.getPersonaName().compareToIgnoreCase(steamUser2.getPersonaName());
                }
            };
        }

        return comparator;
    }
}