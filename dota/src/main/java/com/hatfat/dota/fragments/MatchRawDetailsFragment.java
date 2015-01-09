package com.hatfat.dota.fragments;

import android.content.Context;
import android.os.Bundle;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
/**
 * Created by scottrick on 9/3/14.
 */
public class MatchRawDetailsFragment extends CharltonFragment {

    private static final String MATCH_RAW_DETAILS_FRAGMENT_MATCH_ID_KEY
            = "MATCH_RAW_DETAILS_FRAGMENT_MATCH_ID_KEY";

    private Match match;

    public static Bundle newBundleForMatch(Long matchId) {
        Bundle args = new Bundle();
        args.putLong(MATCH_RAW_DETAILS_FRAGMENT_MATCH_ID_KEY, matchId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long matchId = getArguments().getLong(MATCH_RAW_DETAILS_FRAGMENT_MATCH_ID_KEY);

        if (matchId == null) {
            throw new RuntimeException("must be created with a match id");
        }

        match = Matches.get().getMatch(matchId);

        signalCharltonActivityToUpdateTab();
    }

    @Override public String getCharltonMessageText(Context context) {
        if (match != null) {
            return String.format(context.getResources()
                    .getString(R.string.match_raw_details_charlton_text), match.getMatchId());
        }

        return null;
    }
}
