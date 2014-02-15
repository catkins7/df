package com.hatfat.dota.model.game;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by scottrick on 2/15/14.
 */
public class HeroAdapter extends TypeAdapter<Hero> {
    @Override
    public void write(JsonWriter jsonWriter, Hero hero) throws IOException {

    }

    @Override
    public Hero read(JsonReader jsonReader) throws IOException {
        Hero hero = new Hero();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
                case "name":
                    hero.name = jsonReader.nextString();
                    break;
                case "localized_name":
                    hero.localizedName = jsonReader.nextString();
                    break;
                case "id":
                    hero.heroId = jsonReader.nextInt();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();

        return hero;
    }
}
