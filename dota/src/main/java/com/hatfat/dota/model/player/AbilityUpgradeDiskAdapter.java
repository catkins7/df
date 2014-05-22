package com.hatfat.dota.model.player;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class AbilityUpgradeDiskAdapter extends TypeAdapter<AbilityUpgrade> {

    @Override
    public void write(JsonWriter jsonWriter, AbilityUpgrade abilityUpgrade) throws IOException {
        jsonWriter.value(abilityUpgrade.abilityId);
        jsonWriter.value(abilityUpgrade.time);
        jsonWriter.value(abilityUpgrade.level);
    }

    @Override
    public AbilityUpgrade read(JsonReader jsonReader) throws IOException {
        AbilityUpgrade abilityUpgrade = new AbilityUpgrade();

        abilityUpgrade.abilityId = jsonReader.nextInt();
        abilityUpgrade.time = jsonReader.nextInt();
        abilityUpgrade.level = jsonReader.nextInt();

        return abilityUpgrade;
    }
}
