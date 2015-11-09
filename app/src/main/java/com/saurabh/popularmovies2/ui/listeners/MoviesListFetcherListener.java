/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.listeners;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * The interface used for returning the list of movies from the AsyncTask
 */
public interface MoviesListFetcherListener {

    /**
     * Called when the AsyncTask is finished executing
     */
    void onTaskCompleted(List<MovieDb> response);
}
