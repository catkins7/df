package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.PlayerMatchListActivity;
import com.hatfat.dota.adapters.CommonMatchesAdapter;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.CommonMatches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.CommonMatchSteamUserView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class PlayerFriendsFragment extends CharltonFragment {

    private final static String PLAYER_FRIEND_FRAGMENT_STEAM_USER_ID_KEY = "PLAYER_FRIEND_FRAGMENT_STEAM_USER_ID_KEY";

    private BroadcastReceiver receiver;
    private boolean needsRecalculation;

    private SteamUser user;
    private CommonMatchesAdapter adapter;

    private ListView listView;

    public static Bundle newBundleForUser(String steamUserId) {
        Bundle args = new Bundle();
        args.putString(PLAYER_FRIEND_FRAGMENT_STEAM_USER_ID_KEY, steamUserId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String steamUserId = getArguments().getString(PLAYER_FRIEND_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        needsRecalculation = true;

        signalCharltonActivityToUpdateTab();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (needsRecalculation) {
            adapter.setCommonMatches(null);
            calculateFriendInfo();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (needsRecalculation) {
            adapter.setCommonMatches(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_friends, container, false);

        setupListView(view);

        return view;
    }

    private void setupListView(View view) {
        if (adapter == null) {
            adapter = new CommonMatchesAdapter();
        }

        listView = (ListView) view.findViewById(R.id.fragment_player_friends_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonMatches matches = adapter.getItem(position);
                String steamId = SteamUser.getSteamIdFromAccountId(matches.getUserOneAccountId());

                SteamUser otherUser = SteamUsers.get().getByAccountId(matches.getUserTwoAccountId());
                String label = otherUser.getDisplayName();

                Intent intent = PlayerMatchListActivity.intentForUserLabelAndMatches(getActivity().getApplicationContext(), steamId, label, otherUser.getAvatarFullUrl(),new ArrayList(matches.getCommonMatches()));
                startActivity(intent);
            }
        });
    }

    private void calculateFriendInfo() {
        needsRecalculation = false;

        new AsyncTask<SteamUser, Void, List<CommonMatches>>() {
            @Override
            protected List<CommonMatches> doInBackground(SteamUser... params) {
                SteamUser user = params[0];
                HashMap<String, CommonMatches> commonMatchesMap = new HashMap(); //steamUserId --> CommonMatches list

                TreeSet<String> matches = user.getMatches();

                for (String matchId : matches) {
                    Match match = Matches.get().getMatch(matchId);

                    if (!match.hasMatchDetails()) {
                        //skip any matches that have no details...
                        continue;
                    }

                    for (Player player : match.getPlayers()) {
                        String steamUserAccountId = String.valueOf(player.getAccountId());

                        //filter out players that are yourself
                        if (!steamUserAccountId.equals(user.getAccountId())) {
                            CommonMatches commonMatches = commonMatchesMap.get(steamUserAccountId);

                            if (commonMatches == null) {
                                commonMatches = new CommonMatches(user.getAccountId(), steamUserAccountId);
                                commonMatchesMap.put(steamUserAccountId, commonMatches);
                            }

                            commonMatches.addMatch(matchId);
                        }
                    }
                }

                List<CommonMatches> resultsList = new LinkedList(commonMatchesMap.values());
                Collections.sort(resultsList);

                int cutoffIndex = resultsList.size() / 60; //arbitrary position that seems to work well
                int cutoff = resultsList.get(cutoffIndex).getCommonMatches().size();

                List<CommonMatches> filteredList = new LinkedList();

                for (CommonMatches resultsMatches : resultsList) {
                    if (resultsMatches.getCommonMatches().size() <= cutoff) {
                        //we only care about players that we have more than the cutoff amount
                        continue;
                    }

                    //not make sure it is a real user (if its downloaded)
                    SteamUser matchUser = SteamUsers.get().getExistingUserByAccountId(
                            resultsMatches.getUserTwoAccountId());
                    if (matchUser != null && !matchUser.isRealUser()) {
                        //we don't care about fake users (Anonymous and Bots basically)
                        continue;
                    }

                    filteredList.add(resultsMatches);
                }

                for (CommonMatches filteredMatch : filteredList) {
                    //pre-calculate the strings
                    filteredMatch.calculateInfo();
                }

                return filteredList;
            }

            @Override
            protected void onPostExecute(List<CommonMatches> commonMatchesList) {
                adapter.setCommonMatches(commonMatchesList);
            }
        }.execute(user);
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUser.STEAM_USER_MATCHES_CHANGED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        needsRecalculation = true;
                    }
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        needsRecalculation = true;
                    }
                }
                else if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);

                    if (listView != null && adapter != null) {
                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View view = listView.getChildAt(i);

                            if (view instanceof CommonMatchSteamUserView) {
                                CommonMatchSteamUserView userView = (CommonMatchSteamUserView) view;

                                if (userView.getSteamUserAccountId().equals(updatedId)) {
                                    userView.notifyMatchUpdated();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter friendFilter = new IntentFilter();
        friendFilter.addAction(SteamUser.STEAM_USER_MATCHES_CHANGED);
        friendFilter.addAction(Match.MATCH_UPDATED);
        friendFilter.addAction(SteamUser.STEAM_USER_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                friendFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    @Override
    public String getCharltonMessageText(Resources resources) {
        if (user != null) {
            return String.format(resources.getString(R.string.player_friends_charlton_text),
                    user.getDisplayName());
        }
        else {
            return null;
        }
    }
}
