package com.hatfat.dota.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;

/**
 * Created by scottrick on 2/16/14.
 */
public class MatchSummaryFragment extends CharltonFragment {

    private Match match;

    private TextView matchIdTextView;

    public MatchSummaryFragment(Match match) {
        this.match = match;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_summary, null);

        matchIdTextView = (TextView) view.findViewById(R.id.fragment_match_summary_match_id_text_view);

        updateViews();

        return view;
    }

    private void updateViews() {
        matchIdTextView.setText("Match " + match.getMatchId());
    }

    @Override
    public String getCharltonText() {
        return "Here's match " + match.getMatchId() + " that you asked for.";
    }
}
