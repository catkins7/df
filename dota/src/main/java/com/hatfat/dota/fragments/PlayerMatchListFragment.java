package com.hatfat.dota.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hatfat.dota.R;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.util.ArrayList;
import java.util.List;

public class PlayerMatchListFragment extends CharltonFragment {

    private static String PLAYER_MATCH_LIST_USER_ID_KEY = "PLAYER_MATCH_LIST_USER_ID_KEY";
    private static String PLAYER_MATCH_LIST_LABEL_KEY = "PLAYER_MATCH_LIST_LABEL_KEY";
    private static String PLAYER_MATCH_LIST_MATCHES_KEY = "PLAYER_MATCH_LIST_MATCHES_KEY";

    private SteamUser user;
    private List<String> matchIds;
    private String matchesLabel;

    public static Bundle newBundleForUserAndMatches(String userId, String label, ArrayList<String> matchIds) {
        Bundle args = new Bundle();
        args.putString(PLAYER_MATCH_LIST_USER_ID_KEY, userId);
        args.putString(PLAYER_MATCH_LIST_LABEL_KEY, label);
        args.putStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY, matchIds);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = getArguments().getString(PLAYER_MATCH_LIST_USER_ID_KEY);

        user = SteamUsers.get().getBySteamId(userId);
        matchIds = getArguments().getStringArrayList(PLAYER_MATCH_LIST_MATCHES_KEY);
        matchesLabel = getArguments().getString(PLAYER_MATCH_LIST_LABEL_KEY);

//        startListening();

        signalCharltonActivityToUpdateTab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_match_list, container, false);

        return view;
    }

    @Override
    public String getCharltonMessageText(Resources resources) {
        if (user != null) {
            return String.format(resources.getString(R.string.player_match_list_charlton_text),
                    user.getDisplayName(), matchesLabel);
        }
        else {
            return null;
        }
    }
}
