package com.saurabh.popularmovies2.data;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;
import com.saurabh.popularmovies2.ui.listeners.ReviewsFetcherListener;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.Reviews;

public class ReviewsFetcher extends AsyncTask<Integer, Void, List<Reviews>> {
    public static final String TAG = ReviewsFetcher.class.getSimpleName();

    private ReviewsFetcherListener mReviewsFetcherListener;

    public ReviewsFetcher(ReviewsFetcherListener context) {
        this.mReviewsFetcherListener = context;
    }

    @Override
    protected List<Reviews> doInBackground(Integer... params) {
        try {
            TmdbReviews.ReviewResultsPage reviews = new TmdbApi(Keys.API_KEY)
                    .getReviews()
                    .getReviews(params[0], "en", 1);
            return reviews.getResults();
        } catch (Exception e) {
            Log.e(TAG, "Error: Could not connect to remote server.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fires the interface method and sets the result.
     */
    @Override
    protected void onPostExecute(List<Reviews> reviews) {
        mReviewsFetcherListener.onReviewResponseReceived(reviews);
    }
}