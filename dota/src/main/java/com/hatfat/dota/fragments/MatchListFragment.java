package com.hatfat.dota.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.view.MatchSimpleSummaryView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 3/14/14.
 */
public class MatchListFragment extends CharltonFragment {

    private static final String MATCH_LIST_FRAGMENT_ID_LIST_KEY = "MATCH_LIST_FRAGMENT_ID_LIST_KEY";

    private List<Match> matches;

    private BaseAdapter matchesAdapter;

    public static MatchListFragment newInstance(List<Match> matches) {
        MatchListFragment newFragment = new MatchListFragment();

        ArrayList<String> matchIds = new ArrayList<>();

        for (Match match : matches) {
            matchIds.add(match.getMatchId());
        }

        Bundle args = new Bundle();
        args.putStringArrayList(MATCH_LIST_FRAGMENT_ID_LIST_KEY, matchIds);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matches = new LinkedList<>();

        ArrayList<String> matchIds = getArguments().getStringArrayList(MATCH_LIST_FRAGMENT_ID_LIST_KEY);
        if (matchIds != null) {
            for (String matchId : matchIds) {
                Match match = Matches.get().getMatch(matchId);
                matches.add(match);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView matchesListView = (ListView) inflater
                .inflate(R.layout.fragment_match_list, container, false);

        matchesAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return matches.size();
            }

            @Override
            public Match getItem(int i) {
                return matches.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                Match match = getItem(i);

                MatchSimpleSummaryView matchView = (MatchSimpleSummaryView) view;

                if (matchView == null) {
                    matchView = new MatchSimpleSummaryView(viewGroup.getContext());
                }

                matchView.setMatch(match);

                return matchView;
            }
        };

        matchesListView.setAdapter(matchesAdapter);

        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = (Match) matchesAdapter.getItem(i);

                Log.e("catfat", "to fix");
//                getCharltonActivity().pushCharltonFragment(MatchSummaryFragment.newInstance(match));
            }
        });

        return matchesListView;
    }

    @Override
    public String getCharltonMessageText(Resources resources) {
        return "Here's a list of matches.";
    }
}
