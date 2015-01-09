package com.hatfat.dota.model.user;

import android.content.res.Resources;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;

import java.util.LinkedList;
import java.util.List;

public class CommonMatches implements Comparable {
    private String userOneAccountId;
    private String userTwoAccountId;

    private List<Long> commonMatches;

    //description values set by the calculateInfo method
    private int totalGameCount = 0;
    private int totalWinCount = 0;

    public CommonMatches(String userOneAccountId, String userTwoAccountId) {
        this.userOneAccountId = userOneAccountId;
        this.userTwoAccountId = userTwoAccountId;
        this.commonMatches = new LinkedList();
    }

    public String getUserOneAccountId() {
        return userOneAccountId;
    }

    public String getUserTwoAccountId() {
        return userTwoAccountId;
    }

    public void addMatch(Long matchId) {
        commonMatches.add(matchId);
    }

    public List<Long> getCommonMatches() {
        return commonMatches;
    }

    @Override
    public int compareTo(Object o) {
        //Sorts matches by date, so the newest match is first
        if (o != null && o instanceof CommonMatches) {
            CommonMatches other = (CommonMatches)o;

            int countDiff = other.getCommonMatches().size() - this.getCommonMatches().size();

            if (countDiff > 0) {
                return 1;
            }
            else if (countDiff < 0) {
                return -1;
            }

            //start times were the same, so compare match ids
            return getUserTwoAccountId().compareTo(other.getUserTwoAccountId());
        }

        return 0;
    }

    public String getMatchCountString(Resources resources) {
        return String.format(resources.getString(R.string.player_friends_matches_count_text), commonMatches.size());
    }

    public String getOverallWinRateString(Resources resources) {
        float winPercent = (float)totalWinCount / (float)totalGameCount * 100.0f;
        return String.format(resources.getString(R.string.player_friends_win_rate_text), winPercent);
    }

    public void calculateInfo() {
        SteamUser user1 = SteamUsers.get().getByAccountId(userOneAccountId);

        for (Long matchId : commonMatches) {
            Match match = Matches.get().getMatch(matchId);

            if (match.hasMatchDetails()) {
                totalGameCount++;
            }
            else {
                continue;
            }

            Player player = match.getPlayerForSteamUser(user1);
            Match.PlayerMatchResult matchResult = match.getPlayerMatchResultForPlayer(player);

            if (matchResult.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                totalWinCount++;
            }
        }
    }
}
