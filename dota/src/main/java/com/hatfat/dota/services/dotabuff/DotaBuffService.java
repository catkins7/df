package com.hatfat.dota.services.dotabuff;

import com.hatfat.dota.model.dotabuff.DotaBuffHackSearchResult;
import com.hatfat.dota.model.dotabuff.DotaBuffSearchResult;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DotaBuffService {

    @GET("/search/hints.json")
    public void searchForPlayerName(@Query("q") String playerName, Callback<List<DotaBuffSearchResult>> searchResult);

    @GET("/search")
    public void searchForPlayerHack(@Query("q") String playerName, Callback<DotaBuffHackSearchResult> searchResult);
}
