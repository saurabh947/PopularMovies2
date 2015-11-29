/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.models;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.saurabh.popularmovies2.data.provider.ProviderSqlHelper;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * The model class for easy handling of stored favorite movies
 * in the database.
 */
public class Movie {
    private Bitmap mPoster;
    private Bitmap mThumbnail;
    private int mId;
    private String mName;
    private String mReleaseDate;
    private float mRating;
    private int mRuntime;
    private String mOverview;
    private boolean mIsFavorite;

    public void setMovieFromDb(Cursor cursor) {
        setId(cursor.getInt(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_MOVIE_ID)));
        setName(cursor.getString(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_NAME)));
        setReleaseDate(cursor.getString(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_RELEASE_DATE)));
        setRuntime(cursor.getInt(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_RUNTIME)));
        setRating(cursor.getFloat(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_RATING)));
        setOverview(cursor.getString(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_OVERVIEW)));
        setFavorite(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_IS_FAVORITE))));

        setImages();
    }

    public void setMovieFromNetwork(MovieDb movieDb) {

    }

    public void setImages() {

    }

    public Bitmap getPoster() {
        return mPoster;
    }

    public void setPoster(Bitmap poster) {
        mPoster = poster;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        mThumbnail = thumbnail;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
    }
}
