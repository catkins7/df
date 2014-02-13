package com.hatfat.dota.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by scottrick on 2/12/14.
 */
public class MatchHistoryAdapter extends TypeAdapter<MatchHistory> {

    @Override
    public void write(JsonWriter jsonWriter, MatchHistory matchHistory) throws IOException {

    }

    @Override
    public MatchHistory read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
