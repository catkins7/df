package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
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
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.SteamUserView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SteamUserListFragment extends CharltonFragment {

    private static final String STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY = "STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY";
    private static final String STEAM_USER_LIST_FRAGMENT_MESSAGE_KEY = "STEAM_USER_LIST_FRAGMENT_MESSAGE_LIST_KEY";

    private BroadcastReceiver receiver;

    private List<SteamUser> steamUsers;
    private String message;

    private ListView usersListView;
    private BaseAdapter usersAdapter;

    public static Bundle newBundleForUserIdsWithMessage(ArrayList<String> userIds, String message) {
        Bundle args = new Bundle();
        args.putStringArrayList(STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY, userIds);
        args.putString(STEAM_USER_LIST_FRAGMENT_MESSAGE_KEY, message);
        return args;
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

        message = getArguments().getString(STEAM_USER_LIST_FRAGMENT_MESSAGE_KEY);

        ArrayList<String> steamIds = getArguments().getStringArrayList(STEAM_USER_LIST_FRAGMENT_ID_LIST_KEY);
        if (steamIds != null) {
            for (String steamId : steamIds) {
                SteamUser user = SteamUsers.get().getBySteamId(steamId);
                steamUsers.add(user);
            }
        }

        startListening();

        signalCharltonActivityToUpdateTab();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        usersListView = (ListView) inflater.inflate(R.layout.fragment_steam_user_list, container, false);

        usersAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (steamUsers.size() <= 0) {
                    return 1;
                }
                else {
                    return steamUsers.size();
                }
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
            public int getItemViewType(int position) {
                if (steamUsers.size() <= 0) {
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
                if (steamUsers.size() <= 0) {
                    //no users, so send "no users" row
                    TextView textView = new TextView(viewGroup.getContext());

                    textView.setBackgroundResource(R.drawable.unselectable_background);
                    textView.setText(R.string.no_steam_users);
                    textView.setTextColor(getResources().getColor(R.color.off_white));

                    float fontSize = getResources().getDimensionPixelSize(R.dimen.font_size_medium);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

                    int padding = (int)getResources().getDimension(R.dimen.default_padding);
                    textView.setPadding(padding, padding, padding, padding);

                    return textView;
                }

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
                if (steamUsers.size() > i) {
                    SteamUser steamUser = (SteamUser) usersAdapter.getItem(i);

                    Intent intent = PlayerActivity.intentForPlayer(getActivity().getApplicationContext(), steamUser.getSteamId());
                    startActivity(intent);
                }
            }
        });

        return usersListView;
    }

    @Override
    public String getCharltonMessageText(Context context) {
        if (message != null) {
            return message;
        }
        else {
            return context.getResources().getString(R.string.steam_user_list_default_charlton_text);
        }
    }
}
