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
import android.widget.Button;
import android.widget.ListView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.SteamUserView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 2/10/14.
 */
public class DotaPlayerListFragment extends CharltonFragment {

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

        IntentFilter newUsersFilter = new IntentFilter(SteamUsers.STEAM_USERS_USER_LIST_CHANGED);
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
        View view = inflater.inflate(R.layout.fragment_dota_player_list, null);

        Button addNewPlayerButton = (Button) view.findViewById(R.id.dota_player_list_fragment_add_player);
        addNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCharltonActivity().pushCharltonFragment(new AddNewPlayerFragment());
            }
        });

        listView = (ListView) view.findViewById(R.id.dota_player_list_fragment_list_view);

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return sortedUsers.size();
            }

            @Override
            public Object getItem(int i) {
                return sortedUsers.get(i);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SteamUser steamUser = (SteamUser) listView.getAdapter().getItem(i);
                getCharltonActivity().pushCharltonFragment(new DotaPlayerSummaryFragment(steamUser));
            }
        });

        return view;
    }

    @Override
    public String getCharltonText() {
        return "Hello.  I'm Charlton Heston.";
    }
}
