package com.hatfat.dota.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.PlayerMatchListFragment;
import com.hatfat.dota.fragments.PlayerMatchListStatisticsFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class PlayerMatchListActivity extends CharltonActivity {

    private static final String PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY = "PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY = "PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_SECONDARY_IMAGE_URL_KEY = "PLAYER_MATCH_LIST_ACTIVITY_SECONDARY_IMAGE_URL_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY = "PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY";
    private static final String PLAYER_MATCH_LIST_ACTIVITY_TEXT_MODE_KEY = "PLAYER_MATCH_LIST_ACTIVITY_TEXT_MODE_KEY";

    public enum MatchListTextMode {
        NORMAL_MODE(1),
        ALTERNATE_MODE(2),
        MATCH_UP_MODE(3);

        public int mode;

        MatchListTextMode(int mode) {
            this.mode = mode;
        }

        public static MatchListTextMode fromInt(int mode) {
            switch (mode) {
                case 1:
                    return NORMAL_MODE;
                case 2:
                    return ALTERNATE_MODE;
                case 3:
                    return MATCH_UP_MODE;
            }

            return NORMAL_MODE;
        }
    }

    private String            steamUserId;
    private String            label;
    private String            secondaryImageUrl;
    private MatchListTextMode mode;
    private long[]            matchIds;

    public static Intent intentForUserLabelAndMatches(Context context, String steamUserId,
            String label, String secondaryImageUrl, long[] matchIds,
            MatchListTextMode textMode) {
        Intent intent = new Intent(context, PlayerMatchListActivity.class);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY, steamUserId);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY, label);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_SECONDARY_IMAGE_URL_KEY, secondaryImageUrl);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY, matchIds);
        intent.putExtra(PLAYER_MATCH_LIST_ACTIVITY_TEXT_MODE_KEY, textMode.mode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        steamUserId = getIntent().getStringExtra(PLAYER_MATCH_LIST_ACTIVITY_STEAM_USER_ID_KEY);
        label = getIntent().getStringExtra(PLAYER_MATCH_LIST_ACTIVITY_LABEL_KEY);
        secondaryImageUrl = getIntent().getStringExtra(PLAYER_MATCH_LIST_ACTIVITY_SECONDARY_IMAGE_URL_KEY);
        matchIds = getIntent().getLongArrayExtra(PLAYER_MATCH_LIST_ACTIVITY_MATCHES_KEY);
        mode = MatchListTextMode.fromInt(getIntent().getIntExtra(PLAYER_MATCH_LIST_ACTIVITY_TEXT_MODE_KEY, MatchListTextMode.NORMAL_MODE.mode));

        if (steamUserId == null || label == null || matchIds == null) {
            throw new RuntimeException("Must be initialized with a steam user id, label, and match ids");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle matchListBundle = PlayerMatchListFragment
                .newBundleForUserAndMatches(steamUserId, label, secondaryImageUrl, matchIds, mode);
        Bundle statsBundle = PlayerMatchListStatisticsFragment.newBundleForUserAndMatches(steamUserId, label, matchIds, mode);

        CharltonTab<PlayerMatchListFragment> matchesTab = new CharltonTab(this, label, PlayerMatchListFragment.class, matchListBundle);
        CharltonTab<PlayerMatchListStatisticsFragment> statsTab = new CharltonTab(this, getString(R.string.player_match_list_stats_tab_text), PlayerMatchListStatisticsFragment.class, statsBundle);

        tabs.add(matchesTab);
        tabs.add(statsTab);

        return tabs;
    }
}
