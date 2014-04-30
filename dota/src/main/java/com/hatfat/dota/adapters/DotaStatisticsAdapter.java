package com.hatfat.dota.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteHeroRowView;
import com.hatfat.dota.view.DotaPlayerStatisticsFavoriteItemRowView;

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
        SECTION_CSSCORE
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
            else if (this.type == StatsSectionType.SECTION_FAVORITE_HEROES) {
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

    private Resources resources;
    private List<StatsSection> sections;

    private DotaStatistics allMatchStats;
    private DotaStatistics rankedMatchStats;
    private DotaStatistics publicMatchStats;
    private DotaStatistics recentRankedMatchStats;

    public void setNewStatistics(List<DotaStatistics> statsList) {
        if (statsList == null) {
            //nulling out our stats, so return to the loading state
            allMatchStats = null;
            rankedMatchStats = null;
            publicMatchStats = null;
            recentRankedMatchStats = null;

            setupLoadingSections();

            return;
        }

        if (statsList.size() < 4) {
            Log.e(this.getClass().getSimpleName(), "Error calculating stats");

            allMatchStats = null;
            rankedMatchStats = null;
            publicMatchStats = null;
            recentRankedMatchStats = null;

            setupNoDataSections();

            return;
        }

        allMatchStats = statsList.get(0);
        rankedMatchStats = statsList.get(1);
        publicMatchStats = statsList.get(2);
        recentRankedMatchStats = statsList.get(3);

        if (allMatchStats.getGameCount() > 0 || rankedMatchStats.getGameCount() > 0 || publicMatchStats.getGameCount() > 0 || recentRankedMatchStats.getGameCount() > 0) {
            setupRealSections();
        }
        else {
            //we don't have any data!
            setupNoDataSections();
        }
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
        titleTextView.setText(R.string.no_matches);

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

    public View getViewForFavoriteHeroSection(int position, View convertView, ViewGroup parent, StatsSection section) {
        if (position == 0) {
            //title row
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            }

            prepareTextRow(convertView);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
            titleTextView.setText(R.string.player_statistics_favorite_heroes_title_text);

            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);
            subtitleTextView.setText(R.string.player_statistics_favorite_heroes_subtitle_text);

            return convertView;
        }
        else {
            //favorite hero row
            int heroPosition = position - 1;
            DotaStatistics.HeroStats stats = section.stats.getFavoriteHeroes().get(heroPosition);

            DotaPlayerStatisticsFavoriteHeroRowView statsView = (DotaPlayerStatisticsFavoriteHeroRowView) convertView;

            if (statsView == null) {
                statsView = new DotaPlayerStatisticsFavoriteHeroRowView(parent.getContext());
            }

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

            TextView subtitleTextView = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);
            subtitleTextView.setText(R.string.player_statistics_favorite_items_subtitle_text);

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
                subtitleTextView.setText(section.stats.getFavoriteGameModeString());
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
                return getViewForFavoriteHeroSection(sectionRow, convertView, parent, section);

            case SECTION_FAVORITE_ITEMS:
                return getViewForFavoriteItemSection(sectionRow, convertView, parent, section);

            case SECTION_MATCHES_SUMMARY:
                return getViewForMatchesSummarySection(sectionRow, convertView, parent, section);

            case SECTION_CSSCORE:
                return getViewForCSScoreSection(sectionRow, convertView, parent, section);

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

        if (rankedMatchStats.getGameCount() > 0) {
            sections.add(
                    new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, rankedMatchStats,
                            resources.getString(
                                    R.string.player_statistics_all_ranked_matches_title_text)));
            sections.add(
                    new StatsSection(StatsSectionType.SECTION_CSSCORE, rankedMatchStats));
        }

//        if (recentRankedMatchStats.getGameCount() > 0) {
//            sections.add(new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY,
//                    recentRankedMatchStats,
//                    resources.getString(R.string.player_statistics_recent_matches_title_text)));
//        }

        sections.add(new StatsSection(StatsSectionType.SECTION_FAVORITE_HEROES, allMatchStats));
        sections.add(new StatsSection(StatsSectionType.SECTION_FAVORITE_ITEMS, allMatchStats));

//        sections.add(new StatsSection(StatsSectionType.SECTION_GRAPHS, rankedMatchStats));

//        if (allMatchStats.getGameCount() > 0) {
//            sections.add(new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, allMatchStats,
//                    resources.getString(R.string.player_statistics_all_matches_title_text)));
//        }

        if (publicMatchStats.getGameCount() > 0) {
            sections.add(
                    new StatsSection(StatsSectionType.SECTION_MATCHES_SUMMARY, publicMatchStats,
                            resources.getString(
                                    R.string.player_statistics_all_public_matches_title_text)));
            sections.add(
                    new StatsSection(StatsSectionType.SECTION_CSSCORE, publicMatchStats));
        }

        notifyDataSetChanged();
    }
}
