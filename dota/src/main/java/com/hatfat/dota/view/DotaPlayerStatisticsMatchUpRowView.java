package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.DotaStatistics;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 8/20/14.
 */
public class DotaPlayerStatisticsMatchUpRowView extends RelativeLayout {

    private DotaStatistics.MatchUpStats matchUpStats;

    private ImageView iconImageView;
    private TextView gameCountTextView;
    private TextView gameCountLabelTextView;
    private TextView winPercentTextView;

    public DotaPlayerStatisticsMatchUpRowView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_stats_matchup_row, this, true);

        iconImageView = (ImageView) findViewById(R.id.view_stats_matchup_row_icon_image_view);
        gameCountTextView = (TextView) findViewById(R.id.view_stats_matchup_row_game_count_text_view);
        gameCountLabelTextView = (TextView) findViewById(R.id.view_stats_matchup_row_game_count_label_text_view);
        winPercentTextView = (TextView) findViewById(R.id.view_stats_matchup_row_win_percent_text_view);
    }

    public void setMatchUpStats(DotaStatistics.MatchUpStats newStats) {
        matchUpStats = newStats;

        updateViews();
    }

    private void updateViews() {
        Picasso.with(DotaFriendApplication.CONTEXT).load(matchUpStats.hero.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(iconImageView);

        gameCountTextView.setText(matchUpStats.getMatchCountString(getResources()));
        winPercentTextView.setText(matchUpStats.getWinPercentString(getResources()));

        if (matchUpStats.isMatchCountGreaterThanOne()) {
            gameCountLabelTextView.setText(R.string.player_statistics_matchup_matches_label_text_plural);
        }
        else {
            gameCountLabelTextView.setText(R.string.player_statistics_matchup_matches_label_text_single);
        }
    }
}
