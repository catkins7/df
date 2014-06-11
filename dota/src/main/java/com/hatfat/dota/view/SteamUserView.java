package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.squareup.picasso.Picasso;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUserView extends RelativeLayout {

    private String steamUserId;

    public SteamUserView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_steam_user, this, true);
    }

    public String getSteamUserId() {
        return steamUserId;
    }

    public void setSteamUser(SteamUser user) {
        steamUserId = user.getSteamId();

        updateViews();
    }

    public void notifyMatchUpdated() {
        updateViews();
    }

    private void updateViews() {
        SteamUser user = SteamUsers.get().getBySteamId(steamUserId);

        if (user != null) {
            TextView nameTextView = (TextView) findViewById(R.id.steam_user_view_name_text_view);
            TextView statusTextView = (TextView) findViewById(R.id.steam_user_view_status_text_view);
            ImageView imageView = (ImageView) findViewById(R.id.steam_user_view_image_view);

            nameTextView.setText(user.getDisplayName());
            statusTextView.setText(user.getCurrentStateDescriptionString());
            statusTextView.setTextColor(user.getCurrentStateDescriptionTextColor(getResources()));

            Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(imageView);
        }
    }
}
