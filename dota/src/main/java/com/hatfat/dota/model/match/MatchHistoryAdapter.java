package com.hatfat.dota.model.match;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by scottrick on 2/12/14.
 */
public class MatchHistoryAdapter extends TypeAdapter<MatchHistory> {

    @Override
    public void write(JsonWriter jsonWriter, MatchHistory matchHistory) throws IOException {

    }

    @Override
    public MatchHistory read(JsonReader jsonReader) throws IOException {
        MatchHistory matchHistory= new MatchHistory();
        LinkedList<Match> matches = new LinkedList<>();

        boolean closeResultObject = false;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            try {
                switch (name) {
                    case "result":
                        jsonReader.beginObject();
                        closeResultObject = true;
                        break;
                    case "status":
                        matchHistory.status = jsonReader.nextInt();
                        break;
                    case "num_results":
                        matchHistory.numResults = jsonReader.nextInt();
                        break;
                    case "total_results":
                        matchHistory.totalResults = jsonReader.nextInt();
                        break;
                    case "results_remaining":
                        matchHistory.resultsRemaining = jsonReader.nextInt();
                        break;
                    case "matches":
                        jsonReader.beginArray();
                        MatchAdapter matchAdapter = new MatchAdapter();

                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            Match match = matchAdapter.read(jsonReader);
                            matches.add(match);
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
        if (closeResultObject) {
            jsonReader.endObject();
        }

        matchHistory.matches = matches;

        return matchHistory;
    }
}
