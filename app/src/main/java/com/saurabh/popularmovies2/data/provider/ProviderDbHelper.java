package com.saurabh.popularmovies2.data.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

public class ProviderDbHelper {

    // region DB & Table Constants
    public static final String DB_NAME = "movies.db";
    public static final int DB_VERSION = 3;
    public static final String TABLE_MOVIE = "movie";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_NAME = "movie_name";
    public static final String COLUMN_IS_FAVORITE = "is_favorite";

    // endregion

    // region Content Provider Constants
    public static final String CONTENT_AUTHORITY = "com.saurabh.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_MOVIE);

    // create cursor of base type directory for multiple entries
    public static final String CONTENT_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;

    // create cursor of base type item for single entry
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;

    // Codes for the UriMatcher
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 200;

    // endregion

    // for building URIs on insertion
    public static Uri buildMoviesUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
