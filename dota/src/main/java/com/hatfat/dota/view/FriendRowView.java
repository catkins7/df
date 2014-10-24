package com.hatfat.dota.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.friend.Friend;

public class FriendRowView extends RelativeLayout {

    private Friend friend;

    public FriendRowView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_friend_row, this, true);
    }

    public void setFriend(Friend friend) {
        this.friend = friend;

        updateViews();
    }

    public void updateViews() {
        if (friend != null) {
            ImageView friendImageView = (ImageView) findViewById(R.id.view_friend_row_friend_image_view);
            friendImageView.setImageResource(friend.getImageResourceId(getContext()));

            View backgroundView = findViewById(R.id.view_friend_row_container_relative_layout);
            TextView nameTextView = (TextView) findViewById(R.id.view_friend_row_top_textview);
            TextView descTextView = (TextView) findViewById(R.id.view_friend_row_bottom_textview);

            nameTextView.setText(friend.getName());
            descTextView.setText(friend.getDescription());

            if (friend.isCurrentFriend()) {
                backgroundView.setBackgroundResource(R.drawable.gold_black_button_background);
            }
            else {
                backgroundView.setBackgroundResource(R.drawable.off_black_button_background);
            }
        }
    }
}
