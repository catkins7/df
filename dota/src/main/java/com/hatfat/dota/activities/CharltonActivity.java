package com.hatfat.dota.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.tabs.CharltonTab;
import com.hatfat.dota.util.CharltonBubbleDrawable;

import java.util.List;
import java.util.Random;

public abstract class CharltonActivity extends Activity {

    private static Random rand = new Random();
    private static int currentHestonDrawableId = 0;
    private static int currentHestonCountsLeft = 0;

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
        charltonTitleTextView.setText(R.string.default_charlton_text);

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isLoadingActivity() && !isDataLoaded()) {
            throw new RuntimeException("starting this activity with no data!");
        }
    }

    private boolean isDataLoaded() {
        return SteamUsers.get().isLoaded() && Matches.get().isLoaded() && Heroes.get().isLoaded();
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

    public void updateWithCharltonTab(CharltonTab tab) {
        String newCharltonText = tab.getFragment().getCharltonMessageText(getResources());

        if (newCharltonText != null) {
            charltonTitleTextView.setText(newCharltonText);
        }
        else {
            charltonTitleTextView.setText(R.string.default_charlton_text);
        }

        updateCharltonImage();
    }

    public static int getRandomHestonDrawableResource(Context context) {
        int randomHeston = Math.abs(rand.nextInt()) % 15; //15 total heston images currently
        String drawableName = "heston" + randomHeston;
        return context.getResources().getIdentifier(drawableName, "drawable",
                context.getPackageName());
    }

    private static LayerDrawable getCharltonDrawableForId(Context context, int id) {
        Drawable newHestonDrawable = context.getResources().getDrawable(id);
        LayerDrawable layeredDrawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.heston_layered);
        layeredDrawable.setDrawableByLayerId(R.id.heston_layered_drawable_id, newHestonDrawable);

        return layeredDrawable;
    }

    private void updateCharltonImage() {
        if (currentHestonCountsLeft <= 0) {
            //we need to set a new charlton image!
            currentHestonDrawableId = getRandomHestonDrawableResource(getApplicationContext());
            currentHestonCountsLeft = Math.abs(rand.nextInt()) % 10 + 20; //20 to 29 times before it changes
        }

        Drawable charltonDrawable = getCharltonDrawableForId(getApplicationContext(), currentHestonDrawableId);
        getActionBar().setIcon(charltonDrawable);

        currentHestonCountsLeft--;
    }

    public void signalUpdateActiveCharltonTab() {
        int selectedTabIndex = 0;

        if (tabs.size() > 1) {
            selectedTabIndex = getActionBar().getSelectedNavigationIndex();
        }

        updateWithCharltonTab(tabs.get(selectedTabIndex));
    }

    protected boolean hasParentActivity() {
        return true;
    }

    protected boolean isLoadingActivity() {
        return false;
    }

    protected abstract List<CharltonTab> createTabs();
}
