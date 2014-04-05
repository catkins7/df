package com.hatfat.dota.model.player;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.hatfat.dota.model.DotaDiskGson;

import java.io.IOException;
import java.util.LinkedList;

public class PlayerDiskAdapter extends TypeAdapter<Player> {

    @Override
    public void write(JsonWriter jsonWriter, Player player) throws IOException {

        jsonWriter.value(player.accountId);
        jsonWriter.value(player.playerSlot);
        jsonWriter.value(player.heroId);
        jsonWriter.value(player.item0);
        jsonWriter.value(player.item1);
        jsonWriter.value(player.item2);
        jsonWriter.value(player.item3);
        jsonWriter.value(player.item4);
        jsonWriter.value(player.item5);
        jsonWriter.value(player.kills);
        jsonWriter.value(player.deaths);
        jsonWriter.value(player.assists);
        jsonWriter.value(player.leaverStatus);
        jsonWriter.value(player.gold);
        jsonWriter.value(player.lastHits);
        jsonWriter.value(player.denies);
        jsonWriter.value(player.goldPerMinute);
        jsonWriter.value(player.xpPerMinute);
        jsonWriter.value(player.goldSpent);
        jsonWriter.value(player.heroDamage);
        jsonWriter.value(player.towerDamage);
        jsonWriter.value(player.heroHealing);
        jsonWriter.value(player.level);

        int numAdditionalUnits = player.additionalUnits != null ? player.additionalUnits.size() : 0;
        jsonWriter.value(numAdditionalUnits);

        if (numAdditionalUnits > 0) {
            Gson gson = DotaDiskGson.getDotaDiskGson();

            for (AdditionalUnit unit : player.additionalUnits) {
                gson.toJson(unit, AdditionalUnit.class, jsonWriter);
            }
        }
    }

    @Override
    public Player read(JsonReader jsonReader) throws IOException {
        Player player = new Player();

        player.accountId = jsonReader.nextLong();
        player.playerSlot = jsonReader.nextInt();
        player.heroId = jsonReader.nextInt();
        player.item0 = jsonReader.nextInt();
        player.item1 = jsonReader.nextInt();
        player.item2 = jsonReader.nextInt();
        player.item3 = jsonReader.nextInt();
        player.item4 = jsonReader.nextInt();
        player.item5 = jsonReader.nextInt();
        player.kills = jsonReader.nextInt();
        player.deaths = jsonReader.nextInt();
        player.assists = jsonReader.nextInt();
        player.leaverStatus = jsonReader.nextInt();
        player.gold = jsonReader.nextInt();
        player.lastHits = jsonReader.nextInt();
        player.denies = jsonReader.nextInt();
        player.goldPerMinute = jsonReader.nextInt();
        player.xpPerMinute = jsonReader.nextInt();
        player.goldSpent = jsonReader.nextInt();
        player.heroDamage = jsonReader.nextInt();
        player.towerDamage = jsonReader.nextInt();
        player.heroHealing = jsonReader.nextInt();
        player.level = jsonReader.nextInt();

        player.additionalUnits = new LinkedList();
        int numberOfAdditionalUnits = jsonReader.nextInt();

        if (numberOfAdditionalUnits > 0) {
            Gson gson = DotaDiskGson.getDotaDiskGson();

            for (int i = 0; i < numberOfAdditionalUnits; i++){
                AdditionalUnit unit = gson.fromJson(jsonReader, AdditionalUnit.class);
                player.additionalUnits.add(unit);
            }
        }

        return player;
    }
}
