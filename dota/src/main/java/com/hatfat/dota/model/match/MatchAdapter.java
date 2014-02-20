package com.hatfat.dota.model.match;

import android.util.Log;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.player.PlayerAdapter;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by scottrick on 2/13/14.
 */
public class MatchAdapter extends TypeAdapter<Match> {
    @Override
    public void write(JsonWriter jsonWriter, Match match) throws IOException {

    }

    @Override
    public Match read(JsonReader jsonReader) throws IOException {
        Match match = new Match();
        LinkedList<Player> players = new LinkedList<>();

        boolean didOpenResult = false;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

//radiant_win: true,
//duration: 3394,
//tower_status_radiant: 1926,
//tower_status_dire: 0,
//barracks_status_radiant: 63,
//barracks_status_dire: 0,
//cluster: 123,
//first_blood_time: 152,
//human_players: 10,
//leagueid: 0,
//positive_votes: 0,
//negative_votes: 0,
//game_mode: 1

            switch (name) {
                case "radiant_win":
                    boolean result = jsonReader.nextBoolean();
                    match.matchResult = result ? Match.MatchResult.MATCH_RESULT_RADIANT_VICTORY : Match.MatchResult.MATCH_RESULT_DIRE_VICTORY;
                    break;
                case "duration":
                    match.duration = jsonReader.nextInt();
                    break;
                case "tower_status_radiant":
                    match.towerStatusRadiant = jsonReader.nextInt();
                    break;
                case "tower_status_dire":
                    match.towerStatusDire = jsonReader.nextInt();
                    break;
                case "barracks_status_radiant":
                    match.barracksStatusRadiant = jsonReader.nextInt();
                    break;
                case "barracks_status_dire":
                    match.barracksStatusDire = jsonReader.nextInt();
                    break;
                case "cluster":
                    match.cluster = jsonReader.nextInt();
                    break;
                case "first_blood_time":
                    match.firstBloodTime = jsonReader.nextInt();
                    break;
                case "human_players":
                    match.humanPlayers = jsonReader.nextInt();
                    break;
                case "leagueid":
                    match.leagueId = jsonReader.nextInt();
                    break;
                case "positive_votes":
                    match.positiveVotes = jsonReader.nextInt();
                    break;
                case "negative_votes":
                    match.negativeVotes = jsonReader.nextInt();
                    break;
                case "game_mode":
                    match.gameMode = jsonReader.nextInt();
                    break;
                case "match_id":
                    match.matchId = jsonReader.nextString();
                    break;
                case "match_seq_num":
                    match.matchSeqNumber = jsonReader.nextLong();
                    break;
                case "start_time":
                    match.startTime = jsonReader.nextLong();
                    break;
                case "lobby_type":
                    match.lobbyType = Match.LobbyType.fromInt(jsonReader.nextInt());
                    break;
                case "players":
                    jsonReader.beginArray();
                    PlayerAdapter playerAdapter = new PlayerAdapter();

                    while (jsonReader.peek() != JsonToken.END_ARRAY) {
                        Player player = playerAdapter.read(jsonReader);
                        players.add(player);
                    }

                    jsonReader.endArray();
                    break;
                case "result":
                    jsonReader.beginObject();
                    didOpenResult = true;
                    break;
                default:
                    Log.e("catfat", "MatchAdapter skipping: " + name);
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();
        if (didOpenResult) {
            jsonReader.endObject();
        }

        match.players = players;

        return match;
    }
}
