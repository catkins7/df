package com.hatfat.dota.model.user;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by scottrick on 2/10/14.
 */
public class SteamUserAdapter extends TypeAdapter<SteamUser> {

    @Override
    public void write(JsonWriter jsonWriter, SteamUser steamUser) throws IOException {

    }

    @Override
    public SteamUser read(JsonReader jsonReader) throws IOException {
        SteamUser user = new SteamUser();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();
                continue;
            }

            switch (name) {
                case "steamid":
                    user.steamId = jsonReader.nextString();
                    break;
                case "communityvisibilitystate":
                    user.communityVisibilityState = jsonReader.nextInt();
                    break;
                case "profilestate":
                    user.profileState = jsonReader.nextInt();
                    break;
                case "personaname":
                    user.personaName = jsonReader.nextString();
                    break;
                case "lastlogoff":
                    user.lastLogoff = jsonReader.nextLong();
                    break;
                case "profileurl":
                    user.profileUrl = jsonReader.nextString();
                    break;
                case "avatar":
                    user.avatarUrl = jsonReader.nextString();
                    break;
                case "avatarmedium":
                    user.avatarMediumUrl= jsonReader.nextString();
                    break;
                case "avatarfull":
                    user.avatarFullUrl = jsonReader.nextString();
                    break;
                case "personastate":
                    user.personaState = jsonReader.nextInt();
                    break;
                case "realname":
                    user.realName = jsonReader.nextString();
                    break;
                case "primaryclanid":
                    user.primaryClanId = jsonReader.nextString();
                    break;
                case "timecreated":
                    user.timeCreated = jsonReader.nextLong();
                    break;
                case "personastateflags":
                    user.personaStateFlags = jsonReader.nextInt();
                    break;
                case "loccountrycode":
                    user.locCountryCode = jsonReader.nextString();
                    break;
                case "locstatecode":
                    user.locStateCode = jsonReader.nextString();
                    break;
                case "loccityid":
                    user.locCityId = jsonReader.nextString();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
                }
        }

        jsonReader.endObject();

        return user;
    }
}
