package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.adapters.DotaStatisticsAdapter;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DotaPlayerStatisticsFragment extends Fragment implements DrawerLayout.DrawerListener {

    public final static int RECENT_STATS_MATCH_COUNT = 50;

    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY";

    private BroadcastReceiver receiver;
    private boolean needsRecalculation;

    private SteamUser user;

    private ListView listView;
    private DotaStatisticsAdapter adapter;

    public DotaPlayerStatisticsFragment() {

    }

    public static DotaPlayerStatisticsFragment newInstance(SteamUser user) {
        DotaPlayerStatisticsFragment newFragment = new DotaPlayerStatisticsFragment();

        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY, user.getSteamId());
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        needsRecalculation = true;

        String steamUserId = getArguments().getString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_statistics, null);

        setupListView(view);

        return view;
    }

    private void setupListView(View view) {
        adapter = new DotaStatisticsAdapter(getResources());

        listView = (ListView) view.findViewById(R.id.fragment_dota_player_statistics_list_view);
        listView.setAdapter(adapter);
    }

    private void fetchStatistics() {
        needsRecalculation = false;

        new AsyncTask<SteamUser, Void, List<DotaStatistics>>() {
            @Override
            protected List<DotaStatistics> doInBackground(SteamUser... params) {
                SteamUser user = params[0];
                LinkedList<Match> allMatches = new LinkedList();
                LinkedList<Match> rankedMatches = new LinkedList();
                LinkedList<Match> publicMatches = new LinkedList();

                for (String matchId : user.getMatches()) {
                    Match match = Matches.get().getMatch(matchId);

                    if (!match.shouldBeUsedInStatistics()) {
                        continue;
                    }

                    //add to the all matches list
                    allMatches.add(match);

                    if (match.isRankedMatchmaking()) {
                        rankedMatches.add(match);
                    }

                    if (match.isPublicMatchmaking()) {
                        publicMatches.add(match);
                    }
                }

                //sort the ranked list, so we can get the recent ranked matches
                Collections.sort(rankedMatches, Match.getComparator());
                int numberOfRecentRankedMatches = Math.min(RECENT_STATS_MATCH_COUNT,
                        rankedMatches.size());

                LinkedList<DotaStatistics> stats = new LinkedList();
                stats.add(new DotaStatistics(user, new LinkedList(allMatches)));
                stats.add(new DotaStatistics(user, new LinkedList(rankedMatches)));
                stats.add(new DotaStatistics(user, new LinkedList(publicMatches)));
                stats.add(new DotaStatistics(user, rankedMatches.subList(0, numberOfRecentRankedMatches)));

                return stats;
            }

            @Override
            protected void onPostExecute(List<DotaStatistics> statsList) {
                adapter.setNewStatistics(statsList);
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
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        if (needsRecalculation) {
            adapter.setNewStatistics(null);
            fetchStatistics();
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (needsRecalculation) {
            adapter.setNewStatistics(null);
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
