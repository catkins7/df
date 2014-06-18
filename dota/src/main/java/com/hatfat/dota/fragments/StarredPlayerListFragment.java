package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.CharltonActivity;
import com.hatfat.dota.activities.PlayerActivity;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.SteamUserView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StarredPlayerListFragment extends CharltonFragment {

    private ListView listView;
    private BaseAdapter listAdapter;

    private List<SteamUser> sortedUsers;

    private BroadcastReceiver receiver;

    public StarredPlayerListFragment() {
        sortedUsers = new LinkedList<>();
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUsers.STEAM_STARRED_USERS_USER_LIST_CHANGED)) {
                    updateUserListAndSort();
                }
                else if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);

                    if (listView != null && listAdapter != null) {
                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View view = listView.getChildAt(i);

                            if (view instanceof SteamUserView) {
                                SteamUserView userView = (SteamUserView) view;

                                if (userView.getSteamUserId().equals(updatedId)) {
                                    updateUserListAndSort();
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
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                filter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void updateUserListAndSort() {
        sortedUsers = new LinkedList<>();
        sortedUsers.addAll(SteamUsers.get().getStarredUsers());
        Collections.sort(sortedUsers, SteamUser.getComparator());

        if (listView != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        updateUserListAndSort();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        SteamUsers.get().requestStarredUsersSave();
    }

    @Override
    public void onResume() {
        super.onResume();

        SteamUsers.get().refreshUsers(sortedUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starred_player_list, container, false);

        listAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (sortedUsers.size() <= 0) {
                    return 1;
                }
                else {
                    return sortedUsers.size();
                }
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
            public int getItemViewType(int position) {
                if (sortedUsers.size() <= 0) {
                    return 0;
                }
                else {
                    return 1;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (sortedUsers.size() <= 0) {
                    //no users, so send "no users" row
                    TextView textView = new TextView(viewGroup.getContext());

                    textView.setBackgroundResource(R.drawable.off_black_background);
                    textView.setText(R.string.no_starred_users);
                    textView.setTextColor(getResources().getColor(R.color.off_white));

                    float fontSize = getResources().getDimensionPixelSize(R.dimen.font_size_medium);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

                    int padding = (int)getResources().getDimension(R.dimen.default_padding);
                    textView.setPadding(padding, padding, padding, padding);

                    return textView;
                }

                SteamUser user = (SteamUser) getItem(i);
                SteamUserView userView = (SteamUserView) view;

                if (userView == null) {
                    userView = new SteamUserView(viewGroup.getContext());
                }

                userView.setSteamUser(user);

                return userView;
            }
        };

        listView = (ListView) view.findViewById(R.id.dota_player_list_fragment_list_view);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (sortedUsers.size() > i) {
                    SteamUser steamUser = (SteamUser) listView.getAdapter().getItem(i);

                    Intent intent = PlayerActivity.intentForPlayer(getActivity().getApplicationContext(), steamUser.getSteamId());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.about_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu_about_id:
                showAboutDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        TextDialogHelper.showAboutDialog(getActivity());
    }

    @Override
    public String getCharltonMessageText(Context context) {
        Resources resources = context.getResources();

        String randomGreeting = resources.getString(CharltonActivity.getRandomHestonStringResource(context));
        return String.format(resources.getString(R.string.starred_players_charlton_text),
                randomGreeting);
    }
}
