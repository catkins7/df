package com.hatfat.dota.model.game;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by scottrick on 2/15/14.
 */
public class HeroDataAdapter extends TypeAdapter<HeroData>  {
    @Override
    public void write(JsonWriter jsonWriter, HeroData heroData) throws IOException {

    }

    @Override
    public HeroData read(JsonReader jsonReader) throws IOException {
        HeroData data = new HeroData();

        HashMap<String, Hero> heroes = new HashMap<>();

        boolean closeResultObject = false;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
                case "result":
                    jsonReader.beginObject();
                    closeResultObject = true;
                    break;
                case "heroes":
                    jsonReader.beginArray();
                    HeroAdapter heroAdapter = new HeroAdapter();

                    while (jsonReader.peek() != JsonToken.END_ARRAY) {
                        Hero hero = heroAdapter.read(jsonReader);
                        heroes.put(hero.name, hero);
                    }

                    jsonReader.endArray();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();
        if (closeResultObject) {
            jsonReader.endObject();
        }

        data.heroes = heroes;

        return data;
    }
}
