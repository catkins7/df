package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.model.user.CommonMatches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.squareup.picasso.Picasso;

public class CommonMatchSteamUserView extends RelativeLayout {

    private CommonMatches commonMatches;

    public CommonMatchSteamUserView(final Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_common_match_user_view, this, true);

        //setup the friend button!
        Button friendToggleButton = (Button) findViewById(R.id.view_common_match_friend_button);
        friendToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SteamUser user = SteamUsers.get().getByAccountId(commonMatches.getUserTwoAccountId());

                if (user != null) {
                    SteamUsers.get().toggleStarForUser(user, context);
                }
            }
        });
    }

    public void setCommonMatches(CommonMatches commonMatches) {
        this.commonMatches = commonMatches;

        updateViews();
    }

    public String getSteamUserAccountId() {
        return SteamUser.getSteamIdFromAccountId(commonMatches.getUserTwoAccountId());
    }

    public void notifyUserUpdated() {
        updateViews();
    }

    private void updateViews() {
        SteamUser user = SteamUsers.get().getByAccountId(commonMatches.getUserTwoAccountId());

        if (user != null) {
            TextView nameTextView = (TextView) findViewById(R.id.view_common_match_view_name_text_view);
            TextView matchCountTextView = (TextView) findViewById(R.id.view_common_match_view_match_count_text_view);
            TextView topTextView = (TextView) findViewById(R.id.view_common_match_view_right_info_top_text_view);
            ImageView imageView = (ImageView) findViewById(R.id.view_common_match_view_image_view);

            nameTextView.setText(user.getDisplayName());
            matchCountTextView.setText(commonMatches.getMatchCountString(getResources()));
            topTextView.setText(commonMatches.getOverallWinRateString(getResources()));

            Picasso.with(DotaFriendApplication.CONTEXT).load(user.getAvatarFullUrl()).placeholder(R.drawable.ic_launcher).into(imageView);
        }

        updateFriendButtonBackground();
    }

    public void updateFriendButtonBackground() {
        SteamUser user = SteamUsers.get().getByAccountId(commonMatches.getUserTwoAccountId());
        Button friendToggleButton = (Button) findViewById(R.id.view_common_match_friend_button);

        if (user != null) {
            if (user.isRealUser()) {
                friendToggleButton.setVisibility(View.VISIBLE);
            } else {
                friendToggleButton.setVisibility(View.GONE);
            }

            if (SteamUsers.get().isUserStarred(user)) {
                friendToggleButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
            } else {
                friendToggleButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }
        }
        else {
            friendToggleButton.setVisibility(View.GONE);
        }
    }
}
