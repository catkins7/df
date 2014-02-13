package com.hatfat.dota.fragments;

import android.app.Activity;
import android.app.Fragment;
import com.hatfat.dota.activities.CharltonActivity;
import com.hatfat.dota.charlton.CharltonMessageInterface;

/**
 * Created by scottrick on 2/12/14.
 */
public abstract class CharltonFragment extends Fragment implements CharltonMessageInterface {
    protected CharltonActivity getCharltonActivity() {
        Activity activity = getActivity();

        if (activity != null && activity instanceof CharltonActivity) {
            return (CharltonActivity)activity;
        }

        return null;
    }
}
