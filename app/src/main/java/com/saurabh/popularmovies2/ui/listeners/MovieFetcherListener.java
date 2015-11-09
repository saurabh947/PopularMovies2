/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.listeners;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * The interface used for returning the the selected movie from the AsyncTask
 */
public interface MovieFetcherListener {

    /**
     * Called when the AsyncTask is finished executing
     */
    void onMovieResponseReceived(MovieDb response);
}
