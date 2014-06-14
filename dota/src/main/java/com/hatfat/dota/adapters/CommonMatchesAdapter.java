package com.hatfat.dota.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.user.CommonMatches;
import com.hatfat.dota.view.CommonMatchSteamUserView;

import java.util.List;

public class CommonMatchesAdapter extends BaseAdapter {

    private List<CommonMatches> commonMatches;

    public void setCommonMatches(List<CommonMatches> newCommonMatches) {
        this.commonMatches = newCommonMatches;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (commonMatches == null) {
            return 1;
        }
        else if (commonMatches.size() <= 0) {
            return 1;
        }
        else {
            return commonMatches.size();
        }
    }

    @Override
    public CommonMatches getItem(int position) {
        return commonMatches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (commonMatches == null) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (commonMatches == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.view_stats_loading_row, parent, false);
        }
        else if (commonMatches.size() <= 0) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.view_stats_text_row, parent, false);
            view.setBackgroundResource(R.drawable.off_black_background);
            TextView textView = (TextView) view.findViewById(R.id.view_stats_text_row_title_text_view);
            textView.setText(R.string.player_statistics_not_enough_data_text);

            return view;
        }

        CommonMatches matches = getItem(position);
        CommonMatchSteamUserView userView = (CommonMatchSteamUserView) convertView;

        if (convertView == null) {
            userView = new CommonMatchSteamUserView(parent.getContext());
        }

        userView.setCommonMatches(matches);

        return userView;
    }
}
