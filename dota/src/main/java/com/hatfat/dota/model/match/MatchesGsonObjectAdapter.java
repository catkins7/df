package com.hatfat.dota.model.match;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import com.hatfat.dota.model.DotaDiskGson;
import com.hatfat.dota.model.DotaGson;

import java.io.IOException;
import java.util.LinkedList;

public class MatchesGsonObjectAdapter extends TypeAdapter<MatchesGsonObject> {

    private Gson getGsonForVersion(int version) {
        switch (version) {
            case 0:
                return DotaGson.getDotaGson();
            case 1:
                return DotaDiskGson.getDotaDiskGsonVersion1();
            case 2:
                return DotaDiskGson.getDotaDiskGsonVersion2();
            default:
                Log.e("Matches", "Unsupported matches version " + version);
                return null;
        }
    }

    @Override
    public void write(JsonWriter jsonWriter, MatchesGsonObject matches) throws IOException {

        jsonWriter.beginObject();

        //writer version first, always!
        jsonWriter.name("version");
        jsonWriter.value(matches.version);

        Gson gson = getGsonForVersion(matches.version);

        jsonWriter.name("matches");
        jsonWriter.beginArray();

        if (gson != null) {
            for (Match match : matches.matches) {
                gson.toJson(match, Match.class, jsonWriter);
            }
        }

        jsonWriter.endArray();

        jsonWriter.endObject();
    }

    @Override
    public MatchesGsonObject read(JsonReader jsonReader) throws IOException {
        MatchesGsonObject matches = new MatchesGsonObject(0); //default to original save format
        String name;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
                case "version":
                    matches.version = jsonReader.nextInt();
                    break;
                case "matches":
                    matches.matches = new LinkedList();

                    Gson gson = getGsonForVersion(matches.version);

                    jsonReader.beginArray();

                    if (gson != null) {
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            Match match = gson.fromJson(jsonReader, Match.class);
                            matches.matches.add(match);
                        }
                    }

                    jsonReader.endArray();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();

        return matches;
    }
}
