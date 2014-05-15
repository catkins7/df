package com.hatfat.dota.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.PlayerMatchListFragment;
import com.hatfat.dota.fragments.PlayerMatchListStatisticsFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayerMatchListActivity extends CharltonActivity {

    private static final String PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY = "PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY = "PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY = "PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY";

    private String steamUserId;
    private String label;
    private ArrayList<String> matchIds;

    public static Intent intentForUserLabelAndMatches(Context context, String steamUserId, String label, ArrayList<String> matchIds) {
        Intent intent = new Intent(context, PlayerMatchListActivity.class);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY, steamUserId);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY, label);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY, matchIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        steamUserId = getIntent().getStringExtra(PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY);
        label = getIntent().getStringExtra(PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY);
        matchIds = getIntent().getStringArrayListExtra(PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY);

        if (steamUserId == null || label == null || matchIds == null) {
            throw new RuntimeException("Must be initialized with a steam user id, label, and match ids");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle matchListBundle = PlayerMatchListFragment
                .newBundleForUserAndMatches(steamUserId, label, matchIds);
        Bundle statsBundle = PlayerMatchListStatisticsFragment.newBundleForUserAndMatches(steamUserId, label, matchIds);

        CharltonTab<PlayerMatchListFragment> matchesTab = new CharltonTab(this, label, PlayerMatchListFragment.class, matchListBundle);
        CharltonTab<PlayerMatchListStatisticsFragment> statsTab = new CharltonTab(this, getString(R.string.player_match_list_stats_tab_text), PlayerMatchListStatisticsFragment.class, statsBundle);

        tabs.add(matchesTab);
        tabs.add(statsTab);

        return tabs;
    }
}
