package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.adapters.DotaStatisticsAdapter;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.LinkedList;

public class DotaPlayerStatisticsFragment extends CharltonFragment {

    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY";
    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_MODE_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_MODE_KEY";

    private BroadcastReceiver receiver;
    private boolean needsRecalculation;

    private SteamUser user;
    private DotaStatistics.DotaStatisticsMode statsMode;

    private DotaStatisticsAdapter adapter;

    public static Bundle newBundleForUser(String steamUserId, DotaStatistics.DotaStatisticsMode mode) {
        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY, steamUserId);
        args.putInt(DOTA_PLAYER_STATISTICS_FRAGMENT_MODE_KEY, mode.getMode());
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        needsRecalculation = true;

        statsMode = DotaStatistics.DotaStatisticsMode
                .modeFromInt(getArguments().getInt(DOTA_PLAYER_STATISTICS_FRAGMENT_MODE_KEY));
        String steamUserId = getArguments().getString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        signalCharltonActivityToUpdateTab();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (needsRecalculation) {
            adapter.setNewStatistics(null, DotaStatistics.DotaStatisticsMode.ALL_FAVORITES);
            fetchStatistics();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (needsRecalculation) {
            adapter.setNewStatistics(null, DotaStatistics.DotaStatisticsMode.ALL_FAVORITES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_statistics, container, false);

        setupListView(view);

        return view;
    }

    private void setupListView(View view) {
        if (adapter == null) {
            adapter = new DotaStatisticsAdapter(getResources());
        }

        ListView listView = (ListView) view
                .findViewById(R.id.fragment_dota_player_statistics_list_view);
        listView.setAdapter(adapter);
    }

    private void fetchStatistics() {
        needsRecalculation = false;

        new AsyncTask<SteamUser, Void, DotaStatistics>() {
            @Override
            protected DotaStatistics doInBackground(SteamUser... params) {
                SteamUser user = params[0];
                LinkedList<Match> statsMatches = new LinkedList();

                for (String matchId : user.getMatches()) {
                    Match match = Matches.get().getMatch(matchId);

                    if (match.shouldBeUsedInRealStatistics()) {
                        switch (statsMode) {
                            case ALL_HEROES:
                            case ALL_FAVORITES:
                            case ALL_SUCCESS_STATS:
                            case MATCH_UPS:
                                statsMatches.add(match);
                                break;
                            case RANKED_STATS:
                                if (match.isRankedMatchmaking()) {
                                    statsMatches.add(match);
                                }
                                break;
                            case PUBLIC_STATS:
                                if (match.isPublicMatchmaking()) {
                                    statsMatches.add(match);
                                }
                                break;
                        }
                    }
                    else {
                        if (match.hasMatchDetails()) {
                            switch (statsMode) {
                                case OTHER_STATS:
                                    statsMatches.add(match);
                                    break;
                            }
                        }
                    }
                }

                return new DotaStatistics(user, new LinkedList(statsMatches));
            }

            @Override
            protected void onPostExecute(DotaStatistics stats) {
                adapter.setNewStatistics(stats, statsMode);
            }
        }.execute(user);
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUser.STEAM_USER_MATCHES_CHANGED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        needsRecalculation = true;
                    }
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        needsRecalculation = true;
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(SteamUser.STEAM_USER_MATCHES_CHANGED);
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.player_statistics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_player_summary_stats_info:
                showStatsInfoDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showStatsInfoDialog() {
        TextDialogHelper.showStatsDialog(getActivity());
    }

    @Override
    public String getCharltonMessageText(Context context) {
        Resources resources = context.getResources();

        if (statsMode != null) {
            switch (statsMode) {
                case ALL_FAVORITES:
                    return String.format(resources.getString(
                            R.string.player_statistics_charlton_text_all_favorites), user.getDisplayName());
                case ALL_SUCCESS_STATS:
                    return String.format(resources.getString(
                            R.string.player_statistics_charlton_text_success), user.getDisplayName());
                case RANKED_STATS:
                    return String.format(resources
                            .getString(R.string.player_statistics_charlton_text_ranked_stats), user.getDisplayName());
                case OTHER_STATS:
                    return String.format(resources
                            .getString(R.string.player_statistics_charlton_text_other_stats), user.getDisplayName());
                case PUBLIC_STATS:
                    return String.format(resources
                            .getString(R.string.player_statistics_charlton_text_public_stats), user.getDisplayName());
                case ALL_HEROES:
                    return String.format(resources
                            .getString(R.string.player_statistics_charlton_text_all_heroes_stats), user.getDisplayName());
                case MATCH_UPS:
                    return String.format(resources.getString(R.string.player_statistics_charlton_text_match_up_stats), user.getDisplayName());

            }
        }

        return resources.getString(R.string.player_statistics_charlton_text_default);
    }
}
