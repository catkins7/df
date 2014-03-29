package com.hatfat.dota.services;

import com.hatfat.dota.model.DotaResponse;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.game.HeroData;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.player.PlayerSummaries;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by scottrick on 2/10/14.
 */
public interface CharltonService {

    final static String STEAM_DEV_KEY = "B3A7049A360B80408EEC3A9D97153AAF";

    @GET("/IEconDOTA2_570/GetHeroes/v0001/?key=" + STEAM_DEV_KEY)
    public void getHeroes(@Query("language") String language, Callback<DotaResult<HeroData>> heroDataCallback);

    @GET("/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_DEV_KEY)
    public void getPlayerSummaries(@Query("steamIds") String steamIdsFormattedString, Callback<DotaResponse<PlayerSummaries>> steamUserCallback);

    @GET("/IDOTA2Match_570/GetMatchHistory/V001/?key=" + STEAM_DEV_KEY)
    public void getMatchHistory(@Query("account_id") String accountId, Callback<DotaResult<MatchHistory>> matchHistoryCallback);

    @GET("/IDOTA2Match_570/GetMatchHistory/V001/?key=" + STEAM_DEV_KEY)
    public void getMatchHistoryAtMatchId(@Query("account_id") String accountId, @Query("start_at_match_id") String startMatchId, Callback<DotaResult<MatchHistory>> matchHistoryCallback);

    @GET("/IDOTA2Match_570/GetMatchHistory/V001/?key=" + STEAM_DEV_KEY)
    public void getMatchHistoryByPlayerName(@Query("player_name") String playerName, Callback<DotaResult<MatchHistory>> matchHistoryCallback);

    @GET("/IDOTA2Match_570/GetMatchHistory/V001/?key=" + STEAM_DEV_KEY)
    public void getMatchHistoryByPlayerNameAtMatchId(@Query("player_name") String playerName, @Query("start_at_match_id") String startMatchId, Callback<DotaResult<MatchHistory>> matchHistoryCallback);

    @GET("/IDOTA2Match_570/GetMatchDetails/V001/?key=" + STEAM_DEV_KEY)
    public void getMatchDetails(@Query("match_id") String matchId, Callback<DotaResult<Match>> matchHistoryCallback);
}
