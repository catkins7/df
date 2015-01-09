package com.hatfat.dota.model.match;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.model.DotaDiskGson;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.util.FileUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Matches {

    public final static String MATCHES_LOADING_PROGRESS_NOTIFICATION = "MATCHES_LOADING_PROGRESS_NOTIFICATION";
    public final static String MATCHES_LOADING_PERCENT_COMPLETE = "MATCHES_LOADING_PERCENT_COMPLETE";

    public static final String USER_MATCHES_FILE_EXTENSION = "_matches.json";

    private int loadingProgress;
    private boolean isLoaded;

    private static Matches singleton;

    private HashMap<Long, Match> matches; //matchId --> match

    public static Matches get() {
        if (singleton == null) {
            singleton = new Matches();
        }

        return singleton;
    }

    private Matches() {
        matches = new HashMap<>();
    }

    public void load() {
        if (isLoaded) {
            Intent intent = new Intent(MATCHES_LOADING_PROGRESS_NOTIFICATION);
            intent.putExtra(MATCHES_LOADING_PERCENT_COMPLETE, 1.0f);
            LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
        }
        else {
            loadFromDisk();
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private void loadFromDisk() {
        if (SteamUsers.get().getStarredUsers().size() <= 0) {
            //no users!  so we are done
            isLoaded = true;

            Intent intent = new Intent(MATCHES_LOADING_PROGRESS_NOTIFICATION);
            intent.putExtra(MATCHES_LOADING_PERCENT_COMPLETE, 1.0f);
            LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);

            return;
        }

        loadingProgress = 0;

        for (final SteamUser user : SteamUsers.get().getStarredUsers()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    loadMatchesFromDiskForUser(user);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    loadingProgress++;
                    float percentComplete = (float)loadingProgress / (float)SteamUsers.get().getStarredUsers().size();

                    if (loadingProgress == SteamUsers.get().getStarredUsers().size()) {
                        //finished loading!
                        isLoaded = true;
                    }

                    Intent intent = new Intent(MATCHES_LOADING_PROGRESS_NOTIFICATION);
                    intent.putExtra(MATCHES_LOADING_PERCENT_COMPLETE, percentComplete);
                    LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
                }
            }.execute();
        }
    }

    public void addMatch(Match match) {
        if (match == null) {
            return;
        }

        if (matches.containsKey(match.getMatchIdLong())) {
            Match existingMatch = matches.get(match.getMatchIdLong());
            existingMatch.updateWithMatch(match);
        }
        else {
            matches.put(match.getMatchIdLong(), match);
        }
    }

    public void addMatches(List<Match> newMatches) {
        if (newMatches == null) {
            return;
        }

        for (Match match : newMatches) {
            addMatch(match);
        }
    }

    public Match getMatch(Long matchId) {
        if (matches.containsKey(matchId)) {
            return matches.get(matchId);
        }
        else {
            Match newMatch = new Match(String.valueOf(matchId));
            matches.put(matchId, newMatch);

            //don't auto fetch
            return newMatch;
        }
    }

    public Collection<Match> getAllMatches() {
        return matches.values();
    }

    public void saveMatchesToDiskForUser(final SteamUser user) {
        long startTime = System.currentTimeMillis();
        LinkedList matchesList = new LinkedList();

        for (Long matchId : user.getMatches()) {
            matchesList.add(getMatch(matchId));
        }

        MatchesGsonObject obj = DotaDiskGson.getDefaultMatchesGsonObject();
        obj.matches = matchesList;

        FileUtil.saveObjectToDisk(user.getAccountId() + USER_MATCHES_FILE_EXTENSION, obj);
        long endTime = System.currentTimeMillis();

        Log.v("Matches", "saved " + obj.matches.size() + " matches to disk for user " + user.getDisplayName() + " in " + (endTime - startTime) + " millis");
    }

    public void loadMatchesFromDiskForUser(final SteamUser user) {
        long startTime = System.currentTimeMillis();
        MatchesGsonObject obj = FileUtil.loadObjectFromDisk(user.getAccountId() + USER_MATCHES_FILE_EXTENSION, MatchesGsonObject.class);
        long endTime = System.currentTimeMillis();

        if (obj != null) {
            addMatches(obj.matches);

            //make sure the steam user's match list is up to date!
            user.addMatches(obj.matches);

            Log.v("Matches", "loaded " + obj.matches.size() + " matches from disk for user " + user.getDisplayName() + " in " + (endTime - startTime) + " millis");
        }
    }
}
