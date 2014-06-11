package com.hatfat.dota.services.dotabuff;

import com.hatfat.dota.model.dotabuff.DotaBuffHackSearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.LinkedList;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

//Hack converter to convert the web search results page into a list of account ids
public class DotaBuffConverter implements Converter {

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        DotaBuffHackSearchResult hackResult = new DotaBuffHackSearchResult();
        LinkedList<String> accountIds = new LinkedList();

        try {
            String charset = "UTF-8";
            if (body.mimeType() != null) {
                charset = MimeUtil.parseCharset(body.mimeType());
            }

            InputStream is = body.in();

            InputStreamReader reader = new InputStreamReader(is, charset);
            String line;
            int start;
            int idStart = "/players/".length();
            int end;

            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {

                while ((start = line.indexOf("/players/")) != -1) {

                    end = start + idStart + 16;
                    String substring = line.substring(start + idStart, start + 32);

                    int endOfNumber = substring.indexOf("\"");

                    if (endOfNumber > 0) {
                        substring = substring.substring(0, endOfNumber);
                    }

                    String steamId = null;

                    try {
                        steamId = String.valueOf(Long.valueOf(substring));
                    }
                    catch (NumberFormatException e) {

                    }

                    if (steamId != null && !accountIds.contains(steamId)) {
                        accountIds.add(steamId);
                    }

                    line = line.substring(end);
                }
            }

            reader.close();
        }
        catch (IOException e) {

        }

        hackResult.accountIds = accountIds;

        return hackResult;
    }

    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }
}
