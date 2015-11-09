/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.MovieFetcher;
import com.saurabh.popularmovies2.data.ReviewsFetcher;
import com.saurabh.popularmovies2.data.VideosFetcher;
import com.saurabh.popularmovies2.ui.listeners.MovieFetcherListener;
import com.saurabh.popularmovies2.ui.listeners.ReviewsFetcherListener;
import com.saurabh.popularmovies2.ui.listeners.VideosFetcherListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;

/**
 * Displays all the details of the clicked grid item in a new Activity.
 */
public class MovieDetailsActivity extends AppCompatActivity implements MovieFetcherListener,
        ReviewsFetcherListener, VideosFetcherListener {
    public static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private ActionBar mActionBar;

    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.movie_thumbnail) ImageView movieThumbnail;
    @Bind(R.id.movie_name) TextView movieName;
    @Bind(R.id.movie_release_date) TextView movieReleaseDate;
    @Bind(R.id.movie_rating) TextView movieRating;
    @Bind(R.id.movie_runtime) TextView movieRuntime;
    @Bind(R.id.movie_adult_icon) ImageView movieAdultIcon;
    @Bind(R.id.movie_synopsis_text) TextView movieSynopsis;
    @Bind(R.id.movie_trailer_progress) ProgressBar movieTrailerProgressBar;
    @Bind(R.id.movie_trailer_list) ListView movieTrailerList;
    @Bind(R.id.movie_reviews_progress) ProgressBar movieReviewsProgressBar;
    @Bind(R.id.movie_reviews_list) ListView movieReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mActionBar = getSupportActionBar();

        ButterKnife.bind(this);
        int movieId = getIntent().getIntExtra("selectedMovieId", 0);
        setMovieData(movieId);
    }

    /**
     * Sets the movie data to the views.
     */
    public void setMovieData(int movieId) {
        if (movieId != 0) {
            MovieFetcher movieFetcher = new MovieFetcher(this);
            movieFetcher.execute(movieId);

            ReviewsFetcher reviewsFetcher = new ReviewsFetcher(this);
            reviewsFetcher.execute(movieId);

            VideosFetcher videosFetcher = new VideosFetcher(this);
            videosFetcher.execute(movieId);
        } else {
            Toast.makeText(this, "Error retrieving the movie details!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMovieResponseReceived(MovieDb response) {
        if (response != null) {
            mActionBar.setTitle(response.getOriginalTitle());

            Picasso.with(this)
                    .load(Constants.POSTER_URL + response.getPosterPath())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(moviePoster);
            Picasso.with(this)
                    .load(Constants.THUMBNAIL_URL + response.getPosterPath())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(movieThumbnail);

            movieName.setText(response.getOriginalTitle());
            if (response.getReleaseDate() == null) {
                movieReleaseDate.setText(R.string.error_release_date);
            } else {
                movieReleaseDate.setText(response.getReleaseDate());
            }

            if (response.isAdult()) {
                movieAdultIcon.setVisibility(View.VISIBLE);
            }

            movieRating.setText(String.valueOf(response.getVoteAverage()));
            movieRuntime.setText(String.valueOf(response.getRuntime()) + " min");
            if (response.getOverview() == null) {
                movieSynopsis.setText(R.string.error_overview);
            } else {
                movieSynopsis.setText(response.getOverview());
            }
        }
    }

    @Override
    public void onVideoResponseReceived(List<Video> response) {
        movieTrailerProgressBar.setVisibility(View.INVISIBLE);

        ArrayList<String> trailerNames = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            trailerNames.add(response.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.movie_trailer_item, R.id.movie_trailer_name, trailerNames);
        movieTrailerList.setAdapter(adapter);
    }

    @Override
    public void onReviewResponseReceived(List<Reviews> response) {
        movieReviewsProgressBar.setVisibility(View.INVISIBLE);
    }
}
