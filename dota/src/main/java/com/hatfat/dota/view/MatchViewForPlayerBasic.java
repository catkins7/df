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
        TextView heroNameTextView = (TextView) findViewById(R.id.view_match_player_basic_hero_name_text_view);
        TextView matchIdTextView = (TextView) findViewById(R.id.view_match_player_basic_match_id_text_view);
        TextView timeAgoTextView = (TextView) findViewById(R.id.view_match_player_basic_date_text_view);
        TextView victoryTextView = (TextView) findViewById(R.id.view_match_player_basic_victory_text_view);

        matchIdTextView.setText(match.getGameModeString());
        timeAgoTextView.setText(match.getTimeAgoString());

        if (match.isRankedGame()) {
            rankedImageView.setVisibility(View.VISIBLE);
        }
        else {
            rankedImageView.setVisibility(View.INVISIBLE);
        }

        Player player = match.getPlayerForSteamUser(user);
        Hero hero = player == null ? null : Heroes.get().getHero(player.getHeroIdString());

        if (player != null) {
            victoryTextView.setText(match.getMatchResultStringResourceIdForPlayer(player));
            victoryTextView.setTextColor(getResources().getColor(match.getMatchResultColorResourceIdForPlayer(player)));
        }
        else {
            victoryTextView.setText("");
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
