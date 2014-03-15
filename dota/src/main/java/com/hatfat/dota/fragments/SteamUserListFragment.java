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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.SteamUserView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 3/14/14.
 */
public class SteamUserListFragment extends CharltonFragment {

    private static final String STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY = "STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY";
    private static final String STEAM_USER_LIST_FRAGMENT_MESSAGE_LIST_KEY = "STEAM_USER_LIST_FRAGMENT_MESSAGE_LIST_KEY";

    private BroadcastReceiver receiver;

    private List<SteamUser> steamUsers;
    private String message;

    private ListView usersListView;
    private BaseAdapter usersAdapter;

    public static SteamUserListFragment newInstance(List<SteamUser> users, String message) {
        SteamUserListFragment newFragment = new SteamUserListFragment();

        ArrayList<String> steamIds = new ArrayList<>();

        for (SteamUser user : users) {
            steamIds.add(user.getSteamId());
        }

        Bundle args = new Bundle();
        args.putStringArrayList(STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY, steamIds);
        args.putString(STEAM_USER_LIST_FRAGMENT_MESSAGE_LIST_KEY, message);
        newFragment.setArguments(args);

        return newFragment;
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);

                    if (usersListView != null && usersAdapter != null) {
                        for (int i = 0; i < usersListView.getChildCount(); i++) {
                            View view = usersListView.getChildAt(i);

                            if (view instanceof SteamUserView) {
                                SteamUserView userView = (SteamUserView) view;

                                if (userView.getSteamUserId().equals(updatedId)) {
                                    userView.notifyMatchUpdated();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(SteamUsers.STEAM_STARRED_USERS_USER_LIST_CHANGED);
        filter.addAction(SteamUser.STEAM_USER_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, filter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        steamUsers = new LinkedList<>();

        message = getArguments().getString(STEAM_USER_LIST_FRAGMENT_MESSAGE_LIST_KEY);
        ArrayList<String> steamIds = getArguments().getStringArrayList(STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY);
        if (steamIds != null) {
            for (String steamId : steamIds) {
                SteamUser user = SteamUsers.get().getBySteamId(steamId);
                steamUsers.add(user);
            }
        }

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        usersListView = (ListView) inflater.inflate(R.layout.fragment_steam_user_list, null);

        usersAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return steamUsers.size();
            }

            @Override
            public SteamUser getItem(int i) {
                return steamUsers.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                SteamUser user = getItem(i);
                SteamUserView userView = (SteamUserView) view;

                if (userView == null) {
                    userView = new SteamUserView(viewGroup.getContext());
                }

                userView.setSteamUser(user);

                return userView;
            }
        };

        usersListView.setAdapter(usersAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SteamUser steamUser = (SteamUser) usersAdapter.getItem(i);
                getCharltonActivity().pushCharltonFragment(DotaPlayerSummaryFragment.newInstance(steamUser));
            }
        });

        return usersListView;
    }

    @Override
    public String getCharltonText() {
        if (message != null) {
            return message;
        }
        else {
            return "Here's a list of Steam users.";
        }
    }
}