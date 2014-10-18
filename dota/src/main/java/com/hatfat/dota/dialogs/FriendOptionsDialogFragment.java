package com.hatfat.dota.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.LoadingActivity;
import com.hatfat.dota.model.friend.Friends;

public class FriendOptionsDialogFragment extends DialogFragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_options, container, false);

        Button createShortcutButton = (Button) view.findViewById(R.id.fragment_friend_options_create_shortcut_button);
        createShortcutButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                createFriendShortcut(v.getContext());
            }
        });

        return view;
    }

    private void createFriendShortcut(Context context) {
        // Intent to be send, when shortcut is pressed by user ("launched")
        Intent shortcutIntent = new Intent(context, LoadingActivity.class);

        // Create bitmap with number in it -> very default. You probably want to give it a more stylish look
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xffffffff);

        Friends.Friend friend = Friends.get().getCurrentFriend();

        Paint paint = new Paint();
        paint.setColor(0xFF006622);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(120);
        canvas.drawText(String.valueOf(friend.getType()), 128, 128, paint);

        // Decorate the shortcut
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.customize_friend_create_shortcut_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

        // Inform launcher to create shortcut
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }
}
