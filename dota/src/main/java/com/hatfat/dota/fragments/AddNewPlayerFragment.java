package com.hatfat.dota.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.SearchResultsActivity;
import com.hatfat.dota.model.dotabuff.DotaBuffHackSearchResult;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.model.user.SteamUsers;
import com.hatfat.dota.services.dotabuff.DotaBuffRestAdapter;
import com.hatfat.dota.services.dotabuff.DotaBuffService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddNewPlayerFragment extends CharltonFragment {

    private int totalNumberOfSearches;
    private int numberOfSearchesComplete;

    private View notSearchingContainerView;
    private View searchingContainerView;
    private EditText searchEditText;

    private String searchString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_player, container, false);

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

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForString(searchEditText.getText().toString());

                    //return false so the default keyboard "done pressed" handling will happen (ie, hide the keyboard)
                    return false;
                }

                return false;
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
            searchEditText.requestFocus();

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
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

        dotaBuffService.searchForPlayerHack(searchString, new Callback<DotaBuffHackSearchResult>() {
            @Override
            public void success(DotaBuffHackSearchResult dotaBuffHackSearchResults,
                    Response response) {
                LinkedList<SteamUser> users = new LinkedList<>();

                for (String accountId : dotaBuffHackSearchResults.accountIds) {
                    SteamUser user = SteamUsers.get().getByAccountId(accountId);
                    users.add(user);
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
        numberOfSearchesComplete++;

        if (numberOfSearchesComplete >= totalNumberOfSearches) {
            //all searches are finished
            ArrayList<String> userIds = new ArrayList();

            if (users != null) {
                for (SteamUser user : users) {
                    userIds.add(user.getSteamId());
                }
            }

            if (getActivity() != null) {
                Intent resultsIntent = SearchResultsActivity
                        .intentForResultsWithMessage(getActivity().getApplicationContext(), userIds,
                                getSearchString());
                startActivity(resultsIntent);
            }
        }
    }

    private String getSearchString() {
        return String.format(getResources().getString(R.string.add_new_player_search_results_text), searchString);
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
    public String getCharltonMessageText(Context context) {
        return context.getResources().getString(R.string.add_new_player_charlton_text);
    }
}
