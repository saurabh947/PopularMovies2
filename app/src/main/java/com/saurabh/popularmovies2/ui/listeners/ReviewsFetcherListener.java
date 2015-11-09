/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.listeners;

import java.util.List;

import info.movito.themoviedbapi.model.Reviews;

/**
 * The interface used for returning the the selected movie from the AsyncTask
 */
public interface ReviewsFetcherListener {

    /**
     * Called when the AsyncTask is finished executing
     */
    void onReviewResponseReceived(List<Reviews> response);
}
