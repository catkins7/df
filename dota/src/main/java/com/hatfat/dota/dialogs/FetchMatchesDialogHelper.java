package com.hatfat.dota.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaResult;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.MatchHistory;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.user.SteamUser;
import com.hatfat.dota.services.CharltonService;
import com.hatfat.dota.services.DotaRestAdapter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FetchMatchesDialogHelper {

    private enum FetchMatchesState {
        FETCH_MATCHES_STATE_STARTING,
        FETCH_MATCHES_STATE_FETCHING_MATCH_LIST,
        FETCH_MATCHES_STATE_FETCHING_DETAILS,
        FETCH_MATCHES_STATE_SAVING,
        FETCH_MATCHES_STATE_FINISHED,
    }

    private FetchMatchesState state;
    private float matchListProgress;
    private float matchDetailsProgress;
    private float saveProgress;

    private AlertDialog dialog;
    private TextView messageTextView;
    private FrameLayout percentFrameLayout;

    private CharltonService charltonService;
    private SteamUser user;

    private Set<Match> fetchResults;


    public FetchMatchesDialogHelper(SteamUser user) {
        this.user = user;

        state = FetchMatchesState.FETCH_MATCHES_STATE_STARTING;
        fetchResults = new HashSet<>();
    }

    public void showFromActivity(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View contentView = inflater.inflate(R.layout.dialog_fetch_matches, null);
        messageTextView = (TextView) contentView.findViewById(R.id.dialog_fetch_matches_text_view);
        percentFrameLayout = (FrameLayout) contentView.findViewById(R.id.dialog_fetch_matches_percent_frame_layout);

        builder.setView(contentView);
        builder.setCancelable(false);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        dialog = builder.create();
        dialog.setOwnerActivity(activity);
        dialog.show();

        charltonService = DotaRestAdapter.createRestAdapter().create(CharltonService.class);

        nextState();
    }

    private void nextState() {
        switch (state) {
            case FETCH_MATCHES_STATE_STARTING:
                startFetchingMatchList();
                break;
            case FETCH_MATCHES_STATE_FETCHING_MATCH_LIST:
                startFetchingMatchDetails();
                break;
            case FETCH_MATCHES_STATE_FETCHING_DETAILS:
                startSaving();
                break;
            case FETCH_MATCHES_STATE_SAVING:
                finished();
                break;
            default:

                break;
        }

        updateProgressBar();
    }

    private void startFetchingMatchList() {
        state = FetchMatchesState.FETCH_MATCHES_STATE_FETCHING_MATCH_LIST;

        charltonService.getMatchHistory(user.getAccountId(), new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
                finishedWithMatches(matchHistoryDotaResult.result);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("catfat", "1 handle errors??");
            }
        });

    }

    private void fetchMatchListFromMatchId(final String matchId) {
        Log.e("catfat", "fetching more matches!");
        charltonService.getMatchHistoryAtMatchId(user.getAccountId(), matchId, new Callback<DotaResult<MatchHistory>>() {
            @Override
            public void success(DotaResult<MatchHistory> matchHistoryDotaResult, Response response) {
                finishedWithMatches(matchHistoryDotaResult.result);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("catfat", "2 handle errors??");
            }
        });

    }

    private void finishedWithMatches(MatchHistory matchHistory) {
        fetchResults.addAll(matchHistory.getMatches());

        if (matchHistory.getResultsRemaining() > 0) {
            fetchMatchListFromMatchId(matchHistory.getMatches().get(matchHistory.getMatches().size() - 1).getMatchId());
        }
        else {
            matchListProgress = 0.5f;
            updateProgressBar();

            //no more matches to fetch!  we are done here...
            LinkedList<Match> matches = new LinkedList<>(fetchResults);

            //add the matches to the user object!
            user.addMatches(matches);

            //add the match search results to the matches singleton
            Matches.get().addMatches(matches);

            matchListProgress = 1.0f;
            nextState();
        }
    }

    private void startFetchingMatchDetails() {
        state = FetchMatchesState.FETCH_MATCHES_STATE_FETCHING_DETAILS;

        matchDetailsProgress = 1.0f;

        nextState();
    }

    private void startSaving() {
        state = FetchMatchesState.FETCH_MATCHES_STATE_SAVING;

        saveProgress = 1.0f;

        nextState();
    }

    private void finished() {
        state = FetchMatchesState.FETCH_MATCHES_STATE_FINISHED;

        dialog.dismiss();
    }

    private void updateProgressBar() {
        Activity activity = dialog.getOwnerActivity();

        if (activity == null) {
            return;
        }

        float barPercent = 0.0f;
        barPercent += matchListProgress * 0.1f;
        barPercent += matchDetailsProgress * 0.8f;
        barPercent += saveProgress * 0.1f;

        float loadingBarMaxWidth = (float) messageTextView.getWidth();
        final int barWidth = (int) (loadingBarMaxWidth * barPercent);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageTextView.setText(getMessageText());

                percentFrameLayout.getLayoutParams().width = barWidth;
                percentFrameLayout.requestLayout();
            }
        });
    }

    private String getMessageText() {
        Resources resources = dialog.getOwnerActivity().getResources();

        switch (state) {
            case FETCH_MATCHES_STATE_FETCHING_MATCH_LIST:
                return resources.getString(R.string.player_summary_fetch_all_dialog_fetching_match_list_text);
            case FETCH_MATCHES_STATE_FETCHING_DETAILS:
                String messageString = resources.getString(R.string.player_summary_fetch_all_dialog_fetching_match_details_text);

                return messageString;
            case FETCH_MATCHES_STATE_SAVING:
                return resources.getString(R.string.player_summary_fetch_all_dialog_saving_matches_text);
            default:
                return null;
        }
    }
}
