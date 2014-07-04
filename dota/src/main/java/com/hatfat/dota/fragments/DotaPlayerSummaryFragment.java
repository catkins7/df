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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.MatchActivity;
import com.hatfat.dota.adapters.MatchListAdapter;
import com.hatfat.dota.dialogs.FetchMatchesDialogHelper;
import com.hatfat.dota.dialogs.TextDialogHelper;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.services.MatchFetcher;
import com.hatfat.dota.view.MatchViewForPlayerBasic;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    private MatchListAdapter matchAdapter;

    public static Bundle newBundleForUser(String steamUserId) {
        Bundle args = new Bundle();
        args.putString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY, steamUserId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        String steamUserId = getArguments().getString(DOTA_PLAYER_SUMMARY_FRAGMENT_STEAM_USER_ID_KEY);

        if (steamUserId == null) {
            throw new RuntimeException("must be created with a steam user id");
        }

        user = SteamUsers.get().getBySteamId(steamUserId);

        signalCharltonActivityToUpdateTab();

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

        friendToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SteamUsers.get().toggleStarForUser(user, getActivity());
            }
        });
        fetchAllMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmFetchAll();
            }
        });

        setupMatchesList();
        updateMatchList();
        updateViews();
        updateMatchInfoViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SteamUsers.get().refreshUser(user);

        if (user.isRealUser()) {
            matchAdapter.setFetchingMatches(true);
            MatchFetcher.fetchMatches(user, new Callback<List<Match>>() {
                @Override
                public void success(List<Match> matches, Response response) {
                    matchAdapter.setFetchingMatches(false);

                    if (matches.size() <= 0) {
                        reloadMatchListIfNeeded();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    matchAdapter.setFetchingMatches(false);
                    reloadMatchListIfNeeded();
                }
            });
        }
    }

    private void reloadMatchListIfNeeded() {
        if (user.getMatches().size() <= 0) {
            matchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        saveIfNecessary();
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
                        signalCharltonActivityToUpdateTab();
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
                else if (intent.getAction().equals(SteamUsers.STEAM_STARRED_USERS_USER_LIST_CHANGED)) {
                    updateFriendButtonBackground();
                }
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        //a match in this users' match list was updated, so we need to save on exit
                        userMatchesHaveChanged = true;
                        updateMatchInfoViews();
                    }

                    if (matchesListView != null && matchAdapter != null) {
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
        summaryFilter.addAction(SteamUsers.STEAM_STARRED_USERS_USER_LIST_CHANGED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    private void setupMatchesList() {
        matchAdapter = new MatchListAdapter(user);

        matchesListView.setAdapter(matchAdapter);
        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = matchAdapter.getItem(i);
                Intent intent = MatchActivity.intentForMatch(getActivity().getApplicationContext(), match.getMatchId());
                startActivity(intent);
            }
        });
    }

    private void updateMatchList() {
        if (matchesListView != null && matchAdapter != null) {
            matchAdapter.setMatches(new LinkedList(user.getMatches()));
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
            publicMatchesTextView.setVisibility(View.VISIBLE);
            rankedMatchesTextView.setVisibility(View.VISIBLE);
            thirdRowTextView.setVisibility(View.VISIBLE);
        }
        else {
            fetchAllMatchesButton.setVisibility(View.GONE);
            publicMatchesTextView.setVisibility(View.GONE);
            rankedMatchesTextView.setVisibility(View.GONE);
            thirdRowTextView.setVisibility(View.GONE);
        }

        updateFriendButtonBackground();
    }

    private void updateMatchInfoViews() {
        if (publicMatchesTextView == null || !isAdded()) {
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
        inflater.inflate(R.menu.player_statistics, menu);
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
        TextDialogHelper.showStatsDialog(getActivity());
    }

    @Override
    public String getCharltonMessageText(Context context) {
        if (user != null) {
            return String.format(context.getResources().getString(R.string.player_summary_charlton_text), user.getDisplayName(), user.getAccountId());
        }

        return null;
    }
}
