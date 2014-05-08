package com.hatfat.dota.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.SteamUserListFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchResultsActivity extends CharltonActivity {

    private static final String SEARCH_RESULTS_ACTIVITY_USER_LIST_EXTRA_KEY = "SEARCH_RESULTS_ACTIVITY_USER_LIST_EXTRA_KEY";
    private static final String SEARCH_RESULTS_ACTIVITY_MESSAGE_EXTRA_KEY = "SEARCH_RESULTS_ACTIVITY_MESSAGE_EXTRA_KEY";

    private ArrayList<String> steamUserIds;
    private String message;

    public static Intent intentForResultsWithMessage(Context context, ArrayList<String> userIds, String message) {
        Intent intent = new Intent(context, SearchResultsActivity.class);

        intent.putExtra(SEARCH_RESULTS_ACTIVITY_MESSAGE_EXTRA_KEY, message);
        intent.putStringArrayListExtra(SEARCH_RESULTS_ACTIVITY_USER_LIST_EXTRA_KEY, userIds);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        message = getIntent().getStringExtra(SEARCH_RESULTS_ACTIVITY_MESSAGE_EXTRA_KEY);
        steamUserIds = getIntent().getStringArrayListExtra(SEARCH_RESULTS_ACTIVITY_USER_LIST_EXTRA_KEY);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle userIdsBundle = SteamUserListFragment.newBundleForUserIdsWithMessage(steamUserIds, message);

        CharltonTab<SteamUserListFragment> usersTab = new CharltonTab(this, getResources().getString(
                R.string.tab_search_results_title), SteamUserListFragment.class, userIdsBundle);

        tabs.add(usersTab);

        return tabs;
    }
}
