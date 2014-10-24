package com.hatfat.dota.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.friend.Friends;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.tabs.CharltonTab;
import com.hatfat.dota.util.CharltonBubbleDrawable;

import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class CharltonActivity extends Activity {

    private static Random sharedRandom = null;

    private BroadcastReceiver receiver;

    private boolean charltonDialogVisible = false;

    private TextView charltonTitleTextView;

    private List<CharltonTab> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isLoadingActivity() && !isDataLoaded()) {
            //no data is loaded, and we aren't the loading activity.  So start the loading activity
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(hasParentActivity());
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_charlton);

        charltonTitleTextView = (TextView) actionBar.getCustomView()
                .findViewById(R.id.actionbar_charlton_text_view);

        Drawable notPressedDrawable = new CharltonBubbleDrawable(R.color.off_white);
        Drawable pressedDrawable = new CharltonBubbleDrawable(R.color.steam_light_blue);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] { android.R.attr.state_pressed }, pressedDrawable);
        states.addState(new int[] { android.R.attr.state_focused }, pressedDrawable);
        states.addState(new int[] { }, notPressedDrawable);
        actionBar.getCustomView().setBackgroundDrawable(states);

        if (!isLoadingActivity()) {
            actionBar.getCustomView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!charltonDialogVisible) {
                        charltonDialogVisible = true;

                        TextDialogHelper.showHestonDialog(CharltonActivity.this,
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        charltonDialogVisible = false;
                                    }
                                });
                    }
                }
            });
        }

        tabs = createTabs();

        if (tabs.size() > 1) {
            //more than one tab, so setup the tabbed interface
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            for (CharltonTab charltonTab : tabs) {
                ActionBar.Tab tab = actionBar.newTab()
                        .setText(charltonTab.getCharltonTabText())
                        .setTabListener(charltonTab);

                actionBar.addTab(tab);
            }

            int startingTabIndex = getDefaultTabIndex();
            if (startingTabIndex > 0 && startingTabIndex < actionBar.getTabCount()) {
                actionBar.selectTab(actionBar.getTabAt(startingTabIndex));
            }
        }
        else if (tabs.size() == 1) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();

            tabs.get(0).attach(ft);

            ft.commit();
            manager.executePendingTransactions();
        }

        super.onCreate(savedInstanceState);

        //set the initial friend image
        updateFriendImage();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startListening();

        if (!isLoadingActivity() && !isDataLoaded()) {
            throw new RuntimeException("starting this activity with no data!");
        }
    }

    @Override protected void onStop() {
        super.onStop();

        stopListening();
    }

    private boolean isDataLoaded() {
        return SteamUsers.get().isLoaded() && Matches.get().isLoaded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Random getSharedRandom() {
        if (sharedRandom == null) {
            Date currentDate = new Date();
            sharedRandom = new Random(currentDate.getTime());
        }

        return sharedRandom;
    }

    public void updateWithCharltonTab(CharltonTab tab) {
        updateFriendTextForTab(tab);
    }

    private void updateFriendImage() {
        Drawable friendDrawable = getResources().getDrawable(Friends.get().getCurrentFriend().getImageResourceId(this));
        LayerDrawable layeredDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.heston_layered);
        layeredDrawable.setDrawableByLayerId(R.id.heston_layered_drawable_id, friendDrawable);

        getActionBar().setIcon(layeredDrawable);
    }

    private void updateFriendTextForTab(CharltonTab tab) {
        String newCharltonText = tab.getFragment().getCharltonMessageText(this);

        if (newCharltonText != null) {
            charltonTitleTextView.setText(newCharltonText);
        }
        else {
            charltonTitleTextView.setText(Friends.get().getCurrentFriend().getGreeting());
        }
    }

    public void signalUpdateActiveCharltonTab() {
        int selectedTabIndex = 0;

        if (tabs.size() > 1) {
            selectedTabIndex = getActionBar().getSelectedNavigationIndex();
        }

        updateFriendTextForTab(tabs.get(selectedTabIndex));
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Friends.CURRENT_FRIEND_CHANGED_NOTIFICATION:
                    case Friends.FRIENDS_LOADED_NOTIFICATION:
                        updateFriendImage();
                        signalUpdateActiveCharltonTab();
                        break;
                }
            }
        };

        IntentFilter friendFilter = new IntentFilter();
        friendFilter.addAction(Friends.CURRENT_FRIEND_CHANGED_NOTIFICATION);
        friendFilter.addAction(Friends.FRIENDS_LOADED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, friendFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    protected boolean hasParentActivity() {
        return true;
    }

    protected boolean isLoadingActivity() {
        return false;
    }

    protected int getDefaultTabIndex() {
        return 0; //the first tab should be selected by default
    }

    protected abstract List<CharltonTab> createTabs();
}
