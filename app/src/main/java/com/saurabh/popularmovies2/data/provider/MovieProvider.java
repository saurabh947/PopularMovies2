package com.saurabh.popularmovies2.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieSqlDb mMovieSqlDb;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProviderDbHelper.CONTENT_AUTHORITY;

        matcher.addURI(authority, ProviderDbHelper.TABLE_MOVIE, ProviderDbHelper.CODE_MOVIE);
        matcher.addURI(authority, ProviderDbHelper.TABLE_MOVIE + "/#", ProviderDbHelper.CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieSqlDb = new MovieSqlDb(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case ProviderDbHelper.CODE_MOVIE:
                return ProviderDbHelper.CONTENT_DIR_TYPE;

            case ProviderDbHelper.CODE_MOVIE_WITH_ID:
                return ProviderDbHelper.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mMovieSqlDb.getReadableDatabase().query(
                ProviderDbHelper.TABLE_MOVIE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieSqlDb.getWritableDatabase();
        Uri returnUri;

        long _id = db.insert(ProviderDbHelper.TABLE_MOVIE, null, values);

        if (_id > 0) {
            returnUri = ProviderDbHelper.buildMoviesUri(_id);
        } else {
            throw new android.database.SQLException("Failed to insert row into: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieSqlDb.getWritableDatabase();
        int numUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        numUpdated = db.update(ProviderDbHelper.TABLE_MOVIE, values, selection, selectionArgs);

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieSqlDb.getWritableDatabase();
        int numDeleted;

        numDeleted = db.delete(ProviderDbHelper.TABLE_MOVIE, selection, selectionArgs);
        return numDeleted;
    }
}
