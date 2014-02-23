package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.view.PlayerRowView;

/**
 * Created by scottrick on 2/16/14.
 */
public class MatchSummaryFragment extends CharltonFragment {

    private static final String MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY = "MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY";

    private BroadcastReceiver receiver;

    private Match match;

    private TextView matchIdTextView;
    private TextView gameModeTextView;
    private TextView victoryTextView;
    private TextView lobbyTypeTextView;
    private TextView timeAgoTextView;
    private TextView durationTextView;

    private ListView playersListView;
    private BaseAdapter playersAdapter;

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
        gameModeTextView = (TextView) view.findViewById(R.id.fragment_match_summary_game_mode_text_view);
        victoryTextView = (TextView) view.findViewById(R.id.fragment_match_summary_victory_text_view);
        lobbyTypeTextView = (TextView) view.findViewById(R.id.fragment_match_summary_lobby_text_view);
        timeAgoTextView = (TextView) view.findViewById(R.id.fragment_match_summary_time_ago_text_view);
        durationTextView = (TextView) view.findViewById(R.id.fragment_match_summary_duration_text_view);

        playersListView = (ListView) view.findViewById(R.id.fragment_match_summary_players_list_view);

        setupPlayerList();
        updateViews();

        return view;
    }

    private void setupPlayerList() {
        playersAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return match.getPlayers().size();
            }

            @Override
            public Object getItem(int i) {
                return match.getPlayers().get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                Player player = (Player) getItem(i);

                PlayerRowView playerView = (PlayerRowView) view;

                if (playerView == null) {
                    playerView = new PlayerRowView(viewGroup.getContext());
                }

                playerView.setPlayer(player);

                return playerView;
            }
        };

        playersListView.setAdapter(playersAdapter);

        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Player player = (Player) playersAdapter.getItem(i);
                Log.e("MatchSummaryFragment", "Pressed accountId " + player.getAccountId());
//                getCharltonActivity().pushCharltonFragment(MatchSummaryFragment.newInstance(match));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        match.getMatchDetailsIfNeeded();
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

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);

                    if (match.getMatchId().equals(updatedMatchId)) {
                        updateViews();
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void updateViews() {
        matchIdTextView.setText("Match " + match.getMatchId());
        victoryTextView.setText(match.getMatchResult().getDescriptionStringResourceId());
        victoryTextView.setTextColor(getResources().getColor(match.getMatchResult().getColorResourceId()));
        lobbyTypeTextView.setText(match.getLobbyTypeString());
        timeAgoTextView.setText(match.getTimeAgoString());
        durationTextView.setText(match.getDurationString());
        gameModeTextView.setText(match.getGameModeString());


    }

    @Override
    public String getCharltonText() {
        return "Here's match " + match.getMatchId() + " that you asked for.";
    }
}
