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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.MatchActivity;
import com.hatfat.dota.adapters.MatchListAdapter;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.MatchViewForPlayerBasic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlayerMatchListFragment extends CharltonFragment {

    private static String PLAYER_MATCH_LIST_USER_ID_KEY = "PLAYER_MATCH_LIST_USER_ID_KEY";
    private static String PLAYER_MATCH_LIST_LABEL_KEY = "PLAYER_MATCH_LIST_LABEL_KEY";
    private static String PLAYER_MATCH_LIST_MATCHES_KEY = "PLAYER_MATCH_LIST_MATCHES_KEY";

    private BroadcastReceiver receiver;

    private SteamUser user;
    private List<String> matchIds;
    private String matchesLabel;

    private ListView matchListView;
    private MatchListAdapter matchAdapter;

    private TextView userNameTextView;
    private TextView labelTextView;
    private TextView winPercentTextView;
    private TextView gameCountTextView;
    private ImageView userIconImageView;

    public static Bundle newBundleForUserAndMatches(String userId, String label, ArrayList<String> matchIds) {
        Bundle args = new Bundle();
        args.putString(PLAYER_MATCH_LIST_USER_ID_KEY, userId);
        args.putString(PLAYER_MATCH_LIST_LABEL_KEY, label);
        args.putStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY, matchIds);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = getArguments().getString(PLAYER_MATCH_LIST_USER_ID_KEY);

        user = SteamUsers.get().getBySteamId(userId);
        matchIds = getArguments().getStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY);
        matchesLabel = getArguments().getString(PLAYER_MATCH_LIST_LABEL_KEY);

        signalCharltonActivityToUpdateTab();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        updateViews();
                    }
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        updateViews();
                    }

                    if (matchAdapter != null && matchAdapter != null) {
                        for (int i = 0; i < matchListView.getChildCount(); i++) {
                            View view = matchListView.getChildAt(i);

                            if (view instanceof MatchViewForPlayerBasic) {
                                MatchViewForPlayerBasic matchView = (MatchViewForPlayerBasic) view;

                                if (matchView.getMatch().getMatchId().equals(updatedMatchId)) {
                                    matchView.notifyMatchUpdated();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(SteamUser.STEAM_USER_UPDATED);
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_match_list, container, false);

        userNameTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_name_text_view);
        labelTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_label_text_view);
        winPercentTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_matches_text_view);
        gameCountTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_third_row_text_view);
        userIconImageView = (ImageView) view.findViewById(R.id.fragment_player_match_list_image_view);

        matchAdapter = new MatchListAdapter(user);
        matchAdapter.setMatches(matchIds);

        matchListView = (ListView) view.findViewById(R.id.fragment_player_match_list_matches_list_view);

        matchListView.setAdapter(matchAdapter);
        matchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = matchAdapter.getItem(i);
                Intent intent = MatchActivity
                        .intentForMatch(getActivity().getApplicationContext(), match.getMatchId());
                startActivity(intent);
            }
        });

        updateViews();

        return view;
    }

    private void updateViews() {
        if (userIconImageView == null) {
            return;
        }

        Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(userIconImageView);

        userNameTextView.setText(user.getDisplayName());
        labelTextView.setText(matchesLabel);
        winPercentTextView.setText(String.format(getResources().getString(R.string.player_match_list_win_rate_text), getWinPercentage()));
        gameCountTextView.setText(String.format(getResources().getString(R.string.player_match_list_match_count_text), matchIds.size()));
    }

    private float getWinPercentage() {
        int winCount = 0;
        int matchCount = 0;

        for (String matchId : matchIds) {
            Match match = Matches.get().getMatch(matchId);

            if (match.hasMatchDetails()) {
                matchCount++;
            }
            else {
                continue;
            }

            Match.PlayerMatchResult result = match.getPlayerMatchResultForPlayer(match.getPlayerForSteamUser(user));

            if (result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                winCount++;
            }
        }

        if (matchCount != matchIds.size()) {
            //shouldn't happen, not all data is available (probably because we went out of memory)
            return -1.0f;
        }

        return (float)winCount / (float)matchCount * 100.0f;
    }

    @Override
    public String getCharltonMessageText(Resources resources) {
        if (user != null) {
            return String.format(resources.getString(R.string.player_match_list_charlton_text),
                    user.getDisplayName(), matchesLabel);
        }
        else {
            return null;
        }
    }
}
