package com.hatfat.dota.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.PlayerMatchListActivity;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteHeroRowView;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteItemRowView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DotaStatisticsAdapter extends BaseAdapter {

    private enum StatsSectionRowType {
        ROW_FAVORITE_HERO(0),
        ROW_FAVORITE_ITEM(1),
        ROW_LOADING(2),
        ROW_TEXT(3),
        ROW_SPACER(4),

        ROW_NO_REUSE(5),
        ROW_NUMBER_OF_TYPES(6);

        private int type;

        StatsSectionRowType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private enum StatsSectionType {
        SECTION_LOADING,
        SECTION_NO_DATA,
        SECTION_MATCHES_SUMMARY,
        SECTION_FAVORITE_HEROES,
        SECTION_FAVORITE_ITEMS,
        SECTION_GRAPHS,
        SECTION_CSSCORE,
        SECTION_MODE_INFO,
        SECTION_MOST_SUCCESS,
        SECTION_LEAST_SUCCESS,
        SECTION_ALL_HEROES
    }

    private class StatsSection {
        public StatsSectionType type;
        public DotaStatistics stats;
        public String titleText;

        public StatsSection(StatsSectionType type, DotaStatistics stats) {
            this.type = type;
            this.stats = stats;
        }

        public StatsSection(StatsSectionType type, DotaStatistics stats, String titleText) {
            this.type = type;
            this.stats = stats;
            this.titleText = titleText;
        }

        public int getRowCount() {
            switch (this.type) {
                case SECTION_LOADING:
                    return 1;
                case SECTION_NO_DATA:
                    return 1;
                case SECTION_MATCHES_SUMMARY:
                    return 12;
                case SECTION_ALL_HEROES:
                    if (stats.getAllHeroes().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getAllHeroes().size() + 1;
                    }
                case SECTION_FAVORITE_HEROES:
                    if (stats.getFavoriteHeroes().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getFavoriteHeroes().size() + 1;
                    }
                case SECTION_FAVORITE_ITEMS:
                    if (stats.getFavoriteItems().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getFavoriteItems().size() + 1;
                    }
                case SECTION_MOST_SUCCESS:
                    if (stats.getMostSuccessfulHeroes().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getMostSuccessfulHeroes().size() + 1;
                    }
                case SECTION_LEAST_SUCCESS:
                    if (stats.getLeastSuccessfulHeroes().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getLeastSuccessfulHeroes().size() + 1;
                    }
                case SECTION_MODE_INFO:
                    if (stats.getFavoriteGameModes().size() <= 0) {
                        return 0;
                    }
                    else {
                        return stats.getFavoriteGameModes().size() + 1;
                    }
                case SECTION_GRAPHS:
                    return 1;
                case SECTION_CSSCORE:
                    return 2;
                default:
                    return 0;
            }
        }

        public StatsSectionRowType getViewTypeForPosition(int position) {
            if (this.type == StatsSectionType.SECTION_LOADING) {
                return StatsSectionRowType.ROW_LOADING;
            }
            else if (this.type == StatsSectionType.SECTION_NO_DATA) {
                return StatsSectionRowType.ROW_TEXT;
            }
            else if (this.type == StatsSectionType.SECTION_MATCHES_SUMMARY) {
                return StatsSectionRowType.ROW_TEXT;
            }
            else if (this.type == StatsSectionType.SECTION_MODE_INFO) {
                return StatsSectionRowType.ROW_TEXT;
            }
            else if (
                    this.type == StatsSectionType.SECTION_MOST_SUCCESS || this.type == StatsSectionType.SECTION_LEAST_SUCCESS ||
                    this.type == StatsSectionType.SECTION_FAVORITE_HEROES || this.type == StatsSectionType.SECTION_ALL_HEROES) {

                if (position == 0) {
                    return StatsSectionRowType.ROW_TEXT;
                }
                else {
                    return StatsSectionRowType.ROW_FAVORITE_HERO;
                }
            }
            else if (this.type == StatsSectionType.SECTION_FAVORITE_ITEMS) {
                if (position == 0) {
                    return StatsSectionRowType.ROW_TEXT;
                }
                else {
                    return StatsSectionRowType.ROW_FAVORITE_ITEM;
                }
            }
            else if (this.type == StatsSectionType.SECTION_GRAPHS) {
                return StatsSectionRowType.ROW_NO_REUSE;
            }
            else if (this.type == StatsSectionType.SECTION_CSSCORE) {
                return StatsSectionRowType.ROW_TEXT;
            }

            //default I suppose
            return StatsSectionRowType.ROW_NO_REUSE;
        }
    }

    public DotaStatisticsAdapter(Resources resources) {
        this.resources = resources;

        setupLoadingSections();
    }

    private String customLabel; //pretty hacky...

    private Resources resources;
    private List<StatsSection> sections;

    private DotaStatistics dotaStatistics;
    private DotaStatistics.DotaStatisticsMode statsMode;

    public void setNewStatistics(DotaStatistics newStats, DotaStatistics.DotaStatisticsMode newMode) {
        statsMode = newMode;

        if (newStats == null) {
            //removing our stats, so return to the loading state
            dotaStatistics = null;

            setupLoadingSections();

            return;
        }

        dotaStatistics = newStats;

        if (dotaStatistics.getGameCount() > 0) {
            setupRealSections();
        }
        else {
            //we don't have any data!
            setupNoDataSections();
        }
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    @Override
    public int getCount() {
        int count = 0;

        for (StatsSection section : sections) {
            count += section.getRowCount();
        }

        return count;
    }

    @Override
    public boolean isEnabled(int position) {
        StatsSection section = sectionForPosition(position);
        int sectionRow = rowForPosition(position);

        //only rows that can be selected are the hero rows, and the mode rows
        switch (section.type) {
            case SECTION_MODE_INFO:
            case SECTION_ALL_HEROES:
            case SECTION_FAVORITE_HEROES:
            case SECTION_LEAST_SUCCESS:
            case SECTION_MOST_SUCCESS:
                return sectionRow > 0;
        }

        return false; //no row highlighting
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
        StatsSection section = sectionForPosition(position);
        int row = rowForPosition(position);

        return section.getViewTypeForPosition(row).getType();
    }

    @Override
    public int getViewTypeCount() {
        return StatsSectionRowType.ROW_NUMBER_OF_TYPES.getType();
    }

    public View getViewForNoDataSection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
        }

        prepareTextRow(convertView);

        TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
        titleTextView.setText(R.string.player_statistics_not_enough_data_text);

        convertView.setBackgroundColor(0x0);

        return convertView;
    }

    public View getViewForLoadingSection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_stats_loading_row, parent, false);
        }

        return convertView;
    }

    public View getViewForHeroSection(int position, View convertView, ViewGroup parent, List<DotaStatistics.HeroStats> heroStats, String titleText) {
        if (position == 0) {
            //title row
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            titleTextView.setText(titleText);

            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);
            subtitleTextView.setText(R.string.player_statistics_favorite_heroes_subtitle_text);

            return convertView;
        }
        else {
            //favorite hero row
            int heroPosition = position - 1;
            final DotaStatistics.HeroStats stats = heroStats.get(heroPosition);

            DotaPlayerStatisticsFavoriteHeroRowView statsView = (DotaPlayerStatisticsFavoriteHeroRowView) convertView;

            if (statsView == null) {
                statsView = new DotaPlayerStatisticsFavoriteHeroRowView(parent.getContext());
            }

            statsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> matchIds = new ArrayList(stats.getMatchIds());
                    Intent intent = PlayerMatchListActivity.intentForUserLabelAndMatches(
                            v.getContext(), dotaStatistics.getSteamUser().getSteamId(), stats.hero.getLocalizedName(), stats.hero.getFullVerticalPortraitUrl(),matchIds);
                    v.getContext().startActivity(intent);
                }
            });

            statsView.setHeroStats(stats);

            return statsView;
        }
    }

    public View getViewForFavoriteItemSection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (position == 0) {
            //title row
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            titleTextView.setText(R.string.player_statistics_favorite_items_title_text);

            return convertView;
        }
        else {
            //favorite item row
            int itemPosition = position - 1;
            DotaStatistics.ItemStats stats = section.stats.getFavoriteItems().get(itemPosition);

            DotaPlayerStatisticsFavoriteItemRowView statsView = (DotaPlayerStatisticsFavoriteItemRowView) convertView;

            if (statsView == null) {
                statsView = new DotaPlayerStatisticsFavoriteItemRowView(parent.getContext());
            }

            statsView.setItemStats(stats);

            return statsView;
        }
    }

    public View getViewForModeSummarySection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (position == 0) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            titleTextView.setText(section.titleText);

            return convertView;
        }
        else {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);

            subtitleTextView.setTextColor(resources.getColor(R.color.off_white));

            final DotaStatistics.ModeStats modeStats = dotaStatistics.getFavoriteGameModes().get(position - 1);

            String modePrefix = this.statsMode.getModePrefixString(resources);
            final String label = modePrefix == null ? modeStats.mode.getGameModeString(resources) : modePrefix + " " + modeStats.mode.getGameModeString(resources);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> matchIds = new ArrayList(modeStats.getMatchIds());
                    Intent intent = PlayerMatchListActivity.intentForUserLabelAndMatches(
                            v.getContext(), dotaStatistics.getSteamUser().getSteamId(), label, null, matchIds);
                    v.getContext().startActivity(intent);
                }
            });

            titleTextView.setText(modeStats.mode.getGameModeString(resources));
            subtitleTextView.setText(modeStats.getSummaryString(resources));

            convertView.setBackgroundResource(R.drawable.off_black_button_background);

            return convertView;
        }
    }

    public View getViewForMatchesSummarySection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (position == 0) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            titleTextView.setText(section.titleText);

            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);
            subtitleTextView.setText(R.string.player_statistics_averages_text);

            return convertView;
        }
        else {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);

            subtitleTextView.setTextColor(resources.getColor(R.color.off_white));

            if (position == 1) {
                titleTextView.setText(R.string.player_statistics_game_count_title_text);
                subtitleTextView.setText(section.stats.getGameCountString());
            }
            else if (position == 2) {
                titleTextView.setText(R.string.player_statistics_favorite_game_mode_title_text);
                subtitleTextView.setText(section.stats.getFavoriteGameModeString(resources));
            }
            else if (position == 3) {
                titleTextView.setText(R.string.player_statistics_win_percent_title_text);
                subtitleTextView.setText(section.stats.getWinPercentString(resources));
            }
            else if (position == 4) {
                titleTextView.setText(R.string.player_statistics_avg_kda_title_text);
                subtitleTextView.setText(section.stats.getAvgKDAString(resources));
            }
            else if (position == 5) {
                titleTextView.setText(R.string.player_statistics_avg_kills_over_deaths_title_text);
                subtitleTextView.setText(section.stats.getAverageKillsOverDeathsString(resources));
            }
            else if (position == 6) {
                titleTextView.setText(R.string.player_statistics_avg_kills_and_assists_over_deaths_title_text);
                subtitleTextView.setText(section.stats.getAverageKillsAndAssistsOverDeathsString(
                        resources));
            }
            else if (position == 7) {
                titleTextView.setText(R.string.player_statistics_avg_last_hits_title_text);
                subtitleTextView.setText(section.stats.getAverageLastHitsString(resources));
            }
            else if (position == 8) {
                titleTextView.setText(R.string.player_statistics_avg_denies_title_text);
                subtitleTextView.setText(section.stats.getAverageDeniesString(resources));
            }
            else if (position == 9) {
                titleTextView.setText(R.string.player_statistics_avg_gpm_title_text);
                subtitleTextView.setText(section.stats.getAverageGpmString(resources));
            }
            else if (position == 10) {
                titleTextView.setText(R.string.player_statistics_avg_xpm_title_text);
                subtitleTextView.setText(section.stats.getAverageXpmString(resources));
            }
            else if (position == 11) {
                titleTextView.setText(R.string.player_statistics_avg_duration_title_text);
                subtitleTextView.setText(section.stats.getAvgDurationString(resources));
            }

            convertView.setBackgroundColor(0x0);

            return convertView;
        }
    }

    public View getViewForCSScoreSection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
        }

        prepareTextRow(convertView);

        TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
        TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);

        convertView.setBackgroundColor(0x0);
        subtitleTextView.setTextColor(resources.getColor(R.color.off_white));

        if (position == 0) {
            //CS score
            titleTextView.setText(R.string.player_statistics_cs_score_text);
            subtitleTextView.setText(section.stats.getCsScoreString());
        }
//        else if (position == 1) {
//            //gpm score
//            titleTextView.setText(R.string.player_statistics_gpm_score_text);
//            subtitleTextView.setText(section.stats.getGpmScoreString());
//        }
//        else if (position == 2) {
//            //xpm score
//            titleTextView.setText(R.string.player_statistics_xpm_score_text);
//            subtitleTextView.setText(section.stats.getXpmScoreString());
//        }
//        else if (position == 3) {
//            //composite score
//            titleTextView.setText(R.string.player_statistics_xpm_gpm_composite_score_text);
//            subtitleTextView.setText(section.stats.getCompositeScoreString());
//        }
        else if (position == 1) {
            //teamwork score
            titleTextView.setText(R.string.player_statistics_teamwork_score_text);
            subtitleTextView.setText(section.stats.getTeamworkScoreString(resources));
        }

        return convertView;
    }

    //view must be view_stats_text_row
    private void prepareTextRow(View convertView) {
        TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
        titleTextView.setTextColor(resources.getColor(R.color.off_white));
        titleTextView.setText(null);

        TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);
        subtitleTextView.setTextColor(resources.getColor(R.color.off_black));
        subtitleTextView.setText(null);

        convertView.setOnClickListener(null);
        convertView.setBackgroundResource(R.drawable.black_gold_background);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StatsSection section = sectionForPosition(position);
        int sectionRow = rowForPosition(position);

        TextView textView;

        switch (section.type) {
            case SECTION_NO_DATA:
                return getViewForNoDataSection(sectionRow, convertView, parent, section);

            case SECTION_LOADING:
                return getViewForLoadingSection(sectionRow, convertView, parent, section);

            case SECTION_FAVORITE_HEROES:
                return getViewForHeroSection(sectionRow, convertView, parent, section.stats.getFavoriteHeroes(), resources.getString(R.string.player_statistics_favorite_heroes_title_text));

            case SECTION_FAVORITE_ITEMS:
                return getViewForFavoriteItemSection(sectionRow, convertView, parent, section);

            case SECTION_MATCHES_SUMMARY:
                return getViewForMatchesSummarySection(sectionRow, convertView, parent, section);

            case SECTION_MODE_INFO:
                return getViewForModeSummarySection(sectionRow, convertView, parent, section);

            case SECTION_CSSCORE:
                return getViewForCSScoreSection(sectionRow, convertView, parent, section);

            case SECTION_ALL_HEROES:
                return getViewForHeroSection(sectionRow, convertView, parent, section.stats.getAllHeroes(), resources.getString(R.string.tab_player_all_heroes_stats_title));

            case SECTION_MOST_SUCCESS:
                return getViewForHeroSection(sectionRow, convertView, parent,
                        section.stats.getMostSuccessfulHeroes(), resources
                        .getString(R.string.player_statistics_most_success_heroes_title_text));

            case SECTION_LEAST_SUCCESS:
                return getViewForHeroSection(sectionRow, convertView, parent, section.stats.getLeastSuccessfulHeroes(), resources.getString(R.string.player_statistics_least_success_heroes_title_text));

            default:
                textView = new TextView(parent.getContext());
                textView.setPadding(12, 12, 12, 12);
                textView.setTextColor(0xffffffff);
                textView.setTextSize(18.0f);
                textView.setText("Not Implemented");
                return textView;
        }
    }

    private StatsSection sectionForPosition(int position) {
        for (StatsSection section : sections) {
            if (position < section.getRowCount()) {
                return section;
            }

            position -= section.getRowCount();
        }

        return null;
    }

    private int rowForPosition(int position) {
        for (StatsSection section : sections) {
            if (position < section.getRowCount()) {
                return position;
            }

            position -= section.getRowCount();
        }

        return -1;
    }

    private void setupLoadingSections() {
        sections = new LinkedList();
        sections.add(new StatsSection(StatsSectionType.SECTION_LOADING, null));

        notifyDataSetChanged();
    }

    private void setupNoDataSections() {
        sections = new LinkedList();
        sections.add(new StatsSection(StatsSectionType.SECTION_NO_DATA, null));

        notifyDataSetChanged();
    }

    private void setupRealSections() {
        sections = new LinkedList();

        switch (statsMode) {
            case ALL_FAVORITES:
                sections.add(new StatsSection(StatsSectionType.SECTION_FAVORITE_HEROES, dotaStatistics));
                sections.add(new StatsSection(StatsSectionType.SECTION_FAVORITE_ITEMS, dotaStatistics));
                break;
            case ALL_SUCCESS_STATS:
                if (dotaStatistics.getMostSuccessfulHeroes().size() <= 0 && dotaStatistics.getLeastSuccessfulHeroes().size() <= 0) {
                    sections.add(new StatsSection(StatsSectionType.SECTION_NO_DATA, dotaStatistics));
                }
                else {
                    sections.add(new StatsSection(StatsSectionType.SECTION_MOST_SUCCESS,
                            dotaStatistics));
                    sections.add(new StatsSection(StatsSectionType.SECTION_LEAST_SUCCESS,
                            dotaStatistics));
                }
                break;
            case ALL_HEROES:
                sections.add(new StatsSection(StatsSectionType.SECTION_ALL_HEROES, dotaStatistics));
                break;
            case RANKED_STATS:
                sections.add(
                        new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, dotaStatistics,
                                resources.getString(
                                        R.string.player_statistics_all_ranked_matches_title_text)));
//                sections.add(
//                        new StatsSection(StatsSectionType.SECTION_CSSCORE, dotaStatistics));
                sections.add(new StatsSection(StatsSectionType.SECTION_MODE_INFO, dotaStatistics,
                        resources.getString(R.string.player_statistics_modes_info_title_text)));
                break;
            case PUBLIC_STATS:
                sections.add(
                        new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, dotaStatistics,
                                resources.getString(
                                        R.string.player_statistics_all_public_matches_title_text)));
//                sections.add(
//                        new StatsSection(StatsSectionType.SECTION_CSSCORE, dotaStatistics));
                sections.add(new StatsSection(StatsSectionType.SECTION_MODE_INFO, dotaStatistics,
                        resources.getString(R.string.player_statistics_modes_info_title_text)));
                break;
            case OTHER_STATS:
                sections.add(
                        new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, dotaStatistics,
                                resources.getString(
                                        R.string.player_statistics_other_matches_title_text)));
//                sections.add(
//                        new StatsSection(StatsSectionType.SECTION_CSSCORE, dotaStatistics));
                sections.add(new StatsSection(StatsSectionType.SECTION_MODE_INFO, dotaStatistics,
                        resources.getString(R.string.player_statistics_modes_info_title_text)));
                break;
            case CUSTOM_STATS:
                sections.add(
                        new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, dotaStatistics, customLabel));
//                sections.add(
//                        new StatsSection(StatsSectionType.SECTION_CSSCORE, dotaStatistics));
                break;
        }

        notifyDataSetChanged();
    }
}
