package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Hero;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.squareup.picasso.Picasso;

/**
 * Created by scottrick on 2/16/14.
 */
public class MatchViewForPlayerBasic extends RelativeLayout {

    private Match match;
    private SteamUser user;

    public MatchViewForPlayerBasic(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_match_for_player_basic, this, true);
    }

    public void setMatchAndUser(Match match, SteamUser user) {
        this.match = match;
        this.user = user;

        updateViews();
    }

    private void updateViews() {
        ImageView imageView = (ImageView) findViewById(R.id.view_match_player_basic_image_view);
        TextView heroNameTextView = (TextView) findViewById(R.id.view_match_player_basic_hero_name_text_view);
        TextView matchIdTextView = (TextView) findViewById(R.id.view_match_player_basic_match_id_text_view);

        matchIdTextView.setText(match.getMatchId());

        Player player = match.getPlayerForSteamUser(user);
        Hero hero = Heroes.get().getHero(player.getHeroIdString());

        if (hero != null) {
            Picasso.with(DotaFriendApplication.CONTEXT).load(hero.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(imageView);
            heroNameTextView.setText(hero.getLocalizedName());
        }
        else {
            imageView.setImageResource(R.drawable.ic_launcher);
            heroNameTextView.setText(R.string.no_hero);
        }
    }
}
