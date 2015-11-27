/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.network.MovieFetcher;
import com.saurabh.popularmovies2.data.network.MoviesListFetcher;
import com.saurabh.popularmovies2.data.provider.ProviderSqlHelper;
import com.saurabh.popularmovies2.ui.adapters.MoviesGridAdapter;
import com.saurabh.popularmovies2.ui.dialogs.SortDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Showed when the app is launched, this activity displays the movie posters in a GridView.
 * <p/>
 * The menu overflow icon can be used to change the sort order of the movies.
 */
public class MovieGridActivity extends AppCompatActivity implements MoviesListFetcher.MoviesListFetcherListener,
        MovieFetcher.MovieFetcherListener, SortDialog.SortDialogListener {
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.movie_grid)
    RecyclerView movieGrid;
    /**
     * Called each time a movie is fetched from the network,
     * used for displaying the user's favorite movies.
     */
    int counter = 0;
    private ActionBar mActionBar;
    private MoviesGridAdapter mAdapter;
    private List<MovieDb> mFavoriteMovies = new ArrayList<>();
    private int mFavoriteMoviesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        initializeStetho();

        mActionBar = getSupportActionBar();

        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);

        populateGridWithMovies(Constants.SORT_POPULARITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_grid_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                SortDialog dialog = new SortDialog();
                dialog.show(getFragmentManager(), SortDialog.TAG);
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
        if (sortCriteria.equals(Constants.SORT_POPULARITY)) {
            mActionBar.setTitle(R.string.title_popular_movies);
        } else {
            mActionBar.setTitle(R.string.title_highest_rated_movies);
        }
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

            Cursor cursor = getContentResolver()
                    .query(ProviderSqlHelper.CONTENT_URI,
                            null,
                            selection,
                            selectionArgs,
                            null);
            if (cursor != null && cursor.getCount() == 0) {
                Toast.makeText(this, R.string.error_no_favorite_movies, Toast.LENGTH_LONG).show();
                cursor.close();

            } else if (cursor != null) {
                mActionBar.setTitle(R.string.title_favorite_movies);
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
     * **For Debugging purposes only.**
     */
    private void initializeStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    /**
     * Called when a movie is clicked in the grid;
     * navigates to {@link MovieDetailsActivity}
     *
     * @param movieId The movie id of the clicked grid item.
     */
    private void showMovieDetails(int movieId) {
        Intent intent = new Intent(MovieGridActivity.this, MovieDetailsActivity.class);
        intent.putExtra("selectedMovieId", movieId);
        startActivity(intent);
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
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.toast_success, Toast.LENGTH_SHORT).show();

        mAdapter = new MoviesGridAdapter(this, movies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        movieGrid.setLayoutManager(layoutManager);
        movieGrid.setHasFixedSize(true);

        mAdapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showMovieDetails(movies.get(position).getId());
            }
        });
        movieGrid.setAdapter(mAdapter);
    }

    /**
     * Called when the user selects an item from the sort dialog fragment.
     *
     * @param sortCriteria the criteria to sort with.
     */
    @Override
    public void onSortCriteriaClicked(String sortCriteria) {
        progressBar.setVisibility(View.VISIBLE);
        populateGridWithMovies(sortCriteria);
    }

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
}
