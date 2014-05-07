package com.hatfat.dota.tabs;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.hatfat.dota.activities.CharltonActivity;
import com.hatfat.dota.fragments.CharltonFragment;

public class CharltonTab<T extends CharltonFragment> implements ActionBar.TabListener{

    private CharltonFragment mFragment;
    private final CharltonActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private final Bundle mArgs;
    private final String mCharltonTabText;

    /** Constructor used each time a new tab is created.
     * @param activity  The host Activity, used to instantiate the fragment
     * @param charltonTabText  The tab's title text
     * @param clz  The fragment's Class, used to instantiate the fragment
     */
    public CharltonTab(CharltonActivity activity, String charltonTabText, Class<T> clz, Bundle args) {
        mActivity = activity;
        mTag = charltonTabText; //just use the same text for now?
        mClass = clz;
        mArgs = args;
        mCharltonTabText = charltonTabText;
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = (CharltonFragment) Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }

        mActivity.updateWithCharltonTab(this);
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

    public void attach(FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = (CharltonFragment) Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }

        mActivity.updateWithCharltonTab(this);
    }

    public void detach(FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    public CharltonFragment getFragment() {
        return mFragment;
    }

    public String getCharltonTabText() {
        return mCharltonTabText;
    }
}
