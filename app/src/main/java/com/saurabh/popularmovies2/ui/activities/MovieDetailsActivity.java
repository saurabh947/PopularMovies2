/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.squareup.picasso.Picasso;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Displays all the details of the clicked grid item in a new Activity.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private ImageView mMoviePoster;
    private ImageView mMovieThumbnail;
    private TextView mMovieName;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private TextView mMovieSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initComponents();
        setMovieData();
    }

    /**
     * Initializes the views.
     */
    public void initComponents() {
        mActionBar = getSupportActionBar();
        mMoviePoster = (ImageView) findViewById(R.id.movie_poster);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieName = (TextView) findViewById(R.id.movie_name);
        mMovieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        mMovieRating = (TextView) findViewById(R.id.movie_rating);
        mMovieSynopsis = (TextView) findViewById(R.id.movie_synopsis_text);
    }

    /**
     * Sets the movie data to the views.
     */
    public void setMovieData() {
        MovieDb movie = (MovieDb) getIntent().getSerializableExtra("selectedMovie");

        if (movie != null) {
            mActionBar.setTitle(movie.getOriginalTitle());

            Picasso.with(this)
                    .load(Constants.POSTER_URL + movie.getPosterPath())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(mMoviePoster);
            Picasso.with(this)
                    .load(Constants.THUMBNAIL_URL + movie.getPosterPath())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(mMovieThumbnail);

            mMovieName.setText(movie.getOriginalTitle());
            if (movie.getReleaseDate().equals(getString(R.string.error_null))) {
                mMovieReleaseDate.setText(R.string.error_release_date);
            } else {
                mMovieReleaseDate.setText(movie.getReleaseDate());
            }

//            TmdbReviews.ReviewResultsPage reviews = new TmdbApi("<apikey>").getReviews().getReviews(movie.getId(), "en", 1);
//
//            android.util.Log.i("HEREEE", "=========" + reviews.getResults().get(0).getContent());

            mMovieRating.setText(String.valueOf(Float.valueOf(movie.getVoteAverage())));

            if (movie.getOverview().equals(getString(R.string.error_null))) {
                mMovieSynopsis.setText(R.string.error_overview);
            } else {
                mMovieSynopsis.setText(movie.getOverview());
            }
        }
    }
}
