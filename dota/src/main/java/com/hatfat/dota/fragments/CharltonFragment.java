package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.content.Context;

import com.hatfat.dota.activities.CharltonActivity;

/**
 * Created by scottrick on 2/12/14.
 */
public abstract class CharltonFragment extends Fragment {
    public abstract String getCharltonMessageText(Context context);

    protected void signalCharltonActivityToUpdateTab() {
        if (getCharltonActivity() != null) {
            getCharltonActivity().signalUpdateActiveCharltonTab();
        }
    }

    protected CharltonActivity getCharltonActivity() {
        return (CharltonActivity) getActivity();
    }
}
