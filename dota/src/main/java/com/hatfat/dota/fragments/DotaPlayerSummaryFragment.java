package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.services.MatchFetcher;
import com.hatfat.dota.view.MatchViewForPlayerBasic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by scottrick on 2/12/14.
 */
public class DotaPlayerSummaryFragment extends CharltonFragment {

    private static final String DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY";

    private SteamUser user;

    private BroadcastReceiver receiver;

    private TextView personaTextView;
    private TextView currentStateTextView;
    private ImageView profileImageView;
    private ListView matchesListView;

    private BaseAdapter matchesAdapter;
    private ArrayList<String> sortedMatches;

    public static DotaPlayerSummaryFragment newInstance(SteamUser user) {
        DotaPlayerSummaryFragment newFragment = new DotaPlayerSummaryFragment();

        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY, user.getSteamId());
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String steamUserId = getArguments().getString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_summary, null);

        personaTextView = (TextView) view.findViewById(R.id.fragment_dota_player_summary_persona_text_view);
        currentStateTextView = (TextView) view.findViewById(R.id.fragment_dota_player_summary_current_state_text_view);
        profileImageView = (ImageView) view.findViewById(R.id.fragment_dota_player_summary_user_image_view);
        matchesListView = (ListView) view.findViewById(R.id.fragment_dota_player_summary_matches_list_view);

        updateMatchList();
        setupMatchesList();
        updateViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        MatchFetcher.fetchMatches(user);
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
                if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        updateMatchList();
                        updateViews();
                    }
                }
                else if (intent.getAction().equals(Heroes.HERO_DATA_UPDATED_NOTIFICATION)) {
                    //reload the match rows with the updated icons
                    if (matchesListView != null && matchesAdapter != null) {
                        matchesAdapter.notifyDataSetChanged();
                    }
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);

                    if (matchesListView != null && matchesAdapter != null) {
                        for (int i = 0; i < matchesListView.getChildCount(); i++) {
                            View view = matchesListView.getChildAt(i);

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
        summaryFilter.addAction(Heroes.HERO_DATA_UPDATED_NOTIFICATION);
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void setupMatchesList() {
        matchesAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sortedMatches.size();
            }

            @Override
            public Object getItem(int i) {
                return Matches.get().getMatch(sortedMatches.get(i));
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                Match match = (Match) getItem(i);

                match.getMatchDetailsIfNeeded();

                MatchViewForPlayerBasic matchView = (MatchViewForPlayerBasic) view;

                if (matchView == null) {
                    matchView = new MatchViewForPlayerBasic(viewGroup.getContext());
                }

                matchView.setMatchAndUser(match, user);

                return matchView;
            }
        };

        matchesListView.setAdapter(matchesAdapter);

        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = (Match) matchesAdapter.getItem(i);
                getCharltonActivity().pushCharltonFragment(MatchSummaryFragment.newInstance(match));
            }
        });
    }

    private void updateMatchList() {
        sortedMatches = new ArrayList<>();
        sortedMatches.addAll(user.getMatches());
        Collections.sort(sortedMatches, Match.getMatchIdComparator());

        if (matchesListView != null && matchesAdapter != null) {
            matchesAdapter.notifyDataSetChanged();
        }
    }

    private void updateViews() {
        personaTextView.setText(user.getPersonaName());
        currentStateTextView.setText(user.getCurrentStateDescriptionString());
        Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(profileImageView);
    }

    @Override
    public String getCharltonText() {
        return "Here is " + user.getPersonaName() +"'s summary information.";
    }
}
