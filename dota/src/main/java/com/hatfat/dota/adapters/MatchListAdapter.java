package com.hatfat.dota.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.view.MatchViewForPlayerBasic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class MatchListAdapter extends BaseAdapter {

    private SteamUser user;
    private List<Long> matchIds;
    private boolean fetchingMatches;

    public MatchListAdapter(SteamUser user) {
        this.user = user;
        this.matchIds = new LinkedList();
    }

    public void setMatches(long[] newMatchIds) {
        matchIds = new LinkedList();
        for (long id : newMatchIds) {
            matchIds.add(id);
        }

        Collections.sort(matchIds, Match.getMatchIdComparator());
        notifyDataSetChanged();
    }

    public void setMatches(TreeSet<Long> newMatchIds) {
        matchIds = new LinkedList(newMatchIds);
        Collections.sort(matchIds, Match.getMatchIdComparator());
        notifyDataSetChanged();
    }

    public void setMatches(List<Long> newMatchIds) {
        matchIds = new LinkedList(newMatchIds);
        Collections.sort(matchIds, Match.getMatchIdComparator());
        notifyDataSetChanged();
    }

    public void setFetchingMatches(boolean fetchingMatches) {
        this.fetchingMatches = fetchingMatches;
    }

    @Override
    public int getCount() {
        if (matchIds.size() <= 0) {
            return 1;
        }
        else {
            return matchIds.size();
        }
    }

    @Override
    public Match getItem(int i) {
        if (i < matchIds.size()) {
            return Matches.get().getMatch(matchIds.get(i));
        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (matchIds.size() <= 0) {
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
        Resources resources = viewGroup.getResources();

        if (matchIds.size() <= 0) {
            if (fetchingMatches) {
                LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                return inflater.inflate(R.layout.view_stats_loading_row, viewGroup, false);
            }
            else {
                TextView textView = new TextView(viewGroup.getContext());

                textView.setBackgroundResource(R.drawable.unselectable_background);
                textView.setText(R.string.no_matches);
                textView.setTextColor(resources.getColor(R.color.off_white));

                float fontSize = viewGroup.getResources().getDimensionPixelSize(R.dimen.font_size_medium);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

                int padding = (int) resources.getDimension(R.dimen.default_padding);
                textView.setPadding(padding, padding, padding, padding);

                return textView;
            }
        }

        Match match = getItem(i);
        match.getMatchDetailsIfNeededForUser(user);
        MatchViewForPlayerBasic matchView = (MatchViewForPlayerBasic) view;

        if (matchView == null) {
            matchView = new MatchViewForPlayerBasic(viewGroup.getContext());
        }

        matchView.setMatchAndUser(match, user);

        return matchView;
    }
}
