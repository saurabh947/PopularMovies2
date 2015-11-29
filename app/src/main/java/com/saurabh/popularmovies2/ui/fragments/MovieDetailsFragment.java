/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.saurabh.popularmovies2.data.provider.ProviderSqlHelper;
import com.saurabh.popularmovies2.ui.adapters.MovieReviewsAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;

/**
 * Displays all the details of the clicked grid item (Movie) in a new Activity.
 */
public class MovieDetailsFragment extends Fragment implements MovieFetcher.MovieFetcherListener,
        ReviewsFetcher.ReviewsFetcherListener, VideosFetcher.VideosFetcherListener {
    public static final String TAG = MovieDetailsFragment.class.getSimpleName();

    @Bind(R.id.movie_poster) ImageView moviePoster;
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
    /**
     * Fires the Palette class after loading the image from
     * the network.
     */
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            moviePoster.setImageBitmap(bitmap);

            /**
             * Sets the actionbar and status bar colors by getting the muted colors from
             * the movie poster.
             * If not found, sets them to default colors.
             */
            Palette palette = Palette.from(bitmap).generate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(palette.getDarkMutedColor(
                        getResources().getColor(R.color.colorPrimaryDark)));
                mActionBar.setBackgroundDrawable(new ColorDrawable(palette.getMutedColor(
                        getResources().getColor(R.color.colorPrimary))));
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            moviePoster.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    private int mMovieId;
    private boolean mIsMovieAFavorite;

    public MovieDetailsFragment() {
        setRetainInstance(true);
    }

    public static MovieDetailsFragment newInstance(int movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("selectedMovieId", movieId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ButterKnife.bind(this, view);

        if (savedInstanceState != null && savedInstanceState.getInt("selectedMovieId") != 0) {
            mMovieId = savedInstanceState.getInt("selectedMovieId");
            setMovieData();
        } else {
            mMovieId = getArguments().getInt("selectedMovieId", 0);
            setMovieData();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("movieId", mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
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

        }
    }

    // region Listener Implementations

    /**
     * Called when the app receives all the movie information and displays it.
     *
     * @param response The {@link MovieDb} object
     */
    @Override
    public void onMovieResponse(final MovieDb response) {
        if (response != null) {
            mActionBar.setTitle(response.getOriginalTitle());

            Picasso.with(getContext())
                    .load(Uri.withAppendedPath(Constants.POSTER_URL, response.getBackdropPath()))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(target);

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

            movieAdultIcon.setVisibility(response.isAdult() ? View.VISIBLE : View.INVISIBLE);

            movieRating.setText(String.valueOf(response.getVoteAverage()));
            movieRuntime.setText(String.valueOf(response.getRuntime()) + " min");
            if (response.getOverview() == null) {
                movieSynopsis.setText(R.string.error_overview);
            } else {
                movieSynopsis.setText(response.getOverview());
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
        }

        VideosFetcher videosFetcher = new VideosFetcher(this);
        videosFetcher.execute(mMovieId);
    }

    /**
     * Called when the app receives all the movie videos and displays it.
     *
     * @param response The {@link Video} object
     */
    @Override
    public void onVideoResponse(final List<Video> response) {
        movieTrailerProgressBar.setVisibility(View.INVISIBLE);

        if (response != null) {
            ArrayList<String> trailerNames = new ArrayList<>();
            for (int i = 0; i < response.size(); i++) {
                trailerNames.add(response.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    R.layout.movie_trailer_item, R.id.movie_trailer_name, trailerNames);
            movieTrailerList.setAdapter(adapter);

            movieTrailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Uri uri = Uri.withAppendedPath(Constants.YOUTUBE_URL, response.get(position).getKey());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    List activities = getContext().getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (activities.size() > 0) {
                        startActivity(intent);
                    }
                }
            });

        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
        }

        ReviewsFetcher reviewsFetcher = new ReviewsFetcher(this);
        reviewsFetcher.execute(mMovieId);
    }

    /**
     * Called when the app receives all the movie reviews and displays it.
     *
     * @param response The {@link Reviews} object
     */
    @Override
    public void onReviewResponse(List<Reviews> response) {
        movieReviewsProgressBar.setVisibility(View.INVISIBLE);

        if (response != null) {
            MovieReviewsAdapter adapter = new MovieReviewsAdapter(getContext(), response);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            movieReviewsList.setLayoutManager(layoutManager);
            movieReviewsList.setHasFixedSize(true);

            movieReviewsList.getLayoutParams().height = 460 * response.size();

            movieReviewsList.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    // endregion

    // region DB operations

    /**
     * Get the movie record from the database; insert it if not present.
     *
     * @param movie The {@link MovieDb} object.
     */
    private void getMovieFromDb(MovieDb movie) {
        String selection = "movie_id = ?";
        String[] selectionArgs = {String.valueOf(mMovieId)};

        Cursor cursor = getContext().getContentResolver()
                .query(ProviderSqlHelper.CONTENT_URI,
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
                    cursor.getColumnIndex(ProviderSqlHelper.COLUMN_IS_FAVORITE)));
            mIsMovieAFavorite = selected;
            movieFavorite.setSelected(selected);
            cursor.close();
        }
    }

    /**
     * Update the movie record in the database.
     */
    private void updateMovieInDb() {
        String selection = "movie_id = ?";
        String[] selectionArgs = {String.valueOf(mMovieId)};

        ContentValues values = new ContentValues();
        values.put(ProviderSqlHelper.COLUMN_IS_FAVORITE, String.valueOf(mIsMovieAFavorite));

        getContext().getContentResolver().update(ProviderSqlHelper.CONTENT_URI, values, selection, selectionArgs);
    }

    /**
     * Insert the movie record in the database.
     *
     * @param movie The {@link MovieDb} object.
     */
    private void insertMovieInDb(MovieDb movie) {
        ContentValues values = new ContentValues();
        values.put(ProviderSqlHelper.COLUMN_MOVIE_ID, movie.getId());
        values.put(ProviderSqlHelper.COLUMN_NAME, movie.getOriginalTitle());
        values.put(ProviderSqlHelper.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(ProviderSqlHelper.COLUMN_RATING, movie.getVoteAverage());
        values.put(ProviderSqlHelper.COLUMN_RUNTIME, movie.getRuntime());
        values.put(ProviderSqlHelper.COLUMN_OVERVIEW, movie.getOverview());

        getContext().getContentResolver().insert(ProviderSqlHelper.CONTENT_URI, values);
    }

    // endregion
}
