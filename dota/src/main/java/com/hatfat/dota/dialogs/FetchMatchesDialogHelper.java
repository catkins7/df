package com.hatfat.dota.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;

public class FetchMatchesDialogHelper {

    SteamUser user;

    public FetchMatchesDialogHelper(SteamUser user) {
        this.user = user;
    }

    public void showFromActivity(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_fetch_matches, null));
        builder.setCancelable(false);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
