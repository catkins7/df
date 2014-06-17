package com.hatfat.dota.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.CharltonActivity;

public class TextDialogHelper {

    private interface TextDialogInterface extends AdapterView.OnItemClickListener {
        public boolean isEnabled(int position);
    }

    public static void showHestonDialog(final Activity activity, final
            DialogInterface.OnDismissListener dismissListener) {
        int charltonResource = CharltonActivity.getRandomHestonDrawableResource(activity);
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), charltonResource);
        int size = (int) activity.getResources().getDimension(R.dimen.charlton_dialog_image_size);
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

        TextDialogHelper.showFromActivity(
                activity,
                R.string.charlton_dialog_title,
                R.array.charlton_string_array,
                new BitmapDrawable(activity.getResources(), bitmap),
                new TextDialogInterface() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
                        switch (position) {
                            case 3:
                            {
                                //planet of the apes row
                                String url = "http://www.wikipedia.org/wiki/Planet_of_the_Apes_(1968_film)";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                activity.startActivity(intent);
                                break;
                            }
                            case 4: {
                                //ben-hur row
                                String url = "http://www.wikipedia.org/wiki/Ben-Hur_(1959_film)";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                activity.startActivity(intent);
                                break;

                            }
                            case 5:
                                //charlton wikipedia row
                                String url = "http://www.wikipedia.org/wiki/Charlton_Heston";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                activity.startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return position > 2;
                    }
                },
                dismissListener);
    }

    public static void showStatsDialog(final Activity activity) {
        TextDialogHelper.showFromActivity(
                activity,
                R.string.player_summary_menu_info_text,
                R.array.stats_info_string_array,
                activity.getResources().getDrawable(R.drawable.ic_launcher),
                null,
                null);
    }

    public static void showAboutDialog(final Activity activity) {
        TextDialogHelper.showFromActivity(
                activity,
                R.string.about_dialog_title,
                R.array.about_string_array,
                activity.getResources().getDrawable(R.drawable.ic_launcher),
                new TextDialogInterface() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
                        switch (position) {
                            case 3:
                            {
                                //twitter row
                                String tweetUrl =
                                        String.format(
                                                "https://twitter.com/intent/tweet?text=%s",
                                                Uri.encode("@scottrick49 "));
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                                activity.startActivity(intent);
                                break;
                            }
                            case 4: {
                                //email row
                                Resources resources = activity.getResources();
                                String suggestedSubject = resources.getString(
                                        R.string.about_dialog_email_suggested_subject);
                                String suggestedEmail = resources
                                        .getString(R.string.about_dialog_email_address);
                                String intentChooserTitle = resources.getString(
                                        R.string.about_dialog_email_intent_chooser_title);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("message/rfc822");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{suggestedEmail});
                                intent.putExtra(Intent.EXTRA_SUBJECT, suggestedSubject);

                                activity.startActivity(
                                        Intent.createChooser(intent, intentChooserTitle));
                                break;
                            }
                        }
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return position > 2;
                    }
                },
                null);
    }

    private static void showFromActivity(final Activity activity, int titleStringResourceId, int messageStringArrayResourceId, Drawable iconDrawable, final TextDialogInterface listener, final
            DialogInterface.OnDismissListener dismissListener) {
        if (activity == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View contentView = inflater.inflate(R.layout.dialog_info, null);

        builder.setIcon(iconDrawable);
        builder.setTitle(titleStringResourceId);
        builder.setView(contentView);
        builder.setOnDismissListener(dismissListener);

        final String[] messages = activity.getResources().getStringArray(messageStringArrayResourceId);

        ListView listView = (ListView) contentView.findViewById(R.id.dialog_info_list_view);
        listView.setOnItemClickListener(listener);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public boolean isEnabled(int position) {
                if (listener != null) {
                    return listener.isEnabled(position);
                }

                return false;
            }

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
