package com.hatfat.dota.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hatfat.dota.R;

/**
 * Created by scottrick on 2/12/14.
 */
public class AddNewPlayerFragment extends CharltonFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_player, null);

        return view;
    }

    @Override
    public String getCharltonText() {
        return "Let me help you add a new player.";
    }
}
