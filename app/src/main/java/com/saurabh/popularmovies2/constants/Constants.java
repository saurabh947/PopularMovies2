/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.constants;

import android.net.Uri;

/**
 * App wide constants are declared here.
 */
public class Constants {
    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING = "vote_average.desc";

    public static final Uri THUMBNAIL_URL = Uri.parse("http://image.tmdb.org/t/p/w500");
    public static final Uri POSTER_URL = Uri.parse("http://image.tmdb.org/t/p/w780");
    public static final Uri YOUTUBE_URL = Uri.parse("https://www.youtube.com/watch?v=");
}
