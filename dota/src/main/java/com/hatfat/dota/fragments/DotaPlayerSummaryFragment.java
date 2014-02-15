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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.SteamUser;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.services.MatchHistoryFetcher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by scottrick on 2/12/14.
 */
public class DotaPlayerSummaryFragment extends CharltonFragment {

    private SteamUser user;

    private BroadcastReceiver receiver;

    private TextView personaTextView;
    private TextView currentStateTextView;
    private ImageView profileImageView;
    private ListView matchesListView;

    private BaseAdapter matchesAdapter;
    private ArrayList<String> sortedMatches;

    public DotaPlayerSummaryFragment(SteamUser user) {
        this.user = user;
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

        MatchHistoryFetcher.fetchMatches(user);
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
                String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                if (updatedId.equals(user.getSteamId())) {
                    updateMatchList();
                    updateViews();
                }
            }
        };

        IntentFilter newUsersFilter = new IntentFilter(SteamUser.STEAM_USER_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, newUsersFilter);
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

                TextView textView = new TextView(DotaFriendApplication.CONTEXT);
                textView.setText(match.getMatchId());

                return textView;
            }
        };

        matchesListView.setAdapter(matchesAdapter);
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

    @Override
    public String toString() {
        return super.toString() + " [" + user.getPersonaName() +"]";
    }
}
