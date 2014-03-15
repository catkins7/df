package com.hatfat.dota.services.dotabuff;

import com.hatfat.dota.model.dotabuff.DotaBuffSearchResult;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by scottrick on 3/15/14.
 */
public interface DotaBuffService {

    @GET("/search/hints.json")
    public void searchForPlayerName(@Query("q") String playerName, Callback<List<DotaBuffSearchResult>> searchResult);
}
