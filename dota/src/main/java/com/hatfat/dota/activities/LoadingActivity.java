package com.hatfat.dota.activities;

import com.hatfat.dota.fragments.LoadingFragment;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class LoadingActivity extends CharltonActivity {

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        CharltonTab<LoadingFragment> loadingTab = new CharltonTab(this, "Loading", LoadingFragment.class, null);
        tabs.add(loadingTab);

        return tabs;
    }
}
