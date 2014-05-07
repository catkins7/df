package com.hatfat.dota.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hatfat.dota.R;

/**
 * Created by scottrick on 2/12/14.
 */
public class CharltonMessageFragment extends Fragment {

    private TextView messageTextView;
    private CharltonFragment charltonFragment;

    @Override
    public void onResume() {
        super.onResume();

        updateMessageViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charlton_message, container, false);

        messageTextView = (TextView) view.findViewById(R.id.fragment_charlton_message_textview);

        return view;
    }

    public void setCharltonObject(CharltonFragment newMessageInterface) {
        if (charltonFragment != newMessageInterface) {
            charltonFragment = newMessageInterface;

            updateMessageViews();
        }
    }

    private void updateMessageViews() {
        if (messageTextView != null && charltonFragment != null) {
            messageTextView.setText(charltonFragment.getCharltonMessageText(getResources()));
        }
    }
}
