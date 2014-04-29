package com.hatfat.dota.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.dialogs.FetchMatchesDialogHelper;
import com.hatfat.dota.dialogs.InfoDialogHelper;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.services.MatchFetcher;
import com.hatfat.dota.view.MatchViewForPlayerBasic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scottrick on 2/12/14.
 */
public class DotaPlayerSummaryFragment extends CharltonFragment {

    private static final String DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY = "DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY";

    private SteamUser user;
    private boolean userMatchesHaveChanged;

    private BroadcastReceiver receiver;

    private TextView personaTextView;
    private TextView publicMatchesTextView;
    private TextView rankedMatchesTextView;
    private TextView thirdRowTextView;
    private ImageView profileImageView;
    private ListView matchesListView;
    private Button friendToggleButton;
    private Button fetchAllMatchesButton;
    private Button statsButton;

    private BaseAdapter matchesAdapter;
    private ArrayList<String> sortedMatches;

    private boolean fetchingMatches;

    public static DotaPlayerSummaryFragment newInstance(SteamUser user) {
        DotaPlayerSummaryFragment newFragment = new DotaPlayerSummaryFragment();

        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY, user.getSteamId());
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        String steamUserId = getArguments().getString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY);
        if (steamUserId != null) {
            user = SteamUsers.get().getBySteamId(steamUserId);
        }

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dota_player_summary, container, false);

        personaTextView = (TextView) view.findViewById(R.id.fragment_dota_player_summary_persona_text_view);
        publicMatchesTextView = (TextView) view.findViewById(R.id.fragment_dota_player_public_matches_text_view);
        rankedMatchesTextView = (TextView) view.findViewById(R.id.fragment_dota_player_ranked_matches_text_view);
        thirdRowTextView = (TextView) view.findViewById(R.id.fragment_dota_player_summary_third_row_text_view);
        profileImageView = (ImageView) view.findViewById(R.id.fragment_dota_player_summary_user_image_view);
        matchesListView = (ListView) view.findViewById(R.id.fragment_dota_player_summary_matches_list_view);
        friendToggleButton = (Button) view.findViewById(R.id.fragment_dota_player_summary_friend_button);
        fetchAllMatchesButton = (Button) view.findViewById(R.id.fragment_dota_player_summary_fetch_all_button);
        statsButton = (Button) view.findViewById(R.id.fragment_dota_player_summary_right_tray_button);

        friendToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStar();
            }
        });
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCharltonActivity().openRightDrawer();
            }
        });
        fetchAllMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmFetchAll();
            }
        });

        updateMatchList();
        setupMatchesList();
        updateViews();
        updateMatchInfoViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SteamUsers.get().refreshUser(user);

        if (user.isRealUser()) {
            fetchingMatches = true;
            MatchFetcher.fetchMatches(user, new Callback<List<Match>>() {
                @Override
                public void success(List<Match> matches, Response response) {
                    fetchingMatches = false;

                    if (matches == null || matches.size() <= 0) {
                        //no matches, but we want to remove the progress bar and show the "no matches row" so reload
                        matchesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    fetchingMatches = false;
                    matchesAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getCharltonActivity().putFragmentInRightDrawer(
                DotaPlayerStatisticsFragment.newInstance(user));
    }

    @Override
    public void onStop() {
        super.onStop();

        saveIfNecessary();

        getCharltonActivity().removeDrawerFragment();
    }

    private void saveIfNecessary() {
        if (SteamUsers.get().isUserStarred(user)) {
            if (userMatchesHaveChanged) {
                userMatchesHaveChanged = false;
                Matches.get().saveMatchesToDiskForUser(user);
            }
        }
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        updateViews();
                    }
                }
                else if (intent.getAction().equals(SteamUser.STEAM_USER_MATCHES_CHANGED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);
                    if (updatedId.equals(user.getSteamId())) {
                        updateMatchInfoViews();
                        updateMatchList();
                        userMatchesHaveChanged = true;
                    }
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        //a match in this users' match list was updated, so we need to save on exit
                        userMatchesHaveChanged = true;
                        updateMatchInfoViews();
                    }

                    if (matchesListView != null && matchesAdapter != null) {
                        for (int i = 0; i < matchesListView.getChildCount(); i++) {
                            View view = matchesListView.getChildAt(i);

                            if (view instanceof MatchViewForPlayerBasic) {
                                MatchViewForPlayerBasic matchView = (MatchViewForPlayerBasic) view;

                                if (matchView.getMatch().getMatchId().equals(updatedMatchId)) {
                                    matchView.notifyMatchUpdated();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(SteamUser.STEAM_USER_UPDATED);
        summaryFilter.addAction(SteamUser.STEAM_USER_MATCHES_CHANGED);
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    private void setupMatchesList() {
        matchesAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (sortedMatches.size() <= 0) {
                    return 1;
                }
                else {
                    return sortedMatches.size();
                }
            }

            @Override
            public Object getItem(int i) {
                return Matches.get().getMatch(sortedMatches.get(i));
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public int getItemViewType(int position) {
                if (sortedMatches.size() <= 0) {
                    return 0;
                }
                else {
                    return 1;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (sortedMatches.size() <= 0) {
                    //no users

                    if (fetchingMatches) {
                        ProgressBar progressBar = new ProgressBar(viewGroup.getContext());
                        progressBar.setBackgroundResource(R.drawable.off_black_background);
                        progressBar.setIndeterminate(true);

                        return progressBar;
                    }
                    else {
                        TextView textView = new TextView(viewGroup.getContext());

                        textView.setBackgroundResource(R.drawable.off_black_background);
                        textView.setText(R.string.no_matches);
                        textView.setTextColor(getResources().getColor(R.color.off_white));
                        textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size_tiny));

                        int padding = (int) getResources().getDimension(R.dimen.default_padding);
                        textView.setPadding(padding, padding, padding, padding);

                        return textView;
                    }
                }

                Match match = (Match) getItem(i);
                match.getMatchDetailsIfNeeded();
                MatchViewForPlayerBasic matchView = (MatchViewForPlayerBasic) view;

                if (matchView == null) {
                    matchView = new MatchViewForPlayerBasic(viewGroup.getContext());
                }

                matchView.setMatchAndUser(match, user);

                return matchView;
            }
        };

        matchesListView.setAdapter(matchesAdapter);

        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (sortedMatches.size() > i) {
                    Match match = (Match) matchesAdapter.getItem(i);
                    getCharltonActivity()
                            .pushCharltonFragment(MatchSummaryFragment.newInstance(match));
                }
            }
        });
    }

    private void updateMatchList() {
        sortedMatches = new ArrayList<>();
        sortedMatches.addAll(user.getMatches());
        Collections.sort(sortedMatches, Match.getMatchIdComparator());

        if (matchesListView != null && matchesAdapter != null) {
            matchesAdapter.notifyDataSetChanged();
        }
    }

    private void toggleStar() {
        if (SteamUsers.get().isUserStarred(user)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.player_summary_remove_friend_title_text);
            builder.setMessage(R.string.player_summary_remove_friend_message_text);
            builder.setPositiveButton(R.string.player_summary_remove_friend_remove_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SteamUsers.get().removeSteamUserFromStarredList(user);
                    updateFriendButtonBackground();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            SteamUsers.get().addSteamUserToStarredList(user);
            updateFriendButtonBackground();

            Toast.makeText(getActivity().getApplicationContext(), R.string.player_summary_added_friend_text, Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmFetchAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.player_summary_fetch_all_dialog_title);
        builder.setMessage(R.string.player_summary_fetch_all_dialog_message);
        builder.setPositiveButton(R.string.player_summary_fetch_all_dialog_confirm_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //star the user automatically when you fetch all
                if (!SteamUsers.get().isUserStarred(user)) {
                    SteamUsers.get().addSteamUserToStarredList(user);
                    updateFriendButtonBackground();
                }

                FetchMatchesDialogHelper dialogHelper = new FetchMatchesDialogHelper(user);
                dialogHelper.showFromActivity(getActivity());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateViews() {
        if (profileImageView == null) {
            return;
        }

        Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(profileImageView);

        personaTextView.setText(user.getDisplayName());

        if (user.isRealUser()) {
            fetchAllMatchesButton.setVisibility(View.VISIBLE);
            statsButton.setVisibility(View.VISIBLE);
        }
        else {
            fetchAllMatchesButton.setVisibility(View.GONE);
            statsButton.setVisibility(View.GONE);
        }

        updateFriendButtonBackground();
    }

    private void updateMatchInfoViews() {
        if (publicMatchesTextView == null) {
            return;
        }

        String[] summaryStrings = user.getMatchSummaryStrings(getResources());
        publicMatchesTextView.setText(summaryStrings[0]);
        rankedMatchesTextView.setText(summaryStrings[1]);
        thirdRowTextView.setText(summaryStrings[2]);
    }

    private void updateFriendButtonBackground() {
        if (user.isRealUser()) {
            friendToggleButton.setVisibility(View.VISIBLE);
        }
        else {
            friendToggleButton.setVisibility(View.GONE);
        }

        if (SteamUsers.get().isUserStarred(user)) {
            friendToggleButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        else {
            friendToggleButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.player_summary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_player_summary_stats_info:
                showStatsInfoDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showStatsInfoDialog() {
        InfoDialogHelper.showFromActivity(getActivity());
    }

    @Override
    public String getCharltonText() {
        return "Here is " + user.getDisplayName() +"'s [" + user.getAccountId() + "] summary information.";
    }
}
