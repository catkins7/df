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

    public void setSteamUser(SteamUser user) {
        steamUserId = user.getSteamId();

        updateViews();
    }

    private void updateViews() {
        SteamUser user = SteamUsers.get().getBySteamId(steamUserId);

        if (user != null) {
            TextView textView = (TextView) findViewById(R.id.steam_user_view_text_view);
            ImageView imageView = (ImageView) findViewById(R.id.steam_user_view_image_view);

            textView.setText(user.getPersonaName());
            Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(imageView);
        }
    }
}
