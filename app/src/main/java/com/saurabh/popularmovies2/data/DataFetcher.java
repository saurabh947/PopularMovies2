/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.data;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;
import com.saurabh.popularmovies2.ui.helpers.DataFetcherListener;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * An AsyncTask which queries themoviedb.org API for the list of movies
 * based on a particular sort order.
 */
public class DataFetcher extends AsyncTask<String, Void, List<MovieDb>> {
    private static final String TAG = DataFetcher.class.getSimpleName();
    private DataFetcherListener dataFetcherListener;

    public DataFetcher(DataFetcherListener context) {
        this.dataFetcherListener = context;
    }

    @Override
    protected List<MovieDb> doInBackground(String... params) {
        try {
            return getData(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fires the DataFetcherListener and sets the result MovieDb objects.
     */
    @Override
    protected void onPostExecute(List<MovieDb> movies) {
        try {
            dataFetcherListener.onTaskCompleted(movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a new TheMovieDB API object and queries for results
     *
     * @param sortCriteria The criteria to sort.
     * @return The list of MovieDb objects
     */
    private List<MovieDb> getData(String sortCriteria) {
        try {
            Discover discover = new Discover();
            discover.page(1);
            discover.sortBy(sortCriteria);

            MovieResultsPage movies = new TmdbApi(Keys.API_KEY).getDiscover().getDiscover(discover);

            return movies.getResults();
        } catch (Exception e) {
            Log.e(TAG, "Error: Could not connect to remote server.");
            e.printStackTrace();
        }
        return null;
    }
}
