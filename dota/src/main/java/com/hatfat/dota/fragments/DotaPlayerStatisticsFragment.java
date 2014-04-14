package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUserStatistics;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteHeroRowView;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteItemRowView;

public class DotaPlayerStatisticsFragment extends Fragment {

    private static final String DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_STATISTICS_FRAGMENT_STEAM_USER_ID_KEY";

    private SteamUser user;
    private SteamUserStatistics statistics;

    private ListView listView;
    private BaseAdapter adapter;

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

        setupListView(view);

        return view;
    }

    private void setupListView(View view) {
        listView = (ListView) view.findViewById(R.id.fragment_dota_player_statistics_list_view);

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (statistics != null) {
                    return statistics.getFavoriteHeroes().size() + statistics.getFavoriteItems().size() + 2;
                }
                else {
                    return 1;
                }
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getItemViewType(int position) {
                if (statistics == null) {
                    return 0; //nothing loaded yet, so textview
                }
                else if (position == 0 || position == statistics.getFavoriteHeroes().size() + 1) {
                    return 1; //textview title row
                }
                else if (position <= statistics.getFavoriteHeroes().size()) {
                    return 2; //favorite hero row
                }
                else {
                    return 3; //favorite item row!
                }
            }

            @Override
            public int getViewTypeCount() {
                return 4;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (statistics == null || position == 0 || position == statistics.getFavoriteHeroes().size() + 1) {
                    int textId = 0;

                    if (statistics == null) {
                        textId = R.string.player_statistics_loading_text;
                    }
                    else if (position == 0) {
                        textId = R.string.player_statistics_favorite_heroes_title_text;
                    }
                    else if (position == statistics.getFavoriteHeroes().size() + 1) {
                        textId = R.string.player_statistics_favorite_items_title_text;
                    }

                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.view_statistics_title_row, null);
                    }

                    TextView textView = (TextView) convertView.findViewById(R.id.view_statistics_title_row_text_view);
                    textView.setText(textId);

                    return convertView;
                }
                else if (position <= statistics.getFavoriteHeroes().size()) {
                    //hero row
                    int heroPosition = position - 1;
                    SteamUserStatistics.HeroStats stats = statistics.getFavoriteHeroes().get(heroPosition);

                    DotaPlayerStatisticsFavoriteHeroRowView statsView = (DotaPlayerStatisticsFavoriteHeroRowView) convertView;

                    if (statsView == null) {
                        statsView = new DotaPlayerStatisticsFavoriteHeroRowView(parent.getContext());
                    }

                    statsView.setHeroStats(stats);

                    return statsView;
                }
                else {
                    //item row
                    int itemPosition = position - 2 - statistics.getFavoriteHeroes().size();
                    SteamUserStatistics.ItemStats stats = statistics.getFavoriteItems().get(itemPosition);

                    DotaPlayerStatisticsFavoriteItemRowView statsView = (DotaPlayerStatisticsFavoriteItemRowView) convertView;

                    if (statsView == null) {
                        statsView = new DotaPlayerStatisticsFavoriteItemRowView(parent.getContext());
                    }

                    statsView.setItemStats(stats);

                    return statsView;
                }
            }
        };

        listView.setAdapter(adapter);
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
        adapter.notifyDataSetChanged();
    }
}
