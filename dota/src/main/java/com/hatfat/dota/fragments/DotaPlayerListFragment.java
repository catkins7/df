package com.hatfat.dota.fragments;

import android.app.Fragment;
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
import android.widget.ListView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.SteamUser;
import com.hatfat.dota.model.SteamUsers;
import com.hatfat.dota.view.SteamUserView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaPlayerListFragment extends Fragment {

    ListView listView;

    List<SteamUser> sortedUsers;

    BroadcastReceiver receiver;

    public DotaPlayerListFragment() {
        sortedUsers = new LinkedList<>();
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserList();
            }
        };

        IntentFilter newUsersFilter = new IntentFilter(SteamUsers.STEAM_USERS_NEW_USERS_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, newUsersFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void updateUserList() {
        sortedUsers = new LinkedList<>();
        sortedUsers.addAll(SteamUsers.get().getUsers());
        Collections.sort(sortedUsers, SteamUser.getComparator());

        if (listView != null) {
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateUserList();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.dota_player_list_fragment, null);

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return sortedUsers.size() * 5;
            }

            @Override
            public Object getItem(int i) {
                return sortedUsers.get(i % 5);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                SteamUser user = (SteamUser) getItem(i);

                SteamUserView userView = (SteamUserView) view;

                if (userView == null) {
                    userView = new SteamUserView(viewGroup.getContext());
                }

                userView.setSteamUser(user);

                return userView;
            }
        });

        return listView;
    }
}
