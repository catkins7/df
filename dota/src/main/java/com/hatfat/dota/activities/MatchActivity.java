package com.hatfat.dota.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hatfat.dota.fragments.MatchSummaryFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class MatchActivity extends CharltonActivity {

    private static final String MATCH_ACTIVITY_MATCH_ID_EXTRA_KEY = "MATCH_ACTIVITY_MATCH_ID_EXTRA_KEY";

    private String matchId;

    public static Intent intentForMatch(Context context, String matchId) {
        Intent intent = new Intent(context, MatchActivity.class);
        intent.putExtra(MatchActivity.MATCH_ACTIVITY_MATCH_ID_EXTRA_KEY, matchId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        matchId = getIntent().getStringExtra(MATCH_ACTIVITY_MATCH_ID_EXTRA_KEY);

        if (matchId == null) {
            throw new RuntimeException("Must be initialized with a match id");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle matchSummaryBundle = MatchSummaryFragment.newBundleForMatch(matchId);

        CharltonTab<MatchSummaryFragment> matchSummaryTab = new CharltonTab(this, "Summary", MatchSummaryFragment.class, matchSummaryBundle);

        tabs.add(matchSummaryTab);

        return tabs;
    }
}
