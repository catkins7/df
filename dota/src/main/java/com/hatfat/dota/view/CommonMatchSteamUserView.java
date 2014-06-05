package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
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

    public CommonMatchSteamUserView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_common_match_user_view, this, true);
    }

    public void setCommonMatches(CommonMatches commonMatches) {
        this.commonMatches = commonMatches;

        updateViews();
    }

    public String getSteamUserAccountId() {
        return SteamUser.getSteamIdFromAccountId(commonMatches.getUserTwoAccountId());
    }

    public void notifyMatchUpdated() {
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
    }
}
