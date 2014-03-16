package com.hatfat.dota.model.match;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
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

        loadFromDisk();
    }

    public void init() {}

    private void loadFromDisk() {
        for (SteamUser user : SteamUsers.get().getStarredUsers()) {
            loadMatchesFromDiskForUser(user);
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

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
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
            MatchesGsonObject obj = gson.fromJson(jsonReader, MatchesGsonObject.class);

            addMatches(obj.matches);

            Log.e("catfat", "loaded " + obj.matches.size() + " matches from disk for user " + user.getDisplayName());
        }
        catch (FileNotFoundException e) {
            Log.e("SteamUsers", "Error loading from disk: " + e.toString());
        }
    }
}
