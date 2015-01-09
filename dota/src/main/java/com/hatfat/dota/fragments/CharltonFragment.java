package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.content.Context;

import com.hatfat.dota.activities.CharltonActivity;

public abstract class CharltonFragment extends Fragment {
    public abstract String getCharltonMessageText(Context context);

    /* fragments can override this if they need notice when their tab is selected */
    public void tabWasForegrounded() {

    }

    protected void signalCharltonActivityToUpdateTab() {
        if (getCharltonActivity() != null) {
            getCharltonActivity().signalUpdateActiveCharltonTab();
        }
    }

    protected CharltonActivity getCharltonActivity() {
        return (CharltonActivity) getActivity();
    }
}
