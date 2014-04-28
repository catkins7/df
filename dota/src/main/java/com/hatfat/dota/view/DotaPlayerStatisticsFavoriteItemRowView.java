package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.DotaStatistics;
import com.squareup.picasso.Picasso;

public class DotaPlayerStatisticsFavoriteItemRowView extends RelativeLayout {

    private DotaStatistics.ItemStats itemStats;

    private ImageView iconImageView;
    private TextView countTextView;
    private TextView winTextView;

    public DotaPlayerStatisticsFavoriteItemRowView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_stats_favorite_item_row, this, true);

        iconImageView = (ImageView) findViewById(R.id.view_stats_favorite_item_row_icon_image_view);
        countTextView = (TextView) findViewById(R.id.view_stats_favorite_item_row_count_text_view);
        winTextView = (TextView) findViewById(R.id.view_stats_favorite_item_row_win_text_view);
    }

    public void setItemStats(DotaStatistics.ItemStats newStats) {
        itemStats = newStats;

        updateViews();
    }

    private void updateViews() {
        Picasso.with(DotaFriendApplication.CONTEXT).load(itemStats.item.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(iconImageView);
        countTextView.setText("x" + itemStats.purchaseCount);
        winTextView.setText(itemStats.getWinString());
    }
}
