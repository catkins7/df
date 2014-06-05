package com.hatfat.dota.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.DotaPlayerStatisticsFragment;
import com.hatfat.dota.fragments.DotaPlayerSummaryFragment;
import com.hatfat.dota.fragments.PlayerFriendsFragment;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.tabs.CharltonTab;

import java.util.LinkedList;
import java.util.List;

public class PlayerActivity extends CharltonActivity {

    private static final String PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY = "PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY";

    private String steamUserId;

    public static Intent intentForPlayer(Context context, String steamUserId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(PlayerActivity.PLAYER_ACTIVITY_STEAM_USER_ID_EXTRA_KEY, steamUserId);
        return intent;
    }

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
        SteamUser user = SteamUsers.get().getBySteamId(steamUserId);

        LinkedList<CharltonTab> tabs = new LinkedList();

        Bundle playerSummaryBundle = DotaPlayerSummaryFragment.newBundleForUser(steamUserId);
        Bundle favoritesBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.ALL_FAVORITES);
        Bundle successBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.ALL_SUCCESS_STATS);
        Bundle friendsBundle = PlayerFriendsFragment.newBundleForUser(steamUserId);
        Bundle rankedBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.RANKED_STATS);
        Bundle publicBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.PUBLIC_STATS);
        Bundle otherBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.OTHER_STATS);
        Bundle allHeroesBundle = DotaPlayerStatisticsFragment.newBundleForUser(steamUserId,
                DotaStatistics.DotaStatisticsMode.ALL_HEROES);

        String summaryTitle = getResources().getString(R.string.tab_player_summary_title);
        String favoritesTitle = getResources().getString(R.string.tab_player_favorites_title);
        String successTitle = getResources().getString(R.string.tab_player_success_title);
        String friendsTitle = getResources().getString(R.string.tab_player_friends_title);
        String rankedTitle = getResources().getString(R.string.tab_player_ranked_stats_title);
        String publicTitle = getResources().getString(R.string.tab_player_public_stats_title);
        String otherTitle = getResources().getString(R.string.tab_player_other_stats_title);
        String allHeroesTitle = getResources().getString(R.string.tab_player_all_heroes_stats_title);

        CharltonTab<DotaPlayerSummaryFragment> playerSummaryTab = new CharltonTab(this, summaryTitle, DotaPlayerSummaryFragment.class, playerSummaryBundle);
        CharltonTab<DotaPlayerStatisticsFragment> favoritesTab = new CharltonTab(this, favoritesTitle, DotaPlayerStatisticsFragment.class, favoritesBundle);
        CharltonTab<DotaPlayerStatisticsFragment> successTab = new CharltonTab(this, successTitle, DotaPlayerStatisticsFragment.class, successBundle);
        CharltonTab<PlayerFriendsFragment> friendsTab = new CharltonTab(this, friendsTitle, PlayerFriendsFragment.class, friendsBundle);
        CharltonTab<DotaPlayerStatisticsFragment> rankedTab = new CharltonTab(this, rankedTitle, DotaPlayerStatisticsFragment.class, rankedBundle);
        CharltonTab<DotaPlayerStatisticsFragment> publicTab = new CharltonTab(this, publicTitle, DotaPlayerStatisticsFragment.class, publicBundle);
        CharltonTab<DotaPlayerStatisticsFragment> otherTab = new CharltonTab(this, otherTitle, DotaPlayerStatisticsFragment.class, otherBundle);
        CharltonTab<DotaPlayerStatisticsFragment> allHeroesTab = new CharltonTab(this, allHeroesTitle, DotaPlayerStatisticsFragment.class, allHeroesBundle);

        tabs.add(playerSummaryTab);

        if (user.isRealUser()) {
            //don't add these tabs if they're not a real user
            tabs.add(favoritesTab);
            tabs.add(successTab);
            tabs.add(friendsTab);
            tabs.add(rankedTab);
            tabs.add(publicTab);
            tabs.add(otherTab);
            tabs.add(allHeroesTab);
        }

        return tabs;
    }
}
