package com.hatfat.dota.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import com.hatfat.dota.R;
import com.hatfat.dota.fragments.CharltonFragment;
import com.hatfat.dota.fragments.CharltonMessageFragment;
import com.hatfat.dota.fragments.DotaPlayerListFragment;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.game.Items;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUsers;

/**
 * Created by scottrick on 2/12/14.
 */
public class CharltonActivity extends Activity {

    private FrameLayout fragmentContainer;

    private CharltonMessageFragment messageFragment;
    private CharltonFragment rootContentFragment;
    private Fragment drawerFragment;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charlton);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        setupDrawer();

        messageFragment = new CharltonMessageFragment();
        getFragmentManager().beginTransaction().add(R.id.charlton_fragment_container_view, messageFragment).commit();

        DotaPlayerListFragment playerListFragment = new DotaPlayerListFragment();
        setRootCharltonFragment(playerListFragment);

        Items.get().init(getResources());
        Heroes.get().init();
        SteamUsers.get().init(); //initialize the SteamUsers singleton
        Matches.get().init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //lock the drawers initially
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.left_drawer_container));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.right_drawer_container));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_closed) {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                int width = drawerView.getWidth();
                int moveTo = (int) (width * slideOffset);

                if (isRightDrawerView(drawerView)) {
                    fragmentContainer.setTranslationX(-moveTo);
                }

                if (isLeftDrawerView(drawerView)) {
                    fragmentContainer.setTranslationX(moveTo);
                }
            }

            @Override public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                emptyDrawers();

                //drawers are closed, so lock them again
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.left_drawer_container));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.right_drawer_container));
            }

            @Override public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void showFragmentInLeftDrawer(Fragment fragment) {
        if (drawerFragment != null) {
            //drawer is already open, so can't show something else!
            return;
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.findViewById(R.id.left_drawer_container));
        drawerFragment = fragment;

        getFragmentManager().beginTransaction().add(R.id.left_drawer_container, drawerFragment).commit();
        drawerLayout.openDrawer(Gravity.START);
    }

    public void showFragmentInRightDrawer(Fragment fragment) {
        if (drawerFragment != null) {
            //drawer is already open, so can't show something else!
            return;
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.findViewById(R.id.right_drawer_container));
        drawerFragment = fragment;

        getFragmentManager().beginTransaction().add(R.id.right_drawer_container, drawerFragment).commit();
        drawerLayout.openDrawer(Gravity.END);
    }

    private void emptyDrawers() {
        if (drawerFragment == null) {
            return;
        }

        getFragmentManager().beginTransaction().remove(drawerFragment);
        drawerFragment = null;
    }

    private boolean isRightDrawerView(View drawerView) {
        return drawerView.getId() == R.id.right_drawer_container;
    }

    private boolean isLeftDrawerView(View drawerView) {
        return drawerView.getId() == R.id.left_drawer_container;
    }

    private void setRootCharltonFragment(final CharltonFragment fragment) {
        if (rootContentFragment != fragment) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, fragment.toString());
            transaction.commit();
            manager.executePendingTransactions();

            messageFragment.setCharltonObject(fragment);
        }

        rootContentFragment = fragment;
    }

    public void pushCharltonFragment(final CharltonFragment fragment) {
        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
//        transaction.setCustomAnimations(R.animator.from_right, R.animator.to_left, R.animator.from_left, R.animator.to_right);
        transaction.addToBackStack(fragment.toString());
        transaction.replace(R.id.fragment_container, fragment, fragment.toString());
        transaction.commit();
        manager.executePendingTransactions();

        messageFragment.setCharltonObject(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager manager = getFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            String name = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName();
            Fragment fragment = manager.findFragmentByTag(name);

            if (fragment instanceof CharltonFragment) {
                messageFragment.setCharltonObject((CharltonFragment)fragment);
            }
        }
        else {
            //we are on the root fragment
            messageFragment.setCharltonObject(rootContentFragment);
        }
    }
}
