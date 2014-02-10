package com.hatfat.dota;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import com.hatfat.dota.fragments.DotaPlayerListFragment;
import com.hatfat.dota.model.SteamUsers;

public class MainActivity extends Activity {

    private FrameLayout fragmentContainer;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
        }

        getFragmentManager().beginTransaction().add(R.id.left_drawer_container, new DotaPlayerListFragment()).commit();

        SteamUsers.get().init(); //initialize the SteamUsers singleton

        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        setupDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //lock the right drawer initially
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout.findViewById(R.id.right_drawer_container));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_closed) {
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
            }

            @Override public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (isRightDrawerView(drawerView)) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.findViewById(R.id.left_drawer_container));
                }
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
}
