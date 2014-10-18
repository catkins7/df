package com.hatfat.dota.fragments;

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

import com.hatfat.dota.R;
import com.hatfat.dota.activities.LoadingActivity;

public class FriendOptionsDialogFragment extends DialogFragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_options, container, false);

        return view;
    }

    private static void shortcutAdd(Context context, String name, int number) {
        // Intent to be send, when shortcut is pressed by user ("launched")
        Intent shortcutIntent = new Intent(context, LoadingActivity.class);

        // Create bitmap with number in it -> very default. You probably want to give it a more stylish look
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xffffffff);

        Paint paint = new Paint();
        paint.setColor(0xFF00aaaa);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100);
        canvas.drawText("" + number, 128, 128, paint);

        // Decorate the shortcut
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

        // Inform launcher to create shortcut
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }
}
