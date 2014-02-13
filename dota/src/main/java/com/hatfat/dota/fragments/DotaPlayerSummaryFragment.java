package com.hatfat.dota.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hatfat.dota.R;
import com.hatfat.dota.model.Match;
import com.hatfat.dota.model.SteamUser;
import com.hatfat.dota.services.MatchHistoryFetcher;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class DotaPlayerSummaryFragment extends CharltonFragment {

    private SteamUser user;

    public DotaPlayerSummaryFragment(SteamUser user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_summary, null);

        TextView textView = (TextView) view.findViewById(R.id.fragment_dota_player_summary_text_view);
        textView.setText(user.getPersonaName());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        MatchHistoryFetcher.fetchMatches(user, new Callback<List<Match>>() {
            @Override
            public void success(List<Match> matches, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    @Override
    public String getCharltonText() {
        return "Here is " + user.getPersonaName() +"'s summary information.";
    }

    @Override
    public String toString() {
        return super.toString() + " [" + user.getPersonaName() +"]";
    }
}
