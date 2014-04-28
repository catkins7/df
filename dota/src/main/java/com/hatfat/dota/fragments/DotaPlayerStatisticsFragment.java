package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hatfat.dota.R;
import com.hatfat.dota.adapters.DotaStatisticsAdapter;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.LinkedList;
import java.util.List;

public class DotaPlayerStatisticsFragment extends Fragment {

    public final static int RECENT_STATS_MATCH_COUNT = 100;

    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY";

    private SteamUser user;

    private ListView listView;
    private DotaStatisticsAdapter adapter;

    private DotaPlayerStatisticsFragment() {

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

        String steamUserId = getArguments().getString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        fetchStatistics();
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
        new AsyncTask<SteamUser, Void, List<DotaStatistics>>() {
            @Override
            protected List<DotaStatistics> doInBackground(SteamUser... params) {
                SteamUser user = params[0];
                List<Match> allMatches = new LinkedList();
                List<Match> rankedMatches = new LinkedList();
                List<Match> publicMatches = new LinkedList();
                List<Match> recentRankedMatches = new LinkedList();

                for (String matchId : user.getMatches()) {
                    Match match = Matches.get().getMatch(matchId);

                    if (!match.shouldBeUsedInStatistics()) {
                        continue;
                    }

                    //add to the all matches list
                    allMatches.add(match);

                    if (match.isRankedMatchmaking()) {
                        rankedMatches.add(match);

                        if (recentRankedMatches.size() < RECENT_STATS_MATCH_COUNT) {
                            recentRankedMatches.add(match);
                        }
                    }

                    if (match.isPublicMatchmaking()) {
                        publicMatches.add(match);
                    }
                }

                Log.e("catfat", allMatches.size() + " in allMatches");
                Log.e("catfat", rankedMatches.size() + " in rankedMatches");
                Log.e("catfat", publicMatches.size() + " in publicMatches");
                Log.e("catfat", recentRankedMatches.size() + " in recentRankedMatches");

                LinkedList<DotaStatistics> stats = new LinkedList();
                stats.add(new DotaStatistics(user, allMatches));
                stats.add(new DotaStatistics(user, rankedMatches));
                stats.add(new DotaStatistics(user, publicMatches));
                stats.add(new DotaStatistics(user, recentRankedMatches));

                return stats;
            }

            @Override
            protected void onPostExecute(List<DotaStatistics> statsList) {
                adapter.setNewStatistics(statsList);
            }
        }.execute(user);
    }
}
