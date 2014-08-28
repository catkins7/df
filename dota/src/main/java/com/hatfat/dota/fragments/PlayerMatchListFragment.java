package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.MatchActivity;
import com.hatfat.dota.activities.PlayerMatchListActivity;
import com.hatfat.dota.adapters.MatchListAdapter;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.view.GraphView;
import com.hatfat.dota.view.MatchViewForPlayerBasic;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class PlayerMatchListFragment extends CharltonFragment {

    private static String PLAYER_MATCH_LIST_USER_ID_KEY = "PLAYER_MATCH_LIST_USER_ID_KEY";
    private static String PLAYER_MATCH_LIST_LABEL_KEY   = "PLAYER_MATCH_LIST_LABEL_KEY";
    private static String PLAYER_MATCH_LIST_SECONDARY_IMAGE_KEY
                                                          = "PLAYER_MATCH_LIST_SECONDARY_IMAGE_KEY";
    private static String PLAYER_MATCH_LIST_MATCHES_KEY   = "PLAYER_MATCH_LIST_MATCHES_KEY";
    private static String PLAYER_MATCH_LIST_TEXT_MODE_KEY = "PLAYER_MATCH_LIST_TEXT_MODE_KEY";

    private BroadcastReceiver receiver;

    private SteamUser                                 user;
    private List<String>                              matchIds;
    private String                                    matchesLabel;
    private String                                    secondaryImageUrl;
    private PlayerMatchListActivity.MatchListTextMode textMode;

    private ListView         matchListView;
    private MatchListAdapter matchAdapter;

    private TextView  userNameTextView;
    private TextView  labelTextView;
    private TextView  winPercentTextView;
    private TextView  gameCountTextView;
    private ImageView leftImageView;
    private ImageView rightImageView;
    private GraphView graphView;

    private Target rightTarget;

    public static Bundle newBundleForUserAndMatches(String userId, String label,
            String secondaryImageUrl, ArrayList<String> matchIds,
            PlayerMatchListActivity.MatchListTextMode textMode) {
        Bundle args = new Bundle();
        args.putString(PLAYER_MATCH_LIST_USER_ID_KEY, userId);
        args.putString(PLAYER_MATCH_LIST_LABEL_KEY, label);
        args.putString(PLAYER_MATCH_LIST_SECONDARY_IMAGE_KEY, secondaryImageUrl);
        args.putStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY, matchIds);
        args.putInt(PLAYER_MATCH_LIST_TEXT_MODE_KEY, textMode.mode);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = getArguments().getString(PLAYER_MATCH_LIST_USER_ID_KEY);

        user = SteamUsers.get().getBySteamId(userId);
        matchIds = getArguments().getStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY);
        matchesLabel = getArguments().getString(PLAYER_MATCH_LIST_LABEL_KEY);
        secondaryImageUrl = getArguments().getString(PLAYER_MATCH_LIST_SECONDARY_IMAGE_KEY);
        textMode = PlayerMatchListActivity.MatchListTextMode
                .fromInt(getArguments().getInt(PLAYER_MATCH_LIST_TEXT_MODE_KEY));

        signalCharltonActivityToUpdateTab();

        startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopListening();
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
                else if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    String updatedMatchId = intent.getStringExtra(Match.MATCH_UPDATED_ID_KEY);
                    if (user.getMatches().contains(updatedMatchId)) {
                        updateViews();
                    }

                    if (matchAdapter != null && matchAdapter != null) {
                        for (int i = 0; i < matchListView.getChildCount(); i++) {
                            View view = matchListView.getChildAt(i);

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
        summaryFilter.addAction(Match.MATCH_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver,
                summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(
                receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_match_list, container, false);

        userNameTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_name_text_view);
        labelTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_label_text_view);
        winPercentTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_matches_text_view);
        gameCountTextView = (TextView) view.findViewById(R.id.fragment_player_match_list_third_row_text_view);
        leftImageView = (ImageView) view.findViewById(R.id.fragment_player_match_list_left_image_view);
        rightImageView = (ImageView) view.findViewById(R.id.fragment_player_match_list_right_image_view);
        graphView = (GraphView) view.findViewById(R.id.fragment_player_match_list_graph_view);

        matchAdapter = new MatchListAdapter(user);
        matchAdapter.setMatches(matchIds);

        matchListView = (ListView) view.findViewById(R.id.fragment_player_match_list_matches_list_view);

        matchListView.setAdapter(matchAdapter);
        matchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Match match = matchAdapter.getItem(i);
                Intent intent = MatchActivity
                        .intentForMatch(getActivity().getApplicationContext(), match.getMatchId());
                startActivity(intent);
            }
        });

        rightTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int boundBoxInDp = getResources().getDimensionPixelSize(R.dimen.dota_player_summary_fragment_image_view_size);

                //always scale on the X size
                float xScale = ((float) boundBoxInDp) / width;

                // Create a matrix for the scaling and add the scaling data
                Matrix matrix = new Matrix();
                matrix.postScale(xScale, xScale);

                // Create a new bitmap and convert it to a format understood by the ImageView
                Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                BitmapDrawable result = new BitmapDrawable(scaledBitmap);
                width = scaledBitmap.getWidth();
                height = scaledBitmap.getHeight();

                // Apply the scaled bitmap
                rightImageView.setImageDrawable(result);

                // Now change ImageView's dimensions to match the scaled image
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rightImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                rightImageView.setLayoutParams(params);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        updateViews();

        return view;
    }

    private void updateViews() {
        if (leftImageView == null) {
            return;
        }

        Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(leftImageView);

        if (secondaryImageUrl != null) {
            rightImageView.setVisibility(View.VISIBLE);
            Picasso.with(DotaFriendApplication.CONTEXT).load(secondaryImageUrl)
                    .placeholder(R.drawable.ic_launcher).into(rightTarget);
        }
        else {
            rightImageView.setVisibility(View.INVISIBLE);
        }

        userNameTextView.setText(user.getDisplayName());
        labelTextView.setText(matchesLabel);
        winPercentTextView.setText(String.format(getResources().getString(R.string.player_match_list_win_rate_text), getWinPercentage()));

        if (matchIds.size() > 1) {
            gameCountTextView.setText(String.format(
                    getResources().getString(R.string.player_match_list_match_count_text),
                    matchIds.size()));
        }
        else {
            gameCountTextView.setText(String.format(
                    getResources().getString(R.string.player_match_list_match_count_text_singular),
                    matchIds.size()));
        }

        //set values for the trend graph
        int numToGraph = Math.min(50, matchIds.size());
        List<String>  graphMatches = matchIds.subList(matchIds.size() - numToGraph, matchIds.size());
        graphView.setValuesFromMatchListForUser(graphMatches, user);
    }

    private float getWinPercentage() {
        int winCount = 0;
        int matchCount = 0;

        for (String matchId : matchIds) {
            Match match = Matches.get().getMatch(matchId);

            if (match.hasMatchDetails()) {
                matchCount++;
            }
            else {
                continue;
            }

            Match.PlayerMatchResult result = match.getPlayerMatchResultForPlayer(match.getPlayerForSteamUser(user));

            if (result.equals(Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY)) {
                winCount++;
            }
        }

        if (matchCount != matchIds.size()) {
            //shouldn't happen, not all data is available (probably because we went out of memory)
            return -1.0f;
        }

        return (float)winCount / (float)matchCount * 100.0f;
    }

    @Override
    public String getCharltonMessageText(Context context) {
        if (user != null) {
            switch (textMode) {
                case ALTERNATE_MODE:
                    return String.format(context.getResources().getString(R.string.player_match_list_charlton_text_alternate),
                            user.getDisplayName(), matchesLabel);
                case MATCH_UP_MODE:
                    return String.format(context.getResources().getString(R.string.player_match_list_charlton_text_match_up),
                            user.getDisplayName(), matchesLabel);
                case NORMAL_MODE:
                default:
                    return String.format(context.getResources().getString(R.string.player_match_list_charlton_text),
                            user.getDisplayName(), matchesLabel);
            }
        }
        else {
            return null;
        }
    }
}
