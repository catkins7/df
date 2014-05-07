package com.hatfat.dota.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.fragments.CharltonFragment;
import com.hatfat.dota.fragments.LoadingFragment;
import com.hatfat.dota.fragments.StarredPlayerListFragment;
import com.hatfat.dota.util.CharltonBubbleDrawable;

/**
 * Created by scottrick on 2/12/14.
 */
public class OldCharltonActivity extends Activity {

    private FrameLayout fragmentContainer;

    boolean isPaused;

    private CharltonFragment rootContentFragment;
    private CharltonFragment topCharltonFragment;
    private Fragment drawerFragment;

    private TextView charltonTitleTextView;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FileUtil.dumpFileDirectoryContents();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_charlton);

        charltonTitleTextView = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_charlton_text_view);
        actionBar.getCustomView().setBackgroundDrawable(new CharltonBubbleDrawable());

        setContentView(R.layout.old_activity_charlton);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        setupDrawer();

        LoadingFragment loadingFragment = new LoadingFragment();
        setRootCharltonFragment(loadingFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        emptyDrawers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    //call when loading is finished
    public void startMainFragment() {
        StarredPlayerListFragment playerListFragment = new StarredPlayerListFragment();
        setRootCharltonFragment(playerListFragment);
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //lock the drawers initially
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.left_drawer_container));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.right_drawer_container));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                int width = drawerView.getWidth();
                int moveTo = (int) (width * slideOffset);

                if (isRightDrawerView(drawerView)) {
                    fragmentContainer.setTranslationX(-moveTo);
                }

                if (isLeftDrawerView(drawerView)) {
                    fragmentContainer.setTranslationX(moveTo);
                }

                if (drawerFragment instanceof DrawerLayout.DrawerListener) {
                    ((DrawerLayout.DrawerListener) drawerFragment)
                            .onDrawerSlide(drawerView, slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (drawerFragment instanceof DrawerLayout.DrawerListener) {
                    ((DrawerLayout.DrawerListener) drawerFragment).onDrawerOpened(drawerView);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (drawerFragment instanceof DrawerLayout.DrawerListener) {
                    ((DrawerLayout.DrawerListener) drawerFragment).onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

                if (drawerFragment instanceof DrawerLayout.DrawerListener) {
                    ((DrawerLayout.DrawerListener) drawerFragment).onDrawerStateChanged(newState);
                }
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void putFragmentInRightDrawer(Fragment fragment) {
        if (drawerFragment != null) {
            //drawer already has something in it, so we can't do anything
            return;
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.findViewById(R.id.right_drawer_container));
        drawerFragment = fragment;

        getFragmentManager().beginTransaction().add(R.id.right_drawer_container, drawerFragment).commit();
    }

    public void removeDrawerFragment() {
        emptyDrawers();
    }

    public void openRightDrawer() {
        drawerLayout.openDrawer(Gravity.END);
    }

    private void emptyDrawers() {
        drawerLayout.closeDrawers();

        getFragmentManager().beginTransaction().remove(drawerFragment);
        drawerFragment = null;

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.left_drawer_container));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.right_drawer_container));
    }

    private boolean isRightDrawerView(View drawerView) {
        return drawerView.getId() == R.id.right_drawer_container;
    }

    private boolean isLeftDrawerView(View drawerView) {
        return drawerView.getId() == R.id.left_drawer_container;
    }

    private void updateCharltonFragment(CharltonFragment charltonFragment) {
        topCharltonFragment = charltonFragment;

        getActionBar().setDisplayHomeAsUpEnabled(canPopFragment());

        if (charltonTitleTextView != null) {
            if (topCharltonFragment != null) {
                charltonTitleTextView.setText(topCharltonFragment.getCharltonMessageText(getResources()));
            } else {
                charltonTitleTextView.setText(R.string.default_charlton_text);
            }
        }
    }

    private void setRootCharltonFragment(final CharltonFragment fragment) {
        if (isPaused) {
            return;
        }

        if (rootContentFragment != fragment) {
            try {
                FragmentManager manager = getFragmentManager();

                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.popBackStack();

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, fragment.toString());
                transaction.commit();
                manager.executePendingTransactions();

                rootContentFragment = fragment;
                updateCharltonFragment(fragment);
            }
            catch (IllegalStateException e) {

            }
        }
    }

    public void pushCharltonFragment(final CharltonFragment fragment) {
        if (isPaused) {
            return;
        }

        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
//        transaction.setCustomAnimations(R.animator.from_right, R.animator.to_left, R.animator.from_left, R.animator.to_right);
        transaction.addToBackStack(fragment.toString());
        transaction.replace(R.id.fragment_container, fragment, fragment.toString());
        transaction.commit();
        manager.executePendingTransactions();

        updateCharltonFragment(fragment);
    }

    private boolean canPopFragment() {
        return getFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager manager = getFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            String name = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName();
            Fragment fragment = manager.findFragmentByTag(name);

            if (fragment instanceof CharltonFragment) {
                updateCharltonFragment((CharltonFragment) fragment);
            }
        }
        else {
            //we are on the root fragment
            updateCharltonFragment(rootContentFragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (canPopFragment()) {
                    onBackPressed();
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }
}
