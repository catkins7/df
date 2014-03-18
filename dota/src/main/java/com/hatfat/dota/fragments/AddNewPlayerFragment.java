package com.hatfat.dota.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.hatfat.dota.R;
import com.hatfat.dota.model.dotabuff.DotaBuffSearchResult;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.services.dotabuff.DotaBuffRestAdapter;
import com.hatfat.dota.services.dotabuff.DotaBuffService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 2/12/14.
 */
public class AddNewPlayerFragment extends CharltonFragment {

    private HashSet<SteamUser> playerSearchResults;

    private int totalNumberOfSearches;
    private int numberOfSearchesComplete;

    private View notSearchingContainerView;
    private View searchingContainerView;
    private EditText searchEditText;

    private String searchString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_player, null);

        notSearchingContainerView = view.findViewById(R.id.fragment_add_new_player_not_searching_bottom_view);
        searchingContainerView = view.findViewById(R.id.fragment_add_new_player_searching_bottom_view);

        searchEditText = (EditText)view.findViewById(R.id.fragment_add_new_player_search_text_view);
        final Button searchButton = (Button)view.findViewById(R.id.fragment_add_new_player_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                searchForString(searchEditText.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isSearching()) {
            showSearching();
        }
        else {
            hideSearching();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        hideKeyboard();
    }

    private void showSearching() {
        searchingContainerView.setVisibility(View.VISIBLE);
        notSearchingContainerView.setVisibility(View.GONE);
    }

    private void hideSearching() {
        searchingContainerView.setVisibility(View.GONE);
        notSearchingContainerView.setVisibility(View.VISIBLE);
    }

    private void searchForString(final String searchString) {
        if (searchString == null || searchString.length() <= 0) {
            return;
        }

        playerSearchResults = new HashSet<>();
        totalNumberOfSearches = 2;
        numberOfSearchesComplete = 0;
        this.searchString = searchString;

        showSearching();

        doDotaBuffSearch(searchString);
        doAccountSearch(searchString);
    }

    private boolean isSearching() {
        return numberOfSearchesComplete < totalNumberOfSearches;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void doDotaBuffSearch(final String searchString) {
        DotaBuffService dotaBuffService = DotaBuffRestAdapter.createRestAdapter().create(DotaBuffService.class);
        dotaBuffService.searchForPlayerName(searchString, new Callback<List<DotaBuffSearchResult>>() {
            @Override
            public void success(List<DotaBuffSearchResult> dotaBuffSearchResults, Response response) {
                LinkedList<SteamUser> users = new LinkedList<>();

                for (DotaBuffSearchResult result : dotaBuffSearchResults) {
                    if (result.isPlayer()) {
                        SteamUser user = SteamUsers.get().getByAccountId(result.getAccountId());
                        users.add(user);
                    }
                }

                searchFinishedWithResults(users);
            }

            @Override
            public void failure(RetrofitError error) {
                searchFinishedWithResults(null);
            }
        });
    }

    private void doAccountSearch(final String searchString) {
        searchFinishedWithResults(null);
    }

    private void searchFinishedWithResults(List<SteamUser> users) {
        if (users != null) {
            playerSearchResults.addAll(users);
        }

        numberOfSearchesComplete++;

        if (numberOfSearchesComplete >= totalNumberOfSearches) {
            //all searches are finished
            if (getCharltonActivity() != null) {
                LinkedList<SteamUser> usersList = new LinkedList<>(playerSearchResults);
                SteamUserListFragment listFragment = SteamUserListFragment.newInstance(usersList, getSearchString());
                getCharltonActivity().pushCharltonFragment(listFragment);
            }
        }
    }

    private String getSearchString() {
        return "Here are the results for the search '" + searchString + "'";
    }

//    private void doPlayerSearch(final String playerName) {
//        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
//        charltonService.getMatchHistoryByPlayerName(playerName, new Callback<DotaResult<MatchHistory>>() {
//            @Override
//            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
//                finishedSearchWithMatches(matchHistoryDotaResult.result, playerName);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
//    }
//
//    private void doPlayerSearchFromMatchId(final String playerName, final String matchId) {
//        if (matchId == null) {
//            doPlayerSearch(playerName);
//            return;
//        }
//
//        CharltonService charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);
//        charltonService.getMatchHistoryByPlayerNameAtMatchId(playerName, matchId, new Callback<DotaResult<MatchHistory>>() {
//            @Override
//            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
//                finishedSearchWithMatches(matchHistoryDotaResult.result, playerName);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
//    }
//
//    private void finishedSearchWithMatches(MatchHistory matchHistory, String playerName) {
//        matchSearchResults.addAll(matchHistory.getMatches());
//
//        if (matchHistory.getResultsRemaining() > 0) {
//            doPlayerSearchFromMatchId(playerName, matchHistory.getMatches().get(matchHistory.getMatches().size() - 1).getMatchId());
//        }
//        else {
//            //no more matches to fetch!  we are done here...
//            LinkedList<Match> matches = new LinkedList<>(matchSearchResults);
//
//            //add the match search results to the matches singleton
//            Matches.get().addMatches(matches);
//
//            MatchListFragment matchListFragment = MatchListFragment.newInstance(matches);
//            getCharltonActivity().pushCharltonFragment(matchListFragment);
//        }
//    }

    @Override
    public String getCharltonText() {
        return "Let me help you find a player.  Just enter their name or account ID.";
    }
}
