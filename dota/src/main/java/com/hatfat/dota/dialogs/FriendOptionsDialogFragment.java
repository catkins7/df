package com.hatfat.dota.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;
import com.hatfat.dota.activities.LoadingActivity;
import com.hatfat.dota.model.friend.Friend;
import com.hatfat.dota.model.friend.Friends;
import com.hatfat.dota.view.FriendRowView;

import java.util.List;

public class FriendOptionsDialogFragment extends DialogFragment {

    private BroadcastReceiver receiver;

    private BaseAdapter friendAdapter;
    private ListView friendListView;

    private List<Friend> friendsList;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friendsList = Friends.get().getSortedFriendsList();
    }

    @Override public void onStart() {
        super.onStart();

        startListening();
    }

    @Override public void onStop() {
        super.onStop();

        stopListening();
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.customize_friend_dialog_title);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_friend_options, null);
        setupViews(view);
        builder.setView(view);

        return builder.create();
    }

    private void setupViews(View view) {
        Button createShortcutButton = (Button) view
                .findViewById(R.id.fragment_friend_options_create_shortcut_button);
        createShortcutButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                createFriendShortcut(v.getContext());
            }
        });

        friendListView = (ListView) view.findViewById(R.id.fragment_friend_options_listview);
        friendAdapter = new BaseAdapter() {
            @Override public int getCount() {
                return friendsList.size();
            }

            @Override public Friend getItem(int position) {
                return friendsList.get(position);
            }

            @Override public long getItemId(int position) {
                return 0;
            }

            @Override public View getView(int position, View convertView, ViewGroup parent) {
                FriendRowView friendRow = (FriendRowView) convertView;

                if (friendRow == null) {
                    friendRow = new FriendRowView(parent.getContext());
                }

                friendRow.setFriend(getItem(position));

                return friendRow;
            }
        };

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selectedFriend = (Friend) friendAdapter.getItem(position);
                Friends.get().selectNewFriend(selectedFriend, view.getContext());
            }
        });

        friendListView.setAdapter(friendAdapter);
    }

    private void createFriendShortcut(Context context) {
        // Intent to be send, when shortcut is pressed by user ("launched")
        Intent shortcutIntent = new Intent(context, LoadingActivity.class);

        // Create bitmap with number in it -> very default. You probably want to give it a more stylish look
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xffffffff);

        Friend friend = Friends.get().getCurrentFriend();

        Paint paint = new Paint();
        paint.setColor(0xFF006622);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(120);
        canvas.drawText(friend.getFriendId(), 128, 128, paint);

        // Decorate the shortcut
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.customize_friend_create_shortcut_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

        // Inform launcher to create shortcut
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }

    private void startListening() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Friends.CURRENT_FRIEND_CHANGED_NOTIFICATION:
                        if (friendListView != null && friendAdapter != null) {
                            for (int i = 0; i < friendListView.getChildCount(); i++) {
                                View view = friendListView.getChildAt(i);

                                if (view instanceof FriendRowView) {
                                    FriendRowView friendRowView = (FriendRowView) view;
                                    friendRowView.updateViews();
                                }
                            }
                        }
                        break;
                }
            }
        };

        IntentFilter friendFilter = new IntentFilter();
        friendFilter.addAction(Friends.CURRENT_FRIEND_CHANGED_NOTIFICATION);
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).registerReceiver(receiver, friendFilter);
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).unregisterReceiver(receiver);
    }
}
