package com.hatfat.dota.fragments;

import android.content.Context;
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
import com.hatfat.dota.activities.PlayerMatchListActivity;
import com.hatfat.dota.adapters.DotaStatisticsAdapter;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.LinkedList;
import java.util.List;

public class PlayerMatchListStatisticsFragment extends CharltonFragment {

    private static final String PLAYER_MATCH_LIST_STATS_USER_ID_KEY
                                                                  = "PLAYER_MATCH_LIST_STATS_USER_ID_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_LABEL_KEY
                                                                    = "PLAYER_MATCH_LIST_STATS_LABEL_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_MATCHES_KEY
                                                                      = "PLAYER_MATCH_LIST_STATS_MATCHES_KEY";
    private static final String PLAYER_MATCH_LIST_STATS_TEXT_MODE_KEY
                                                                      = "PLAYER_MATCH_LIST_STATS_TEXT_MODE_KEY";

    private String                                    label;
    private SteamUser                                 user;
    private List<Long>                                matchIds;
    private PlayerMatchListActivity.MatchListTextMode mode;

    private boolean               needsCalculation;
    private DotaStatisticsAdapter adapter;

    public static Bundle newBundleForUserAndMatches(String userId, String label,
            long[] matchIds, PlayerMatchListActivity.MatchListTextMode textMode) {
        Bundle args = new Bundle();
        args.putString(PLAYER_MATCH_LIST_STATS_USER_ID_KEY, userId);
        args.putString(PLAYER_MATCH_LIST_STATS_LABEL_KEY, label);
        args.putLongArray(PLAYER_MATCH_LIST_STATS_MATCHES_KEY, matchIds);
        args.putInt(PLAYER_MATCH_LIST_STATS_TEXT_MODE_KEY, textMode.mode);
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

        long[] matchIdsRaw = getArguments().getLongArray(PLAYER_MATCH_LIST_STATS_MATCHES_KEY);
        matchIds = new LinkedList();
        for (long id : matchIdsRaw) {
            matchIds.add(id);
        }

        label = getArguments().getString(PLAYER_MATCH_LIST_STATS_LABEL_KEY);
        mode = PlayerMatchListActivity.MatchListTextMode
                .fromInt(getArguments().getInt(PLAYER_MATCH_LIST_STATS_TEXT_MODE_KEY));

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
                for (Long matchId : matchIds) {
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
    public String getCharltonMessageText(Context context) {
        if (user != null) {
            switch (mode) {
                case ALTERNATE_MODE:
                    return String.format(context.getResources()
                                    .getString(R.string.player_match_list_stats_tab_charlton_text_alternate),
                            user.getDisplayName(), label);
                case MATCH_UP_MODE:
                    return String.format(context.getResources()
                                    .getString(R.string.player_match_list_stats_tab_charlton_text_match_up),
                            user.getDisplayName(), label);
                case NORMAL_MODE:
                default:
                    return String.format(context.getResources()
                                    .getString(R.string.player_match_list_stats_tab_charlton_text),
                            user.getDisplayName(), label);
            }
        }
        else {
            return null;
        }
    }
}
