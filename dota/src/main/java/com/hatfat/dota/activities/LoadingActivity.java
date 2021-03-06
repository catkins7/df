package com.hatfat.dota.activities;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.LoadingFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class LoadingActivity extends CharltonActivity {

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        CharltonTab<LoadingFragment> loadingTab = new CharltonTab(this, getResources().getString(R.string.tab_loading_title), LoadingFragment.class, null);
        tabs.add(loadingTab);

        return tabs;
    }

    @Override
    protected boolean isLoadingActivity() {
        return true;
    }

    @Override
    protected boolean hasParentActivity() {
        return false;
    }
}
