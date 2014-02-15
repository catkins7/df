package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Hero;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.player.Player;
import com.squareup.picasso.Picasso;

/**
 * Created by scottrick on 2/15/14.
 */
public class MatchSimpleSummaryView extends LinearLayout {

    private Match match;

    public MatchSimpleSummaryView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_match_simple_summary, this, true);
    }

    public void setMatch(Match match) {
        this.match = match;

        updateViews();
    }

    private void updateViews() {
        if (match != null) {
            ImageView[] imageViews = new ImageView[10];

            imageViews[0] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_0);
            imageViews[1] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_1);
            imageViews[2] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_2);
            imageViews[3] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_3);
            imageViews[4] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_4);
            imageViews[5] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_5);
            imageViews[6] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_6);
            imageViews[7] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_7);
            imageViews[8] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_8);
            imageViews[9] = (ImageView) findViewById(R.id.view_match_simple_summary_hero_9);

            for (ImageView imageView : imageViews) {
                imageView.setImageDrawable(null);
            }

            for (Player player : match.getPlayers()) {
                Hero hero = Heroes.get().getHero(player.getHeroIdString());
                int index = match.getPlayers().indexOf(player);

                if (hero != null) {
                    Picasso.with(DotaFriendApplication.CONTEXT).load(hero.getSmallHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(imageViews[index]);
                }
            }
        }
    }
}
