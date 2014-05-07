package com.hatfat.dota.activities;

import android.os.Bundle;

import com.hatfat.dota.fragments.DotaPlayerStatisticsFragment;
import com.hatfat.dota.fragments.DotaPlayerSummaryFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class PlayerActivity extends CharltonActivity {

    public static final String PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY = "PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY";

    private String steamUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        steamUserId = getIntent().getStringExtra(PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY);

        if (steamUserId == null) {
            throw new RuntimeException("Must be initialized with a steam user id");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle playerSummaryBundle = DotaPlayerSummaryFragment.newBundleForUser(steamUserId);
        Bundle playerStatsBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId);

        CharltonTab<DotaPlayerSummaryFragment> playerSummaryTab = new CharltonTab(this, "Summary", DotaPlayerSummaryFragment.class, playerSummaryBundle);
        CharltonTab<DotaPlayerStatisticsFragment> statsTab = new CharltonTab(this, "Stats", DotaPlayerStatisticsFragment.class, playerStatsBundle);

        tabs.add(playerSummaryTab);
        tabs.add(statsTab);

        return tabs;
    }
}
