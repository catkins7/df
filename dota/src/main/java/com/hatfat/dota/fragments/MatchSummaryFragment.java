package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.PlayerActivity;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.view.PlayerRowView;

/**
 * Created by scottrick on 2/16/14.
 */
public class MatchSummaryFragment extends CharltonFragment {

    private static final String MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY = "MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY";

    private BroadcastReceiver receiver;

    private Match match;

    private TextView gameModeTextView;
    private TextView victoryTextView;
    private TextView timeAgoTextView;
    private TextView durationTextView;
    private TextView radiantKillsTextView;
    private TextView direKillsTextView;

    private ListView playersListView;
    private BaseAdapter playersAdapter;

    public static Bundle newBundleForMatch(String matchId) {
        Bundle args = new Bundle();
        args.putString(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY, matchId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String matchId = getArguments().getString(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY);

        if (matchId == null) {
            throw new RuntimeException("must be created with a match id");
        }

        match = Matches.get().getMatch(matchId);

        signalCharltonActivityToUpdateTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_summary, container, false);

        gameModeTextView = (TextView) view.findViewById(R.id.fragment_match_summary_game_mode_text_view);
        victoryTextView = (TextView) view.findViewById(R.id.fragment_match_summary_victory_text_view);
        timeAgoTextView = (TextView) view.findViewById(R.id.fragment_match_summary_time_ago_text_view);
        durationTextView = (TextView) view.findViewById(R.id.fragment_match_summary_duration_text_view);
        radiantKillsTextView = (TextView) view.findViewById(R.id.fragment_match_summary_radiant_kills_text_view);
        direKillsTextView = (TextView) view.findViewById(R.id.fragment_match_summary_dire_kills_text_view);
        playersListView = (ListView) view.findViewById(R.id.fragment_match_summary_players_list_view);

        if (match.hasMatchDetails()) {
            setupPlayerList();
            updateViews();
        }

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
                SteamUser user = player.getSteamUser();

                Intent intent = PlayerActivity.intentForPlayer(getActivity().getApplicationContext(), user.getSteamId());
                startActivity(intent);
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
                else if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);

                    if (playersListView != null && playersAdapter != null) {
                        for (int i = 0; i < playersListView.getChildCount(); i++) {
                            View view = playersListView.getChildAt(i);

                            if (view instanceof PlayerRowView) {
                                PlayerRowView playerRowView = (PlayerRowView) view;

                                if (playerRowView.getPlayer().getSteamUser().getSteamId().equals(updatedId)) {
                                    playerRowView.notifyPlayerUpdated();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(Match.MATCH_UPDATED);
        summaryFilter.addAction(SteamUser.STEAM_USER_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void updateViews() {
        victoryTextView.setText(match.getMatchResult().getDescriptionStringResourceId());
        victoryTextView.setBackgroundResource(match.getMatchResult().getBackgroundResourceId());
        timeAgoTextView.setText(match.getTimeAgoString());
        durationTextView.setText(match.getDurationString());
        gameModeTextView.setText(match.getGameModeString());
        radiantKillsTextView.setText(String.valueOf(match.getDireTotalDeathCount()));
        direKillsTextView.setText(String.valueOf(match.getRadiantTotalDeathCount()));

        if (match.isRankedMatchmaking()) {
            gameModeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ranked_icon), null);
            gameModeTextView.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.default_padding));
        }
        else {
            gameModeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            gameModeTextView.setCompoundDrawablePadding(0);
        }

        playersAdapter.notifyDataSetChanged();
    }

    @Override
    public String getCharltonMessageText(Resources resources) {
        if (match != null) {
            return String.format(resources.getString(R.string.match_summary_charlton_text), match.getMatchId());
        }

        return null;
    }
}
