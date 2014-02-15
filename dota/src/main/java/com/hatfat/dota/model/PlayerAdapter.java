package com.hatfat.dota.model;

import android.util.Log;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by scottrick on 2/13/14.
 */
public class PlayerAdapter extends TypeAdapter<Player> {

    @Override
    public void write(JsonWriter jsonWriter, Player player) throws IOException {

    }

    @Override
    public Player read(JsonReader jsonReader) throws IOException {
        Player player = new Player();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
                case "account_id":
                    player.accountId = jsonReader.nextLong();
                    break;
                case "player_slot":
                    player.playerSlot = jsonReader.nextInt();
                    break;
                case "hero_id":
                    player.heroId = jsonReader.nextInt();
                    break;
                default:
                    Log.e("catfat", "skipping: " + name);
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();

        return player;
    }
}
