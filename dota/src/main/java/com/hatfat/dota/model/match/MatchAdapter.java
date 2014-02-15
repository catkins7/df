package com.hatfat.dota.model.match;

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

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
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
                    match.lobbyType = jsonReader.nextInt();
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
                default:
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();

        return match;
    }
}
