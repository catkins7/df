package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.content.res.Resources;

import com.hatfat.dota.activities.CharltonActivity;

/**
 * Created by scottrick on 2/12/14.
 */
public abstract class CharltonFragment extends Fragment {
    public abstract String getCharltonMessageText(Resources resources);

    protected CharltonActivity getCharltonActivity() {
        return (CharltonActivity) getActivity();
    }
}
