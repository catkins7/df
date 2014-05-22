package com.hatfat.dota.model.player;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class AdditionalUnitDiskAdapter extends TypeAdapter<AdditionalUnit> {

    @Override
    public void write(JsonWriter jsonWriter, AdditionalUnit unit) throws IOException {
        jsonWriter.value(unit.unitName);
        jsonWriter.value(unit.item0);
        jsonWriter.value(unit.item1);
        jsonWriter.value(unit.item2);
        jsonWriter.value(unit.item3);
        jsonWriter.value(unit.item4);
        jsonWriter.value(unit.item5);
    }

    @Override
    public AdditionalUnit read(JsonReader jsonReader) throws IOException {
        AdditionalUnit unit = new AdditionalUnit();

        unit.unitName = jsonReader.nextString();
        unit.item0 = jsonReader.nextInt();
        unit.item1 = jsonReader.nextInt();
        unit.item2 = jsonReader.nextInt();
        unit.item3 = jsonReader.nextInt();
        unit.item4 = jsonReader.nextInt();
        unit.item5 = jsonReader.nextInt();

        return unit;
    }
}
