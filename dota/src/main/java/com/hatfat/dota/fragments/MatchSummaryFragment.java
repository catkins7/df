package com.hatfat.dota.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.PlayerActivity;
import com.hatfat.dota.model.game.Item;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.view.PlayerRowView;
import com.squareup.picasso.Picasso;

public class MatchSummaryFragment extends CharltonFragment {

    private static final String MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY = "MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY";

    private BroadcastReceiver receiver;

    private Match match;

    private View topContainerView;
    private TextView victoryTextView;
    private TextView radiantKillsTextView;
    private TextView direKillsTextView;

    private ListView listView;
    private BaseAdapter matchAdapter;

    private enum MatchSummaryRowTypes {
        ROW_TYPE_GAME_MODE(0),
        ROW_TYPE_DURATION(1),
        ROW_TYPE_TIME_AGO(2),
        ROW_TYPE_ITEM_OF_THE_GAME(3),
        ROW_TYPE_PLAYER_OF_THE_GAME(4),

        ROW_TYPE_COUNT(5);

        private int value;

        MatchSummaryRowTypes(int value) {
            this.value = value;
        }

        public int getIntValue() {
            return value;
        }
    }

    public static Bundle newBundleForMatch(Long matchId) {
        Bundle args = new Bundle();
        args.putLong(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY, matchId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long matchId = getArguments().getLong(MATCH_SUMMARY_FRAGMENT_MATCH_ID_KEY);

        if (matchId == null) {
            throw new RuntimeException("must be created with a match id");
        }

        match = Matches.get().getMatch(matchId);

        signalCharltonActivityToUpdateTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_summary, container, false);

        topContainerView = view.findViewById(R.id.fragment_match_summary_top_layout);
        victoryTextView = (TextView) view.findViewById(R.id.fragment_match_summary_victory_text_view);
        radiantKillsTextView = (TextView) view.findViewById(R.id.fragment_match_summary_radiant_kills_text_view);
        direKillsTextView = (TextView) view.findViewById(R.id.fragment_match_summary_dire_kills_text_view);
        listView = (ListView) view.findViewById(R.id.fragment_match_summary_players_list_view);

        if (match.hasMatchDetails()) {
            setupListView();
            updateViews();
        }

        return view;
    }

    private void setupListView() {
        matchAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return match.getPlayers().size() + MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue();
            }

            @Override
            public Player getItem(int i) {
                if (i < MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue()) {
                    return null;
                }

                return match.getPlayers().get(i - MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue());
            }

            @Override
            public int getViewTypeCount() {
                return 5;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue()) {
                    if (position == MatchSummaryRowTypes.ROW_TYPE_ITEM_OF_THE_GAME.getIntValue()) {
                        return 0; //image row
                    }

                    return 1; //extra info row
                }

                Player player = getItem(position);

                //a Player row, but need to know which layout it will be
                if (match.isAbilityDraft()) {
                    return 2;
                }
                else if (player.hasAdditionalUnitsWeWantToShow()) {
                    return 3;
                }
                else {
                    return 4;
                }
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean isEnabled(int position) {
                return position >= MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue();
            }

            @Override
            public View getView(int i, View convertView, ViewGroup viewGroup) {
                if (i < MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue() && i != MatchSummaryRowTypes.ROW_TYPE_ITEM_OF_THE_GAME.getIntValue()) {
                    //extra text row
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        convertView = inflater.inflate(R.layout.view_stats_text_row, viewGroup, false);
                        convertView.setBackgroundResource(R.drawable.unselectable_background);
                    }

                    TextView titleText = (TextView) convertView.findViewById(R.id.view_stats_text_row_title_text_view);
                    TextView subtitleText = (TextView) convertView.findViewById(R.id.view_stats_text_row_subtitle_text_view);

                    subtitleText.setTextColor(getResources().getColor(R.color.off_white));
                    subtitleText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    subtitleText.setCompoundDrawablePadding(0);

                    if (i == MatchSummaryRowTypes.ROW_TYPE_GAME_MODE.getIntValue()) {
                        //game mode row
                        titleText.setText(R.string.match_summary_mode_label);
                        subtitleText.setText(match.getMatchTypeString(getResources()));

                        if (match.isRankedMatchmaking()) {
                            subtitleText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                    getResources().getDrawable(R.drawable.ranked_icon), null);
                            subtitleText.setCompoundDrawablePadding(
                                    (int) getResources().getDimension(R.dimen.default_padding));
                        }
                    }
                    else if (i == MatchSummaryRowTypes.ROW_TYPE_DURATION.getIntValue()) {
                        //duration row
                        titleText.setText(R.string.match_summary_duration_label);
                        subtitleText.setText(match.getDurationString());
                    }
                    else if (i == MatchSummaryRowTypes.ROW_TYPE_TIME_AGO.getIntValue()) {
                        //time ago row
                        titleText.setText(R.string.match_summary_time_ago_label);
                        subtitleText.setText(match.getTimeAgoString(getResources()));
                    }
                    else if (i == MatchSummaryRowTypes.ROW_TYPE_PLAYER_OF_THE_GAME.getIntValue()) {
                        titleText.setText(R.string.match_summary_player_of_the_game_label);

                        Player potm = match.getPlayerOfTheMatch();
                        if (potm != null) {
                            subtitleText.setText(potm.getSteamUser().getDisplayName());
                        }
                        else {
                            subtitleText.setText(R.string.match_summary_no_player_of_the_game);
                        }
                    }
                    else {
                        TextView textView = new TextView(viewGroup.getContext());
                        textView.setText("Not Implemented");
                        textView.setPadding(8, 8, 8, 8);
                        return textView;
                    }

                    return convertView;
                }
                else if (i < MatchSummaryRowTypes.ROW_TYPE_COUNT.getIntValue() && i == MatchSummaryRowTypes.ROW_TYPE_ITEM_OF_THE_GAME.getIntValue()) {
                    //extra image row
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.view_stats_image_row, viewGroup, false);
                        convertView.setBackgroundResource(R.drawable.unselectable_background);
                    }

                    TextView titleText = (TextView) convertView.findViewById(R.id.view_stats_image_row_title_text_view);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.view_stats_image_row_image_view);

                    titleText.setText(R.string.match_summary_item_of_the_game_label);

                    Item iotm = match.getItemOfTheMatch();

                    if (iotm != null) {
                            Picasso.with(DotaFriendApplication.CONTEXT).load(iotm.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(
                                    imageView);
                    }
                    else {
                            imageView.setImageResource(R.drawable.empty_item_bg);
                    }

                    return convertView;
                }

                Player player = getItem(i);
                PlayerRowView playerView = (PlayerRowView) convertView;
                boolean isPlayerOfTheMatch = match.getPlayerOfTheMatch() == player;

                if (playerView == null) {
                    playerView = new PlayerRowView(viewGroup.getContext(), player, isPlayerOfTheMatch, match.isAbilityDraft());
                }
                else {
                    playerView.setPlayer(player, isPlayerOfTheMatch);
                }

                return playerView;
            }
        };

        listView.setAdapter(matchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Player player = (Player) matchAdapter.getItem(i);
                SteamUser user = player.getSteamUser();

                Intent intent = PlayerActivity.intentForPlayer(getActivity().getApplicationContext(), user.getSteamId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        match.getMatchDetailsIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();

        startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        stopListening();
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Match.MATCH_UPDATED)) {
                    //reload the match row for this match
                    Long updatedMatchId = intent.getLongExtra(Match.MATCH_UPDATED_ID_KEY, 0);

                    if (updatedMatchId.equals(match.getMatchIdLong())) {
                        if (matchAdapter == null) {
                            //was created without match details, need to setup the listview now
                            setupListView();
                        }

                        updateViews();
                    }
                }
                else if (intent.getAction().equals(SteamUser.STEAM_USER_UPDATED)) {
                    String updatedId = intent.getStringExtra(SteamUser.STEAM_USER_UPDATED_ID_KEY);

                    if (listView != null && matchAdapter != null) {
                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View view = listView.getChildAt(i);

                            if (view instanceof PlayerRowView) {
                                PlayerRowView playerRowView = (PlayerRowView) view;

                                if (playerRowView.getPlayer().getSteamUser().getSteamId().equals(updatedId)) {
                                    playerRowView.notifyPlayerUpdated();
                                    updateViews();
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter summaryFilter = new IntentFilter();
        summaryFilter.addAction(Match.MATCH_UPDATED);
        summaryFilter.addAction(SteamUser.STEAM_USER_UPDATED);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, summaryFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }

    private void updateViews() {
        victoryTextView.setText(match.getMatchResult().getDescriptionStringResourceId());
        topContainerView.setBackgroundResource(match.getMatchResult().getBackgroundResourceId());
        radiantKillsTextView.setText(String.valueOf(match.getDireTotalDeathCount()));
        direKillsTextView.setText(String.valueOf(match.getRadiantTotalDeathCount()));

        matchAdapter.notifyDataSetChanged();
    }

    @Override
    public String getCharltonMessageText(Context context) {
        if (match != null) {
            return String.format(context.getResources().getString(R.string.match_summary_charlton_text), match.getMatchId());
        }

        return null;
    }
}
