package com.hatfat.dota.activities;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.AddNewPlayerFragment;
import com.hatfat.dota.fragments.LiveLeaguesFragment;
import com.hatfat.dota.fragments.StarredPlayerListFragment;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class StarredUsersActivity extends CharltonActivity {

    @Override
    protected List<CharltonTab> createTabs() {
        LinkedList<CharltonTab> tabs = new LinkedList();

        String starredTitle = getResources().getString(R.string.tab_starred_players_title);
        String addPlayerTitle = getResources().getString(R.string.tab_add_player_title);
//        String leaguesTitle = getResources().getString(R.string.tab_live_leagues_title);

        CharltonTab<StarredPlayerListFragment> starredTab = new CharltonTab(this, starredTitle, StarredPlayerListFragment.class, null);
        CharltonTab<AddNewPlayerFragment> newPlayerTab = new CharltonTab(this, addPlayerTitle, AddNewPlayerFragment.class, null);
//        CharltonTab<LiveLeaguesFragment> leaguesTab = new CharltonTab(this, leaguesTitle, LiveLeaguesFragment.class, null);

        tabs.add(starredTab);
        tabs.add(newPlayerTab);
//        tabs.add(leaguesTab);

        return tabs;
    }

    @Override
    protected int getDefaultTabIndex() {
        //if they don't have any starred users, then start on the add player tab!
        if (SteamUsers.get().getStarredUsers().size() > 0) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    protected boolean hasParentActivity() {
        return false;
    }
}
