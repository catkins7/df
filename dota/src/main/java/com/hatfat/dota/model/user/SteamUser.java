package com.hatfat.dota.model.user;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.model.match.Match;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUser {

    private static final long ACCOUNT_ID_MAGIC_NUMBER = 76561197960265728L;

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

    TreeSet<String> matches;

    public enum SteamPlayerState
    { //  0 - Offline, 1 - Online, 2 - Busy, 3 - Away, 4 - Snooze, 5 - looking to trade, 6 - looking to play
        SteamPlayerState_Offline("Offline"),
        SteamPlayerState_Online("Online"),
        SteamPlayerState_Busy("Online (Busy)"),
        SteamPlayerState_Away("Online (Away)"),
        SteamPlayerState_Snooze("Online (Snooze)"),
        SteamPlayerState_LookingToTrade("Online (Looking to Trade)"),
        SteamPlayerState_LookingToPlay("Online (Looking to Play)"),
        SteamPlayerState_Unknown("Unknown");

        private String stateName;

        SteamPlayerState(String typeName) {
            this.stateName = typeName;
        }

        public static SteamPlayerState fromInt(int state) {
            switch (state) {
                case 0:
                    return SteamPlayerState_Offline;
                case 1:
                    return SteamPlayerState_Online;
                case 2:
                    return SteamPlayerState_Busy;
                case 3:
                    return SteamPlayerState_Away;
                case 4:
                    return SteamPlayerState_Snooze;
                case 5:
                    return SteamPlayerState_LookingToTrade;
                case 6:
                    return SteamPlayerState_LookingToPlay;
                default:
                    return SteamPlayerState_Unknown;
            }
        }

        public String getStateName() {
            return stateName;
        }
    }

    public SteamUser() {
        matches = new TreeSet<>();
    }

    public String getSteamId() { return steamId; }
    public String getPersonaName() {
        return personaName;
    }
    public String getAvatarFullUrl() { return avatarFullUrl; }
    public SteamPlayerState getPlayerState() {
        return SteamPlayerState.fromInt(personaState);
    }
    public String getCurrentStateDescriptionString() {
        SteamPlayerState state = getPlayerState();

        if (state.equals(SteamPlayerState.SteamPlayerState_Offline)) {
            return state.getStateName();
        }
        else {
            return state.getStateName();
        }
    }
    public String getAccountId() {
        return SteamUser.getAccountIdFromSteamId(steamId);
    }
    public TreeSet<String> getMatches() {
        return matches;
    }

    public void addMatches(List<Match> matches) {
        for (Match match : matches) {
            this.matches.add(match.getMatchId());
        }

        broadcastUserChanged();
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

    public static String getAccountIdFromSteamId(String steamId) {
        long steamIdLong = Long.valueOf(steamId).longValue();
        long accountIdLong = steamIdLong - ACCOUNT_ID_MAGIC_NUMBER;

        return String.valueOf(accountIdLong);
    }

    public static String getSteamIdFromAccountId(String accountId) {
        long accountIdLong = Long.valueOf(accountId).longValue();
        long steamId = accountIdLong + ACCOUNT_ID_MAGIC_NUMBER;

        return String.valueOf(steamId);
    }
}