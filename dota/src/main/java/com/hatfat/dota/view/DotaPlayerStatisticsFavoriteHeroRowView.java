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

public class DotaPlayerStatisticsFavoriteHeroRowView extends RelativeLayout {

    private DotaStatistics.HeroStats heroStats;

    private ImageView iconImageView;
    private ImageView[] itemImageView = new ImageView[3];
    private TextView descriptionTextView;

    public DotaPlayerStatisticsFavoriteHeroRowView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_stats_favorite_hero_row, this, true);

        iconImageView = (ImageView) findViewById(R.id.view_stats_favorite_hero_row_icon_image_view);
        itemImageView[0] = (ImageView) findViewById(R.id.view_stats_favorite_hero_item_0);
        itemImageView[1] = (ImageView) findViewById(R.id.view_stats_favorite_hero_item_1);
        itemImageView[2] = (ImageView) findViewById(R.id.view_stats_favorite_hero_item_2);
        descriptionTextView = (TextView) findViewById(R.id.view_stats_favorite_hero_row_text_view);
    }

    public void setHeroStats(DotaStatistics.HeroStats newStats) {
        heroStats = newStats;

        updateViews();
    }

    private void updateViews() {
        Picasso.with(DotaFriendApplication.CONTEXT).load(heroStats.hero.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(iconImageView);
        descriptionTextView.setText("x" + heroStats.heroCount);

        for (DotaStatistics.ItemStats itemStats : heroStats.favoriteHeroItems) {
            int index = heroStats.favoriteHeroItems.indexOf(itemStats);

            Picasso.with(DotaFriendApplication.CONTEXT)
                    .load(itemStats.item.getLargeHorizontalPortraitUrl())
                    .placeholder(R.drawable.empty_item_bg).into(itemImageView[index]);
        }
    }
}
