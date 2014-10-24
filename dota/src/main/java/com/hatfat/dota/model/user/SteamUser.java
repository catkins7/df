package com.hatfat.dota.model.user;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.annotations.SerializedName;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class SteamUser {

    public static final String STEAM_USER_UPDATED = "SteamUserUpdated_Notification";
    public static final String STEAM_USER_MATCHES_CHANGED = "STEAM_USER_MATCHES_CHANGED";
    public static final String STEAM_USER_UPDATED_ID_KEY = "SteamUserUpdated_UserId_Key";

    private static final long ACCOUNT_ID_MAGIC_NUMBER = 76561197960265728L;

    private static Comparator<SteamUser> comparator;

    @SerializedName("steamid")
    String steamId;

    @SerializedName("communityvisibilitystate")
    int communityVisibilityState;

    @SerializedName("profilestate")
    int profileState;

    @SerializedName("personaname")
    String personaName;

    @SerializedName("lastlogoff")
    long lastLogoff;

    @SerializedName("profileurl")
    String profileUrl;

    @SerializedName("avatar")
    String avatarUrl;

    @SerializedName("avatarmedium")
    String avatarMediumUrl;

    @SerializedName("avatarfull")
    String avatarFullUrl;

    @SerializedName("personastate")
    int personaState;

    @SerializedName("realname")
    String realName;

    @SerializedName("primaryclanid")
    String primaryClanId;

    @SerializedName("timecreated")
    long timeCreated;

    @SerializedName("personastateflags")
    int personaStateFlags;

    @SerializedName("loccountrycode")
    String locCountryCode;

    @SerializedName("locstatecode")
    String locStateCode;

    @SerializedName("loccityid")
    String locCityId;

    boolean isFakeUser; //true for Anonymous users and Bots
    TreeSet<String> matches;

    private long cachedAccountId = -1;

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

    public SteamUser(String steamId) {
        this.steamId = steamId;
        matches = new TreeSet<>();
    }

    public String getSteamId() { return steamId; }
    public String getDisplayName() {
        return personaName == null ? steamId : personaName;
    }
    public boolean isRealUser() {
        return !isFakeUser;
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
    public int getCurrentStateDescriptionTextColor(Resources resources) {
        if (getPlayerState().equals(SteamUser.SteamPlayerState.SteamPlayerState_Offline)) {
            return resources.getColor(R.color.off_gray);
        }
        else {
            return resources.getColor(R.color.steam_light_blue);
        }
    }
    public String getAccountId() {
        return SteamUser.getAccountIdFromSteamId(steamId);
    }
    public long getAccountIdLong() {
        if (cachedAccountId < 0) {
            cachedAccountId = Long.valueOf(getAccountId());
        }

        return cachedAccountId;
    }
    public long getSteamIdLong() {
        return Long.valueOf(steamId);
    }
    public TreeSet<String> getMatches() {
        //return a copy of the matches set, to prevent concurrent threading issues
        return new TreeSet(matches);
    }
    public String[] getMatchSummaryStrings(Resources resources) {
        String[] strings = new String[3];

        int publicWinCount = 0;
        int publicGameCount = 0;
        int rankedWinCount = 0;
        int rankedGameCount = 0;
        int matchesWithDetailsCount = 0;

        for (String matchId : matches) {
            Match match = Matches.get().getMatch(matchId);

            if (match.hasMatchDetails()) {
                matchesWithDetailsCount++;
            }

            if (!match.shouldBeUsedInRealStatistics()) {
                //don't use matches we shouldn't be!
                continue;
            }

            if (match.isRankedMatchmaking()) {
                Match.PlayerMatchResult result = match.getPlayerMatchResultForPlayer(match.getPlayerForSteamUser(this));

                if (result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                    rankedWinCount++;
                }

                if (!result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_UNKNOWN)) {
                    rankedGameCount++;
                }
            }
            else if (match.isPublicMatchmaking()) {
                Match.PlayerMatchResult result = match.getPlayerMatchResultForPlayer(match.getPlayerForSteamUser(this));

                if (result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                    publicWinCount++;
                }

                if (!result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_UNKNOWN)) {
                    publicGameCount++;
                }
            }
        }

        float publicPercent = 100.0f * (float)publicWinCount / (float)publicGameCount;
        float rankedPercent = 100.0f * (float)rankedWinCount / (float)rankedGameCount;

        if (publicGameCount > 0) {
            strings[0] = String
                    .format(resources.getString(R.string.player_summary_public_matchmaking_summary),
                            publicPercent);
        }
        else {
            strings[0] = resources.getString(R.string.player_summary_no_public_matches_string);
        }

        if (rankedGameCount > 0) {
            strings[1] = String
                    .format(resources.getString(R.string.player_summary_ranked_matchmaking_summary),
                            rankedPercent);
        }
        else {
            strings[1] = resources.getString(R.string.player_summary_no_ranked_matches_string);
        }

        strings[2] = String.format(resources.getString(R.string.player_summary_matches_summary_string), matchesWithDetailsCount, matches.size());

        return strings;
    }

    public void addMatches(List<Match> matches) {
        if (matches == null) {
            return;
        }

        boolean addedNewMatch = false;

        for (Match match : matches) {
            if (!this.matches.contains(match.getMatchId())) {
                this.matches.add(match.getMatchId());
                addedNewMatch = true;
            }
        }

        if (addedNewMatch) {
            broadcastUserMatchesChanged();
        }
    }

    public void removeMatchesIfNoDetails(List<String> matchIds) {
        if (matchIds == null) {
            return;
        }

        boolean removedMatch = false;

        for (String matchId : matchIds) {
            if (matches.contains(matchId)) {
                Match match = Matches.get().getMatch(matchId);

                if (!match.hasMatchDetails()) {
                    removedMatch = true;
                    matches.remove(matchId);
                }
            }
        }

        if (removedMatch) {
            broadcastUserMatchesChanged();
        }
    }

    public String toString() {
        return "SteamUser[" + super.toString() + "]";
//                + "\n\tsteamId: " + steamId
//                + "\n\tcommunityVisibilityState: " + communityVisibilityState
//                + "\n\tprofileState: " + profileState
//                + "\n\tpersonaName: " + personaName
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
//                ;
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

        matches.addAll(user.matches);

        broadcastUserChanged();
    }

    private void broadcastUserChanged() {
        Intent intent = new Intent(STEAM_USER_UPDATED);
        intent.putExtra(STEAM_USER_UPDATED_ID_KEY, steamId);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
    }

    private void broadcastUserMatchesChanged() {
        Intent intent = new Intent(STEAM_USER_MATCHES_CHANGED);
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

    @Override
    public int hashCode() {
        return (int) Long.parseLong(steamId);
    }

    public static Comparator<SteamUser> getComparator() {
        if (comparator == null) {
            comparator = new Comparator<SteamUser>() {
                @Override
                public int compare(SteamUser steamUser, SteamUser steamUser2) {
                    return steamUser.getDisplayName().compareToIgnoreCase(steamUser2.getDisplayName());
                }
            };
        }

        return comparator;
    }

    public static String getAccountIdFromSteamId(String steamId) {
        long steamIdLong = Long.valueOf(steamId);
        long accountIdLong = steamIdLong - ACCOUNT_ID_MAGIC_NUMBER;

        return String.valueOf(accountIdLong);
    }

    public static String getSteamIdFromAccountId(String accountId) {
        long accountIdLong = Long.valueOf(accountId);
        long steamId = accountIdLong + ACCOUNT_ID_MAGIC_NUMBER;

        return String.valueOf(steamId);
    }
}