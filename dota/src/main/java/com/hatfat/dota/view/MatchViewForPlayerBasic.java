package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Hero;
import com.hatfat.dota.model.game.Heroes;
import com.hatfat.dota.model.game.Item;
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

    public Match getMatch() {
        return match;
    }

    public SteamUser getUser() {
        return user;
    }

    public void notifyMatchUpdated() {
        updateViews();
    }

    private void updateViews() {
        ImageView heroImageView = (ImageView) findViewById(R.id.view_match_player_basic_hero_image_view);
        ImageView rankedImageView = (ImageView) findViewById(R.id.view_match_player_basic_ranked_image_view);
        ImageView itemImageView = (ImageView) findViewById(R.id.view_match_player_basic_item_image_view);
        TextView heroNameTextView = (TextView) findViewById(R.id.view_match_player_basic_hero_name_text_view);
        TextView matchIdTextView = (TextView) findViewById(R.id.view_match_player_basic_match_id_text_view);
        TextView timeAgoTextView = (TextView) findViewById(R.id.view_match_player_basic_date_text_view);
        TextView victoryTextView = (TextView) findViewById(R.id.view_match_player_basic_victory_text_view);

        timeAgoTextView.setText(match.getTimeAgoString());

        if (match.isRankedMatchmaking()) {
            rankedImageView.setVisibility(View.VISIBLE);
        }
        else {
            rankedImageView.setVisibility(View.INVISIBLE);
        }

        Player player = match.getPlayerForSteamUser(user);

        matchIdTextView.setText(match.getGameModeString());
//        matchIdTextView.setText(player.getKdaString());

        Item iotm = player.getItemOfTheMatch();
        if (iotm != null) {
            Picasso.with(DotaFriendApplication.CONTEXT).load(iotm.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.empty_item_bg).into(itemImageView);
        }
        else {
            itemImageView.setImageResource(R.drawable.empty_item_bg);
        }

        Hero hero = player == null ? null : Heroes.get().getHero(player.getHeroIdString());

        if (player != null) {
            victoryTextView.setText(match.getMatchResultStringResourceIdForPlayer(player));
            victoryTextView.setBackgroundResource(match.getMatchResultBackgroundResourceIdForPlayer(player));
        }
        else {
            victoryTextView.setText(null);
            victoryTextView.setBackgroundColor(0);
        }

        if (hero != null) {
            Picasso.with(DotaFriendApplication.CONTEXT).load(hero.getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(heroImageView);
            heroNameTextView.setText(hero.getLocalizedName());
        }
        else {
            heroImageView.setImageResource(R.drawable.ic_launcher);
            heroNameTextView.setText(R.string.no_hero);
        }
    }
}
