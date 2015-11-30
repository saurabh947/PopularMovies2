/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    @Bind(R.id.movie_grid) RecyclerView movieGrid;

    private ActionBar mActionBar;
    private int counter = 0;
    private List<MovieDb> mFavoriteMovies = new ArrayList<>();
    private int mFavoriteMoviesCount;
    private String mGridContents;
    private MovieGridFragmentCallbacks mCallbacks;

    public MovieGridFragment() {
        setRetainInstance(true);
    }

    public static MovieGridFragment newInstance(MovieGridFragmentCallbacks callbacks) {
        MovieGridFragment fragment = new MovieGridFragment();
        fragment.mCallbacks = callbacks;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);

        // Return the previous instance; if present.
        if (savedInstanceState != null && savedInstanceState.getString("gridContents") != null) {
            if (savedInstanceState.getString("gridContents").equals(getString(R.string.favorites_shown))) {
                populateGridWithFavorites(true);
            } else {
                populateGridWithMovies(savedInstanceState.getString("gridContents"));
            }
        } else {
            mGridContents = Constants.SORT_POPULARITY;
            populateGridWithMovies(mGridContents);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("gridContents", mGridContents);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context != null) {
            try {
                mCallbacks = (MovieGridFragmentCallbacks) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_grid_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                mGridContents = Constants.SORT_POPULARITY;
                populateGridWithMovies(mGridContents);
                return true;

            case R.id.menu_sort_rating:
                mGridContents = Constants.SORT_RATING;
                populateGridWithMovies(mGridContents);
                return true;

            case R.id.menu_favorite:
                mGridContents = getString(R.string.favorites_shown);
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
        if (sortCriteria.equals(Constants.SORT_POPULARITY)) {
            mActionBar.setTitle(R.string.title_popular_movies);
        } else {
            mActionBar.setTitle(R.string.title_highest_rated_movies);
        }
    }

    /**
     * Populates the grid with the user's favorite movies.
     * Shows an error toast message of there are none.
     *
     * @param showFavorites the boolean flag.
     */
    private void populateGridWithFavorites(boolean showFavorites) {
        if (showFavorites) {
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
                mActionBar.setTitle(R.string.title_favorite_movies);
                progressBar.setVisibility(View.VISIBLE);

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
//        getFragmentManager().executePendingTransactions();
    }

    /**
     * Called each time a movie is fetched from the network,
     * used for displaying the user's favorite movies.
     */
    @Override
    public void onMovieResponse(MovieDb response) {
        mFavoriteMovies.add(response);
        if (response != null) {
            counter++;
            if (counter == mFavoriteMoviesCount) {
                onMovieListResponse(mFavoriteMovies);
                counter = 0;
                mFavoriteMovies = new ArrayList<>();
            }
        } else {
            onMovieListResponse(null);
        }
    }

    public interface MovieGridFragmentCallbacks {
        void onGridItemClick(int movieId);
    }
}
