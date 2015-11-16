/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.network.MovieFetcher;
import com.saurabh.popularmovies2.data.network.ReviewsFetcher;
import com.saurabh.popularmovies2.data.network.VideosFetcher;
import com.saurabh.popularmovies2.data.provider.ProviderDbHelper;
import com.saurabh.popularmovies2.ui.adapters.MovieReviewsAdapter;
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
public class MovieDetailsActivity extends AppCompatActivity implements MovieFetcher.MovieFetcherListener,
        ReviewsFetcher.ReviewsFetcherListener, VideosFetcher.VideosFetcherListener {
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.movie_thumbnail) ImageView movieThumbnail;
    @Bind(R.id.movie_name) TextView movieName;
    @Bind(R.id.movie_favourite_icon)
    ImageButton movieFavorite;
    @Bind(R.id.movie_release_date) TextView movieReleaseDate;
    @Bind(R.id.movie_rating) TextView movieRating;
    @Bind(R.id.movie_runtime) TextView movieRuntime;
    @Bind(R.id.movie_adult_icon) ImageView movieAdultIcon;
    @Bind(R.id.movie_synopsis_text) TextView movieSynopsis;
    @Bind(R.id.movie_trailer_progress) ProgressBar movieTrailerProgressBar;
    @Bind(R.id.movie_trailer_list) ListView movieTrailerList;
    @Bind(R.id.movie_reviews_progress) ProgressBar movieReviewsProgressBar;
    @Bind(R.id.movie_reviews_list)
    RecyclerView movieReviewsList;

    private ActionBar mActionBar;
    private int mMovieId;
    private boolean mIsMovieAFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        mMovieId = getIntent().getIntExtra("selectedMovieId", 0);
        setMovieData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("movieId", mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMovieId = savedInstanceState.getInt("movieId");
        setMovieData();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the movie data to the views.
     */
    private void setMovieData() {
        if (mMovieId != 0) {
            MovieFetcher movieFetcher = new MovieFetcher(this);
            movieFetcher.execute(mMovieId);

        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    // region Listener Implementations

    @Override
    public void onMovieResponse(final MovieDb response) {
        if (response != null) {
            mActionBar.setTitle(response.getOriginalTitle());

            Picasso.with(this)
                    .load(Uri.withAppendedPath(Constants.POSTER_URL, response.getPosterPath()))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(moviePoster);
            Picasso.with(this)
                    .load(Uri.withAppendedPath(Constants.THUMBNAIL_URL, response.getPosterPath()))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fit().centerCrop()
                    .into(movieThumbnail);

            movieName.setText(response.getOriginalTitle());

            getMovieFromDb(response);
            movieFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isSelected()) {
                        view.setSelected(false);
                        mIsMovieAFavorite = false;
                    } else {
                        view.setSelected(true);
                        mIsMovieAFavorite = true;
                    }
                    updateMovieInDb();
                }
            });

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
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }

        VideosFetcher videosFetcher = new VideosFetcher(this);
        videosFetcher.execute(mMovieId);
    }

    @Override
    public void onVideoResponse(final List<Video> response) {
        movieTrailerProgressBar.setVisibility(View.INVISIBLE);

        if (response != null) {
            ArrayList<String> trailerNames = new ArrayList<>();
            for (int i = 0; i < response.size(); i++) {
                trailerNames.add(response.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.movie_trailer_item, R.id.movie_trailer_name, trailerNames);
            movieTrailerList.setAdapter(adapter);

            movieTrailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Uri uri = Uri.withAppendedPath(Constants.YOUTUBE_URL, response.get(position).getKey());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    List activities = getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (activities.size() > 0) {
                        startActivity(intent);
                    }
                }
            });

        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }

        ReviewsFetcher reviewsFetcher = new ReviewsFetcher(this);
        reviewsFetcher.execute(mMovieId);
    }

    @Override
    public void onReviewResponse(List<Reviews> response) {
        movieReviewsProgressBar.setVisibility(View.INVISIBLE);

        if (response != null) {
            MovieReviewsAdapter adapter = new MovieReviewsAdapter(this, response);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            movieReviewsList.setLayoutManager(layoutManager);
            movieReviewsList.setHasFixedSize(true);

            movieReviewsList.getLayoutParams().height = 240 * response.size();

            movieReviewsList.setAdapter(adapter);
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }
    }
    // endregion

    // region DB operations

    private void getMovieFromDb(MovieDb movie) {
        String selection = "movie_id = ?";
        String[] selectionArgs = {String.valueOf(mMovieId)};

        Cursor cursor = getContentResolver()
                .query(ProviderDbHelper.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
        if (cursor != null && cursor.getCount() == 0) {
            insertMovieInDb(movie);
            cursor.close();

        } else if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            boolean selected = Boolean.parseBoolean(cursor.getString(
                    cursor.getColumnIndex(ProviderDbHelper.COLUMN_IS_FAVORITE)));
            mIsMovieAFavorite = selected;
            movieFavorite.setSelected(selected);
            cursor.close();
        }
    }

    private void updateMovieInDb() {
        String selection = "movie_id = ?";
        String[] selectionArgs = {String.valueOf(mMovieId)};

        ContentValues values = new ContentValues();
        values.put(ProviderDbHelper.COLUMN_IS_FAVORITE, String.valueOf(mIsMovieAFavorite));

        getContentResolver().update(ProviderDbHelper.CONTENT_URI, values, selection, selectionArgs);
    }

    private void insertMovieInDb(MovieDb movie) {
        ContentValues values = new ContentValues();
        values.put(ProviderDbHelper.COLUMN_MOVIE_ID, movie.getId());
        values.put(ProviderDbHelper.COLUMN_MOVIE_NAME, movie.getOriginalTitle());

        getContentResolver().insert(ProviderDbHelper.CONTENT_URI, values);
    }
    // endregion
}
