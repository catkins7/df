package com.hatfat.dota.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.R;

public class InfoDialogHelper {

    public static void showFromActivity(final Activity activity) {
        if (activity == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View contentView = inflater.inflate(R.layout.dialog_info, null);

        builder.setTitle(R.string.player_statistics_menu_info_text);
        builder.setView(contentView);

        final String[] messages = activity.getResources().getStringArray(R.array.stats_info_string_array);

        ListView listView = (ListView) contentView.findViewById(R.id.dialog_info_list_view);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return messages.length;
            }

            @Override
            public String getItem(int position) {
                return messages[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;

                if (textView == null) {
                    float titleFontSize = activity.getResources().getDimensionPixelSize(R.dimen.font_size_medium);
                    int padding = (int) activity.getResources().getDimension(R.dimen.default_padding);

                    textView = new TextView(parent.getContext());
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setTextColor(activity.getResources().getColor(R.color.off_white));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleFontSize);
                }

                textView.setText(Html.fromHtml(messages[position]), TextView.BufferType.SPANNABLE);

                return textView;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(activity);
        dialog.show();
    }
}
