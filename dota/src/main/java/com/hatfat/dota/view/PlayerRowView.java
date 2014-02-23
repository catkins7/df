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
import com.hatfat.dota.model.player.Player;
import com.squareup.picasso.Picasso;

/**
 * Created by scottrick on 2/23/14.
 */
public class PlayerRowView extends RelativeLayout {

    private Player player;

    public PlayerRowView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_player_row, this, true);
    }

    public void setPlayer(Player newPlayer) {
        player = newPlayer;

        updateViews();
    }

    private void updateViews() {
        if (player != null) {
            View container = findViewById(R.id.view_player_row_container_relative_layout);
            ImageView heroImageView = (ImageView) findViewById(R.id.view_player_row_hero_image_view);
            TextView playerNameTextView = (TextView) findViewById(R.id.view_player_row_player_name_text_view);

            if (player.isRadiantPlayer()) {
                container.setBackgroundResource(R.drawable.black_green_button_background);
            }
            else if (player.isDirePlayer()) {
                container.setBackgroundResource(R.drawable.black_red_button_background);
            }
            else {
                container.setBackgroundResource(R.drawable.off_black_button_background);
            }

            Hero hero = Heroes.get().getHero(player.getHeroIdString());
            playerNameTextView.setText(String.valueOf(player.getAccountId()));


            if (hero != null) {
                Picasso.with(DotaFriendApplication.CONTEXT).load(Heroes.get().getHero(player.getHeroIdString()).getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(heroImageView);
            }
            else {
                heroImageView.setImageDrawable(null);
            }
        }
    }
}
