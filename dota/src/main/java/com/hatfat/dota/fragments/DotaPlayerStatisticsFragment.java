package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUserStatistics;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteItemRowView;

public class DotaPlayerStatisticsFragment extends Fragment {

    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY";

    private SteamUser user;
    private SteamUserStatistics statistics;

    private ListView favoriteItemsListView;
    private BaseAdapter favoriteItemsAdapter;

    private DotaPlayerStatisticsFragment() {

    }

    public static DotaPlayerStatisticsFragment newInstance(SteamUser user) {
        DotaPlayerStatisticsFragment newFragment = new DotaPlayerStatisticsFragment();

        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY, user.getSteamId());
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String steamUserId = getArguments().getString(DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        fetchStatistics();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_statistics, null);

        favoriteItemsListView = (ListView) view.findViewById(R.id.fragment_dota_player_statistics_favorite_items_list_view);
        favoriteItemsAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (statistics != null) {
                    return statistics.getFavoriteItems().size();
                }
                else {
                    return 0;
                }
            }

            @Override
            public SteamUserStatistics.ItemStats getItem(int position) {
                return statistics.getFavoriteItems().get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                SteamUserStatistics.ItemStats stats = getItem(position);

                DotaPlayerStatisticsFavoriteItemRowView statsView = (DotaPlayerStatisticsFavoriteItemRowView) convertView;

                if (statsView == null) {
                    statsView = new DotaPlayerStatisticsFavoriteItemRowView(parent.getContext());
                }

                statsView.setItemStats(stats);

                return statsView;
            }
        };

        favoriteItemsListView.setAdapter(favoriteItemsAdapter);

        return view;
    }

    private void fetchStatistics() {
        new AsyncTask<SteamUser, Void, SteamUserStatistics>() {
            @Override
            protected SteamUserStatistics doInBackground(SteamUser... params) {
                return new SteamUserStatistics(params[0]);
            }

            @Override
            protected void onPostExecute(SteamUserStatistics steamUserStatistics) {
                statistics = steamUserStatistics;
                updateViews();
            }
        }.execute(user);
    }

    private void updateViews() {
        favoriteItemsAdapter.notifyDataSetChanged();
    }
}
