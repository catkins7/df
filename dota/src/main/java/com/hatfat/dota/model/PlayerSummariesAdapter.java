package com.hatfat.dota.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by scottrick on 2/10/14.
 */
public class PlayerSummariesAdapter extends TypeAdapter<PlayerSummaries> {

        @Override
        public void write(JsonWriter jsonWriter, PlayerSummaries summaries) throws IOException {

        }

        @Override
        public PlayerSummaries read(JsonReader jsonReader) throws IOException {
            LinkedList<SteamUser> users = new LinkedList<>();

            boolean closeResponseObject = false;

            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.skipValue();
                    continue;
                }

                try {
                    switch (name) {
                        case "response":
                            jsonReader.beginObject();
                            closeResponseObject = true;
                            break;
                        case "players":
                            jsonReader.beginArray();

                            while (jsonReader.peek() != JsonToken.END_ARRAY) {
                                SteamUserAdapter userAdapter = new SteamUserAdapter();
                                SteamUser user = userAdapter.read(jsonReader);
                                users.add(user);
                            }

                            jsonReader.endArray();
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                } catch (IllegalStateException ex) {
                    jsonReader.skipValue();
                    // continue
                }
            }

            jsonReader.endObject();
            if (closeResponseObject) {
                jsonReader.endObject();
            }

            PlayerSummaries summaries = new PlayerSummaries();
            summaries.users = users;

            return summaries;
        }
}
