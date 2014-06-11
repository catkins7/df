package com.hatfat.dota.fragments;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hatfat.dota.R;
import com.hatfat.dota.adapters.DotaStatisticsAdapter;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlayerMatchListStatisticsFragment extends CharltonFragment {

    private static final String PLAYER_MATCH_LIST_STATS_USER_ID_KEY = "PLAYER_MATCH_LIST_STATS_USER_ID_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_LABEL_KEY = "PLAYER_MATCH_LIST_STATS_LABEL_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_MATCHES_KEY = "PLAYER_MATCH_LIST_STATS_MATCHES_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_ALTERNATE_TEXT_KEY = "PLAYER_MATCH_LIST_STATS_ALTERNATE_TEXT_KEY";

    private String label;
    private SteamUser user;
    private ArrayList<String> matchIds;
    private boolean alternateText;

    private boolean needsCalculation;
    private DotaStatisticsAdapter adapter;

    public static Bundle newBundleForUserAndMatches(String userId, String label, ArrayList<String> matchIds, boolean alternateText) {
        Bundle args = new Bundle();
        args.putString(PLAYER_MATCH_LIST_STATS_USER_ID_KEY, userId);
        args.putString(PLAYER_MATCH_LIST_STATS_LABEL_KEY, label);
        args.putStringArrayList(PLAYER_MATCH_LIST_STATS_MATCHES_KEY, matchIds);
        args.putBoolean(PLAYER_MATCH_LIST_STATS_ALTERNATE_TEXT_KEY, alternateText);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        needsCalculation = true;

        setHasOptionsMenu(true);

        String steamUserId = getArguments().getString(PLAYER_MATCH_LIST_STATS_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        label = getArguments().getString(PLAYER_MATCH_LIST_STATS_LABEL_KEY);
        matchIds = getArguments().getStringArrayList(PLAYER_MATCH_LIST_STATS_MATCHES_KEY);
        alternateText = getArguments().getBoolean(PLAYER_MATCH_LIST_STATS_ALTERNATE_TEXT_KEY);

        signalCharltonActivityToUpdateTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_statistics, container, false);

        setupListView(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (needsCalculation) {
            adapter.setNewStatistics(null, DotaStatistics.DotaStatisticsMode.ALL_FAVORITES);
            fetchStatistics();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (needsCalculation) {
            adapter.setNewStatistics(null, DotaStatistics.DotaStatisticsMode.ALL_FAVORITES);
        }
    }

    private void fetchStatistics() {
        needsCalculation = false;

        new AsyncTask<SteamUser, Void, DotaStatistics>() {
            @Override
            protected DotaStatistics doInBackground(SteamUser... params) {
                SteamUser user = params[0];
                LinkedList<Match> statsMatches = new LinkedList();

                //just use ALL matches from this list!
                for (String matchId : matchIds) {
                    Match match = Matches.get().getMatch(matchId);
                    statsMatches.add(match);
                }

                return new DotaStatistics(user, new LinkedList(statsMatches));
            }

            @Override
            protected void onPostExecute(DotaStatistics stats) {
                adapter.setCustomLabel(String.format(
                        getResources().getString(R.string.player_match_list_stats_tab_title_text),
                        label));
                adapter.setNewStatistics(stats, DotaStatistics.DotaStatisticsMode.CUSTOM_STATS);
            }
        }.execute(user);
    }

    private void setupListView(View view) {
        if (adapter == null) {
            adapter = new DotaStatisticsAdapter(getResources());
        }

        ListView listView = (ListView) view
                .findViewById(R.id.fragment_dota_player_statistics_list_view);
        listView.setAdapter(adapter);
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
    public String getCharltonMessageText(Resources resources) {
        if (user != null) {
            if (alternateText) {
                return String.format(resources
                                .getString(R.string.player_match_list_stats_tab_charlton_text_alternate),
                        user.getDisplayName(), label);
            }
            else {
                return String.format(resources
                                .getString(R.string.player_match_list_stats_tab_charlton_text),
                        user.getDisplayName(), label);
            }
        }
        else {
            return null;
        }
    }
}
