/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.data;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * An AsyncTask which queries themoviedb.org API for the list of movies
 * based on a particular sort order.
 */
public class MoviesListFetcher extends AsyncTask<String, Void, List<MovieDb>> {
    private static final String TAG = MoviesListFetcher.class.getSimpleName();

    private MoviesListFetcherListener moviesListFetcherListener;

    public MoviesListFetcher(MoviesListFetcherListener context) {
        this.moviesListFetcherListener = context;
    }

    /**
     * Makes a new TheMovieDB API object and queries for results
     *
     * @param params The criteria to sort with.
     * @return The list of MovieDb objects
     */
    @Override
    protected List<MovieDb> doInBackground(String... params) {
        try {
            Discover discover = new Discover();
            discover.page(1);
            discover.sortBy(params[0]);

            MovieResultsPage resultsPage = new TmdbApi(Keys.API_KEY).getDiscover().getDiscover(discover);

            return resultsPage.getResults();
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
    protected void onPostExecute(List<MovieDb> movies) {
        moviesListFetcherListener.onMovieListResponse(movies);
    }

    /**
     * The interface used for returning the list of movies from the AsyncTask
     */
    public interface MoviesListFetcherListener {

        /**
         * Called when the AsyncTask is finished executing
         */
        void onMovieListResponse(List<MovieDb> response);
    }
}
