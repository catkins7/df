package com.hatfat.dota.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;

/**
 * Created by scottrick on 2/16/14.
 */
public class MatchSummaryFragment extends CharltonFragment {

    private static final String MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY = "MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY";

    private Match match;

    private TextView matchIdTextView;

    public static MatchSummaryFragment newInstance(Match match) {
        MatchSummaryFragment newFragment = new MatchSummaryFragment();

        Bundle args = new Bundle();
        args.putString(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY, match.getMatchId());
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String matchId = getArguments().getString(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY);
        if (matchId != null) {
            match = Matches.get().getMatch(matchId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_summary, null);

        matchIdTextView = (TextView) view.findViewById(R.id.fragment_match_summary_match_id_text_view);

        updateViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        match.getMatchDetailsIfNeeded();
    }

    private void updateViews() {
        matchIdTextView.setText("Match " + match.getMatchId());
    }

    @Override
    public String getCharltonText() {
        return "Here's match " + match.getMatchId() + " that you asked for.";
    }
}
