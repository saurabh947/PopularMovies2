package com.saurabh.popularmovies2.data.network;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.Reviews;

/**
 * An AsyncTask which queries themoviedb.org API for the list of reviews
 * for a particular movie.
 * <p/>
 * Needs movie id as input.
 */
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
        mReviewsFetcherListener.onReviewResponse(reviews);
    }

    /**
     * The interface used for returning the the selected movie from the AsyncTask
     */
    public interface ReviewsFetcherListener {
        /**
         * Called when the AsyncTask is finished executing
         */
        void onReviewResponse(List<Reviews> response);
    }
}
