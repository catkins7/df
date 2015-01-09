package com.hatfat.dota.tabs;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.hatfat.dota.fragments.CharltonFragment;

public class CharltonTab<T extends CharltonFragment> {

    private CharltonFragment mFragment;
    private final Class<T> mClass;
    private final Bundle mArgs;
    private final String mCharltonTabText;

    /** Constructor used each time a new tab is created.
     * @param charltonTabText  The tab's title text
     * @param clz  The fragment's Class, used to instantiate the fragment
     */
    public CharltonTab(Context context, String charltonTabText, Class<T> clz, Bundle args) {
        mClass = clz;
        mArgs = args;
        mCharltonTabText = charltonTabText;

        mFragment = (CharltonFragment) Fragment.instantiate(context, mClass.getName(), mArgs);
    }

    public CharltonFragment getFragment() {
        return mFragment;
    }

    public String getCharltonTabText() {
        return mCharltonTabText;
    }
}
