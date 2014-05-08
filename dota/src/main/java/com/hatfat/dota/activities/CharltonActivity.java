package com.hatfat.dota.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.tabs.CharltonTab;
import com.hatfat.dota.util.CharltonBubbleDrawable;

import java.util.List;

public abstract class CharltonActivity extends Activity {

    private TextView charltonTitleTextView;

    private List<CharltonTab> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isLoadingActivity() && !isDataLoaded()) {
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
            startActivity(intent);
            finish();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(hasParentActivity());
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_charlton);

        charltonTitleTextView = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_charlton_text_view);
        charltonTitleTextView.setText(R.string.default_charlton_text);
        actionBar.getCustomView().setBackgroundDrawable(new CharltonBubbleDrawable());

        tabs = createTabs();

        if (tabs.size() > 1) {
            //more than one tab, so setup the tabbed interface
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            for (CharltonTab charltonTab : tabs) {
                ActionBar.Tab tab = actionBar.newTab()
                        .setText(charltonTab.getCharltonTabText())
                        .setTabListener(charltonTab);

                actionBar.addTab(tab);
            }
        }
        else if (tabs.size() == 1) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();

            tabs.get(0).attach(ft);

            ft.commit();
            manager.executePendingTransactions();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isLoadingActivity() && !isDataLoaded()) {
            throw new RuntimeException("starting this activity with no data!");
        }
    }

    private boolean isDataLoaded() {
        return SteamUsers.get().isLoaded() && Matches.get().isLoaded() && Heroes.get().isLoaded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateWithCharltonTab(CharltonTab tab) {
        charltonTitleTextView.setText(tab.getFragment().getCharltonMessageText(getResources()));
    }

    public void signalUpdateActiveCharltonTab() {
        int selectedTabIndex = 0;

        if (tabs.size() > 1) {
            selectedTabIndex = getActionBar().getSelectedNavigationIndex();
        }

        updateWithCharltonTab(tabs.get(selectedTabIndex));
    }

    protected boolean hasParentActivity() {
        return true;
    }

    protected boolean isLoadingActivity() {
        return false;
    }

    protected abstract List<CharltonTab> createTabs();
}
