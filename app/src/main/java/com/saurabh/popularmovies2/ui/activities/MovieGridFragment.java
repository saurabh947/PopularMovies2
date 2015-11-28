/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.network.MovieFetcher;
import com.saurabh.popularmovies2.data.network.MoviesListFetcher;
import com.saurabh.popularmovies2.data.provider.ProviderSqlHelper;
import com.saurabh.popularmovies2.ui.adapters.MoviesGridAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Showed when the app is launched, this fragment displays the movie posters in a GridView.
 * <p/>
 * The menu overflow icon can be used to change the sort order of the movies.
 */
public class MovieGridFragment extends Fragment implements MoviesListFetcher.MoviesListFetcherListener,
        MovieFetcher.MovieFetcherListener {
    public static final String TAG = MovieGridFragment.class.getSimpleName();

    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.movie_grid)
    RecyclerView movieGrid;

    private int counter = 0;
    private List<MovieDb> mFavoriteMovies = new ArrayList<>();
    private int mFavoriteMoviesCount;
    private MovieGridFragmentCallbacks mCallbacks;

    public static MovieGridFragment newInstance(MovieGridFragmentCallbacks callbacks) {
        MovieGridFragment fragment = new MovieGridFragment();
        fragment.mCallbacks = callbacks;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);

        populateGridWithMovies(Constants.SORT_POPULARITY);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                populateGridWithMovies(Constants.SORT_POPULARITY);
                return true;

            case R.id.menu_sort_rating:
                populateGridWithMovies(Constants.SORT_RATING);
                return true;

            case R.id.menu_favorite:
                populateGridWithFavorites(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fires the AsyncTask to get the list of movies based on sort criteria.
     *
     * @param sortCriteria The sort criteria
     */
    private void populateGridWithMovies(String sortCriteria) {
        MoviesListFetcher moviesListFetcher = new MoviesListFetcher(this);
        moviesListFetcher.execute(sortCriteria);
    }

    /**
     * Populates the grid with the user's favorite movies.
     * Shows an error toast message of there are none.
     *
     * @param showFavorites the boolean flag.
     */
    private void populateGridWithFavorites(boolean showFavorites) {
        if (showFavorites) {
            progressBar.setVisibility(View.VISIBLE);

            String selection = "is_favorite = ?";
            String[] selectionArgs = {"true"};

            Cursor cursor = getContext().getContentResolver()
                    .query(ProviderSqlHelper.CONTENT_URI,
                            null,
                            selection,
                            selectionArgs,
                            null);
            if (cursor != null && cursor.getCount() == 0) {
                Toast.makeText(getContext(), R.string.error_no_favorite_movies, Toast.LENGTH_LONG).show();
                cursor.close();

            } else if (cursor != null) {
                mFavoriteMoviesCount = cursor.getCount();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String movieId = cursor.getString(cursor.getColumnIndex(ProviderSqlHelper.COLUMN_MOVIE_ID));
                    MovieFetcher movieFetcher = new MovieFetcher(this);
                    movieFetcher.execute(Integer.parseInt(movieId));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
    }

    /**
     * Called when the AsyncTask finishes execution and returns list of movies.
     * Displays the movies in a grid fashion.
     *
     * @param movies The List of MovieSqlDb objects.
     */
    @Override
    public void onMovieListResponse(final List<MovieDb> movies) {
        if (movies == null) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(), R.string.toast_success, Toast.LENGTH_SHORT).show();

        MoviesGridAdapter adapter = new MoviesGridAdapter(getContext(), movies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        movieGrid.setLayoutManager(layoutManager);
        movieGrid.setHasFixedSize(true);

        adapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mCallbacks.onGridItemClick(movies.get(position).getId());
            }
        });
        movieGrid.setAdapter(adapter);
        getFragmentManager().executePendingTransactions();
    }

    /**
     * Called each time a movie is fetched from the network,
     * used for displaying the user's favorite movies.
     */
    @Override
    public void onMovieResponse(MovieDb response) {
        mFavoriteMovies.add(response);
        counter++;
        if (counter == mFavoriteMoviesCount) {
            onMovieListResponse(mFavoriteMovies);
            counter = 0;
            mFavoriteMovies = new ArrayList<>();
        }
    }

    public interface MovieGridFragmentCallbacks {
        void onGridItemClick(int movieId);
    }
}
