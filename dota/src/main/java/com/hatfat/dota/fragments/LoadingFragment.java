package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.StarredUsersActivity;
import com.hatfat.dota.model.game.Abilities;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.game.Items;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUsers;

public class LoadingFragment extends CharltonFragment {

    private BroadcastReceiver receiver;

    private FrameLayout percentFrameLayout;

    private float heroProgress;
    private float itemsProgress;
    private float usersProgress;
    private float matchesProgress;
    private float abilitiesProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        percentFrameLayout = (FrameLayout) view.findViewById(R.id.fragment_loading_percent_frame_layout);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        updateProgressBar();
        load();
    }

    private void load() {
//        FileUtil.dumpFileDirectoryContents();

        loadItems();
        loadHeroes();
        loadUsers();
        loadAbilities();
    }

    private void loadItems() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isAdded()) {
                    Items.get().load(getResources());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                itemsProgress = 1.0f;
                updateProgressBar();
            }
        }.execute();
    }

    private void loadHeroes() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Heroes.get().load();
                return null;
            }
        }.execute();
    }

    private void loadUsers() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SteamUsers.get().load(getResources()); //initialize the SteamUsers singleton
                return null;
            }
        }.execute();
    }

    private void loadMatches() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Matches.get().load();
                return null;
            }
        }.execute();
    }

    private void loadAbilities() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isAdded()) {
                    Abilities.get().load(getResources());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                abilitiesProgress = 1.0f;
                updateProgressBar();
            }
        }.execute();
    }

    private void updateProgressBar() {
        if (!isAdded()) {
            return;
        }

        float barPercent = 0.0f;
        barPercent += heroProgress * 0.1f;
        barPercent += itemsProgress * 0.05f;
        barPercent += usersProgress * 0.05f;
        barPercent += abilitiesProgress * 0.1f;
        barPercent += matchesProgress * 0.7f;

        float loadingBarMaxWidth = getResources().getDimensionPixelSize(R.dimen.loading_bar_width);
        final int barWidth = (int)(loadingBarMaxWidth * barPercent);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentFrameLayout.getLayoutParams().width = barWidth;
                percentFrameLayout.requestLayout();
            }
        });

        if (heroProgress == 1.0f
                && itemsProgress == 1.0f
                && usersProgress == 1.0f
                && matchesProgress == 1.0f
                && abilitiesProgress == 1.0f) {

            finishedLoading();
        }
    }

    private void finishedLoading() {
        Intent starredPlayersIntent = new Intent(getActivity().getApplicationContext(), StarredUsersActivity.class);
        startActivity(starredPlayersIntent);
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Heroes.HERO_DATA_LOADED_NOTIFICATION)) {
                    heroProgress = 1.0f;
                    updateProgressBar();
                }
                else if (intent.getAction().equals(SteamUsers.STEAM_USERS_LOADED_FROM_DISK)) {
                    usersProgress = 1.0f;
                    updateProgressBar();

                    loadMatches();
                }
                else if (intent.getAction().equals(Matches.MATCHES_LOADING_PROGRESS_NOTIFICATION)) {
                    matchesProgress = intent.getFloatExtra(Matches.MATCHES_LOADING_PERCENT_COMPLETE, 0.0f);
                    updateProgressBar();
                }
                else if (intent.getAction().equals(Abilities.ABILITY_DATA_LOADED_NOTIFICATION)) {
                    abilitiesProgress = 1.0f;
                    updateProgressBar();
                }
            }
        };

        IntentFilter loadingFilter = new IntentFilter();
        loadingFilter.addAction(Heroes.HERO_DATA_LOADED_NOTIFICATION);
        loadingFilter.addAction(SteamUsers.STEAM_USERS_LOADED_FROM_DISK);
        loadingFilter.addAction(Matches.MATCHES_LOADING_PROGRESS_NOTIFICATION);
        loadingFilter.addAction(Abilities.ABILITY_DATA_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                loadingFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    @Override
    public String getCharltonMessageText(Context context) {
        return null;
    }
}
