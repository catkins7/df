package com.hatfat.dota.model.match;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.hatfat.dota.model.DotaDiskGson;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.player.PlayerDiskAdapter;

import java.io.IOException;
import java.util.LinkedList;

public class MatchDiskAdapter extends TypeAdapter<Match> {

    @Override
    public void write(JsonWriter jsonWriter, Match match) throws IOException {

        jsonWriter.value(match.radiantWin);
        jsonWriter.value(match.duration);
        jsonWriter.value(match.startTime);
        jsonWriter.value(match.matchId);
        jsonWriter.value(match.matchSeqNumber);
        jsonWriter.value(match.towerStatusRadiant);
        jsonWriter.value(match.towerStatusDire);
        jsonWriter.value(match.barracksStatusRadiant);
        jsonWriter.value(match.barracksStatusDire);
        jsonWriter.value(match.cluster);
        jsonWriter.value(match.firstBloodTime);
        jsonWriter.value(match.lobbyType);
        jsonWriter.value(match.humanPlayers);
        jsonWriter.value(match.leagueId);
        jsonWriter.value(match.positiveVotes);
        jsonWriter.value(match.negativeVotes);
        jsonWriter.value(match.gameMode);
        jsonWriter.value(match.hasMatchDetails);

        int numPlayers = 0;
        if (match.players != null) {
            numPlayers = match.players.size();
        }

        jsonWriter.value(numPlayers);

        if (numPlayers > 0) {
            Gson gson = DotaDiskGson.getDefaultDotaDiskGson();
            TypeAdapter<Player> adapter = gson.getAdapter(Player.class);

            if (adapter instanceof PlayerDiskAdapter) {
                PlayerDiskAdapter playerDiskAdapter = (PlayerDiskAdapter) adapter;
                playerDiskAdapter.setHasAbilityUpgrades(match.shouldSavePlayerAbilities());
            }

            for (Player player : match.players) {
                adapter.write(jsonWriter, player);
            }
        }
    }

    @Override
    public Match read(JsonReader jsonReader) throws IOException {
        Match match = new Match();

        match.radiantWin = jsonReader.nextBoolean();
        match.duration = jsonReader.nextInt();
        match.startTime = jsonReader.nextLong();
        match.matchId = jsonReader.nextString();
        match.matchSeqNumber = jsonReader.nextLong();
        match.towerStatusRadiant = jsonReader.nextInt();
        match.towerStatusDire = jsonReader.nextInt();
        match.barracksStatusRadiant = jsonReader.nextInt();
        match.barracksStatusDire = jsonReader.nextInt();
        match.cluster = jsonReader.nextInt();
        match.firstBloodTime = jsonReader.nextInt();
        match.lobbyType = jsonReader.nextInt();
        match.humanPlayers = jsonReader.nextInt();
        match.leagueId = jsonReader.nextInt();
        match.positiveVotes = jsonReader.nextInt();
        match.negativeVotes = jsonReader.nextInt();
        match.gameMode = jsonReader.nextInt();
        match.hasMatchDetails = jsonReader.nextBoolean();

        match.players = new LinkedList();
        int numPlayers = jsonReader.nextInt();

        if (numPlayers > 0) {
            Gson gson = DotaDiskGson.getDefaultDotaDiskGson();
            TypeAdapter<Player> adapter = gson.getAdapter(Player.class);

            if (adapter instanceof PlayerDiskAdapter) {
                PlayerDiskAdapter playerDiskAdapter = (PlayerDiskAdapter) adapter;
                playerDiskAdapter.setHasAbilityUpgrades(match.shouldSavePlayerAbilities());
            }

            for (int i = 0; i < numPlayers; i++) {
                Player player = adapter.read(jsonReader);
                match.players.add(player);
            }
        }

        return match;
    }
}
