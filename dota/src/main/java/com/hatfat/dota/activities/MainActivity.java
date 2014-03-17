package com.hatfat.dota.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.hatfat.dota.R;
import com.hatfat.dota.fragments.StarredPlayerListFragment;
import com.hatfat.dota.fragments.WelcomeFragment;

public class MainActivity extends Activity {

    private FrameLayout fragmentContainer;

    private Fragment contentFragment;
    private Fragment leftDrawerFragment;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        setupDrawer();

        leftDrawerFragment = new StarredPlayerListFragment();
        getFragmentManager().beginTransaction().add(R.id.left_drawer_container, leftDrawerFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (contentFragment == null) {
            WelcomeFragment welcomeFragment = new WelcomeFragment();
            switchToContentFragment(welcomeFragment);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }, 1000);
        }
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //lock the right drawer initially
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

                if (isRightDrawerView(drawerView)) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.left_drawer_container));
                }

                contentFragment.setHasOptionsMenu(false);
                leftDrawerFragment.setHasOptionsMenu(true);

                invalidateOptionsMenu();
            }

            @Override public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (isRightDrawerView(drawerView)) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.findViewById(R.id.left_drawer_container));
                }

                contentFragment.setHasOptionsMenu(true);
                leftDrawerFragment.setHasOptionsMenu(false);

                invalidateOptionsMenu();
            }

            @Override public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
    }

    private boolean isRightDrawerView(View drawerView) {
        return drawerView.getId() == R.id.right_drawer_container;
    }

    private boolean isLeftDrawerView(View drawerView) {
        return drawerView.getId() == R.id.left_drawer_container;
    }

    private void switchToContentFragment(final Fragment fragment) {
        final float distanceToEdgeOfScreen = fragmentContainer.getScrollX() - getWindow().getDecorView().getWidth();

        if (contentFragment != fragment && Float.compare(distanceToEdgeOfScreen, 0) != 0) {
            final float end = getWindow().getDecorView().getWidth();
            final float start = fragmentContainer.getTranslationX();

            ObjectAnimator animator = ObjectAnimator.ofFloat(fragmentContainer, "translationX", start, end);
            animator.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {

                }

                @Override public void onAnimationEnd(Animator animation) {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment, null);
                    transaction.commit();
                    manager.executePendingTransactions();

                    drawerLayout.closeDrawers();
                }

                @Override public void onAnimationCancel(Animator animation) {

                }

                @Override public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.start();
        }
        else if (contentFragment != fragment) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, null);
            transaction.commit();
            manager.executePendingTransactions();
        }
        else {
            drawerLayout.closeDrawers();
        }

        contentFragment = fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
}
