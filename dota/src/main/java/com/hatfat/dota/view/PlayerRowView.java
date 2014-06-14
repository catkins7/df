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
import com.hatfat.dota.model.player.AdditionalUnit;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.squareup.picasso.Picasso;

/**
 * Created by scottrick on 2/23/14.
 */
public class PlayerRowView extends RelativeLayout {

    private Player player;
    private boolean isPlayerOfTheMatch;

    public PlayerRowView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_player_row, this, true);
    }

    public void setPlayer(Player newPlayer, boolean isPlayerOfTheMatch) {
        this.player = newPlayer;
        this.isPlayerOfTheMatch = isPlayerOfTheMatch;

        updateViews();
    }

    public Player getPlayer() {
        return player;
    }

    public void notifyPlayerUpdated() {
        updateViews();
    }

    private void updateViews() {
        if (player != null) {
            View container = findViewById(R.id.view_player_row_container_relative_layout);
            ImageView heroImageView = (ImageView) findViewById(R.id.view_player_row_hero_image_view);
            TextView playerNameTextView = (TextView) findViewById(R.id.view_player_row_player_name_text_view);
            TextView kdaTextView = (TextView) findViewById(R.id.view_player_row_kda_text_view);
            TextView gpmTextView = (TextView) findViewById(R.id.view_player_row_gpm_text_view);
            TextView xpmTextView = (TextView) findViewById(R.id.view_player_row_xpm_text_view);
            ImageView itemImageView0 = (ImageView) findViewById(R.id.view_player_row_item_image_view_0);
            ImageView itemImageView1 = (ImageView) findViewById(R.id.view_player_row_item_image_view_1);
            ImageView itemImageView2 = (ImageView) findViewById(R.id.view_player_row_item_image_view_2);
            ImageView itemImageView3 = (ImageView) findViewById(R.id.view_player_row_item_image_view_3);
            ImageView itemImageView4 = (ImageView) findViewById(R.id.view_player_row_item_image_view_4);
            ImageView itemImageView5 = (ImageView) findViewById(R.id.view_player_row_item_image_view_5);
            View additionalUnitsContainer = findViewById(R.id.view_player_row_additional_unit_container_view);

            //update player of the match background if necessary
            if (player.isRadiantPlayer()) {
                if (isPlayerOfTheMatch) {
                    container.setBackgroundResource(R.drawable.gold_green_button_background);
                }
                else {
                    container.setBackgroundResource(R.drawable.black_green_button_background);
                }
            }
            else if (player.isDirePlayer()) {
                if (isPlayerOfTheMatch) {
                    container.setBackgroundResource(R.drawable.gold_red_button_background);
                }
                else {
                    container.setBackgroundResource(R.drawable.black_red_button_background);
                }
            }
            else {
                container.setBackgroundResource(R.drawable.off_black_button_background);
            }

            Hero hero = Heroes.get().getHero(player.getHeroIdString());
            SteamUser user = SteamUsers.get().getByAccountId(String.valueOf(player.getAccountId()));

            if (user != null) {
                playerNameTextView.setText(user.getDisplayName());
            }
            else {
                playerNameTextView.setText(String.valueOf(player.getAccountId()));
            }

            kdaTextView.setText(player.getKdaString());
            gpmTextView.setText(player.getLastHitString(getResources()));
            xpmTextView.setText(player.getLevelString(getResources()));

            setItemImageView(itemImageView0, player.getItemImageUrl(0));
            setItemImageView(itemImageView1, player.getItemImageUrl(1));
            setItemImageView(itemImageView2, player.getItemImageUrl(2));
            setItemImageView(itemImageView3, player.getItemImageUrl(3));
            setItemImageView(itemImageView4, player.getItemImageUrl(4));
            setItemImageView(itemImageView5, player.getItemImageUrl(5));

            if (player.hasAdditionalUnitsWeWantToShow()) {
                additionalUnitsContainer.setVisibility(VISIBLE);

                //we only support showing one additional unit at the time
                AdditionalUnit additionalUnit = player.getAdditionalUnits().get(0);

                ImageView additionalUnitIconImageView = (ImageView) findViewById(R.id.view_player_row_additional_unit_image_view);
                ImageView additionalItemImageView0 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_0);
                ImageView additionalItemImageView1 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_1);
                ImageView additionalItemImageView2 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_2);
                ImageView additionalItemImageView3 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_3);
                ImageView additionalItemImageView4 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_4);
                ImageView additionalItemImageView5 = (ImageView) findViewById(R.id.view_player_row_additional_unit_item_image_view_5);

                additionalUnitIconImageView.setImageResource(additionalUnit.getIconResource());
                setItemImageView(additionalItemImageView0, additionalUnit.getItemImageUrl(0));
                setItemImageView(additionalItemImageView1, additionalUnit.getItemImageUrl(1));
                setItemImageView(additionalItemImageView2, additionalUnit.getItemImageUrl(2));
                setItemImageView(additionalItemImageView3, additionalUnit.getItemImageUrl(3));
                setItemImageView(additionalItemImageView4, additionalUnit.getItemImageUrl(4));
                setItemImageView(additionalItemImageView5, additionalUnit.getItemImageUrl(5));
            }
            else {
                additionalUnitsContainer.setVisibility(GONE);
            }

            if (hero != null) {
                Picasso.with(DotaFriendApplication.CONTEXT).load(Heroes.get().getHero(player.getHeroIdString()).getLargeHorizontalPortraitUrl()).placeholder(R.drawable.ic_launcher).into(heroImageView);
            }
            else {
                heroImageView.setImageDrawable(null);
            }
        }
    }

    private void setItemImageView(ImageView imageView, String itemImageUrl) {
        if (itemImageUrl != null) {
            Picasso.with(DotaFriendApplication.CONTEXT).load(itemImageUrl).placeholder(R.drawable.ic_launcher).into(imageView);
        }
        else {
            imageView.setImageResource(R.drawable.empty_item_bg);
        }
    }
}
