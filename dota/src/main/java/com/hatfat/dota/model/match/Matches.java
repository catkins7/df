package com.hatfat.dota.model.match;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 2/14/14.
 */
public class Matches {

    private static final String USER_MATCHES_FILE_EXTENSION = "_matches.json";

    public final static String MATCHES_LOADING_PROGRESS_NOTIFICATION = "MATCHES_LOADING_PROGRESS_NOTIFICATION";
    public final static String MATCHES_LOADING_PERCENT_COMPLETE = "MATCHES_LOADING_PERCENT_COMPLETE";
    private int loadingProgress;
    private boolean isLoaded;

    private static Matches singleton;

    private HashMap<String, Match> matches; //matchId --> match

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
            Log.e("catfat", "matches loaded already");
            Intent intent = new Intent(MATCHES_LOADING_PROGRESS_NOTIFICATION);
            intent.putExtra(MATCHES_LOADING_PERCENT_COMPLETE, 1.0f);
            LocalBroadcastManager.getInstance(DotaFriendApplication.CONTEXT).sendBroadcast(intent);
        }
        else {
            loadFromDisk();
        }
    }

    private void loadFromDisk() {
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

        if (matches.containsKey(match.matchId)) {
            matches.get(match.matchId).updateWithMatch(match);
        }
        else {
            matches.put(match.matchId, match);
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

    public Match getMatch(String matchId) {
        if (matches.containsKey(matchId)) {
            return matches.get(matchId);
        }
        else {
            Match newMatch = new Match(matchId);
            matches.put(matchId, newMatch);

            //auto fetch?
            return newMatch;
        }
    }

    public Collection<Match> getAllMatches() {
        return matches.values();
    }

    public void saveMatchesToDiskForUser(final SteamUser user) {
        Log.e("catfat", "saveMatchesToDiskForUser " + user.getDisplayName());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    LinkedList matchesList = new LinkedList();

                    for (String matchId : user.getMatches()) {
                        matchesList.add(getMatch(matchId));
                    }

                    MatchesGsonObject obj = new MatchesGsonObject();
                    obj.matches = matchesList;

                    File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();
                    fileDir.mkdirs();

                    File jsonFile = new File(fileDir, user.getAccountId() + USER_MATCHES_FILE_EXTENSION);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
                    JsonWriter jsonWriter = new JsonWriter(bw);

                    Gson gson = new Gson();
                    gson.toJson(obj, MatchesGsonObject.class, jsonWriter); // Write to file using BufferedWriter
                    jsonWriter.close();

                    Log.e("catfat", "wrote file size " + jsonFile.length());
                }
                catch (IOException e) {
                    Log.e("Matches", "Error saving to disk: " + e.toString());
                }

                return null;
            }
        }.execute();
    }

    public void loadMatchesFromDiskForUser(final SteamUser user) {
        try {
            File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();
            File jsonFile = new File(fileDir, user.getAccountId() + USER_MATCHES_FILE_EXTENSION);
            BufferedReader br = new BufferedReader(new FileReader(jsonFile));
            JsonReader jsonReader = new JsonReader(br);

            Gson gson = new Gson();
            MatchesGsonObject obj = null;

            try {
                obj = gson.fromJson(jsonReader, MatchesGsonObject.class);
            }
            catch (JsonSyntaxException jsonSyntaxException) {
                Log.e("Matches", "JsonSyntaxException Error parsing user " + user.getDisplayName());
            }

            if (obj != null) {
                addMatches(obj.matches);
                Log.e("Matches", "loaded " + obj.matches.size() + " matches from disk for user " + user.getDisplayName());
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Matches", "Error loading from disk: " + e.toString());
        }
    }
}
