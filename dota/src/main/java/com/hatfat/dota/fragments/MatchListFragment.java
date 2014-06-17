package com.hatfat.dota.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.MatchActivity;
import com.hatfat.dota.adapters.MatchListAdapter;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.ArrayList;

/**
 * Created by scottrick on 3/14/14.
 */
public class MatchListFragment extends CharltonFragment {

    private static final String MATCH_LIST_FRAGMENT_MATCHES_ID_LIST_KEY = "MATCH_LIST_FRAGMENT_MATCHES_ID_LIST_KEY";
    private static final String MATCH_LIST_FRAGMENT_STEAM_USER_ID_KEY = "MATCH_LIST_FRAGMENT_STEAM_USER_ID_KEY";

    private MatchListAdapter matchesAdapter;

    public static Bundle newBundleForMatchIdsAndUser(ArrayList<String> matchIds, String steamUserId) {
        Bundle args = new Bundle();
        args.putStringArrayList(MATCH_LIST_FRAGMENT_MATCHES_ID_LIST_KEY, matchIds);
        args.putString(MATCH_LIST_FRAGMENT_STEAM_USER_ID_KEY, steamUserId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> matchIds = getArguments().getStringArrayList(MATCH_LIST_FRAGMENT_MATCHES_ID_LIST_KEY);
        String steamUserId = getArguments().getString(MATCH_LIST_FRAGMENT_STEAM_USER_ID_KEY);

        SteamUser user = SteamUsers.get().getBySteamId(steamUserId);

        matchesAdapter = new MatchListAdapter(user);
        matchesAdapter.setMatches(matchIds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView matchesListView = (ListView) inflater
                .inflate(R.layout.fragment_match_list, container, false);

        matchesListView.setAdapter(matchesAdapter);

        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = matchesAdapter.getItem(i);
                Intent intent = MatchActivity.intentForMatch(getActivity().getApplicationContext(), match.getMatchId());
                startActivity(intent);
            }
        });

        return matchesListView;
    }

    @Override
    public String getCharltonMessageText(Context context) {
        return null;
    }
}
