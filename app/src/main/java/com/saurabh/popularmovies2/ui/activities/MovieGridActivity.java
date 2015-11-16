/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.saurabh.popularmovies2.data.network.MoviesListFetcher;
import com.saurabh.popularmovies2.ui.adapters.MoviesGridAdapter;
import com.saurabh.popularmovies2.ui.dialogs.SortDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Showed when the app is launched, this activity displays the movie posters &
 * their title in a GridView.
 * <p/>
 * The menu overflow icon can be used to change the sort order of the movies.
 */
public class MovieGridActivity extends AppCompatActivity implements MoviesListFetcher.MoviesListFetcherListener,
        SortDialog.SortDialogListener {
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.movie_grid)
    RecyclerView movieGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        initializeStetho();

        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);

        populateGrid(Constants.SORT_POPULARITY);
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
                populateGrid(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fires the AsyncTask to get the list of movies based on sort criteria.
     *
     * @param sortCriteria The sort criteria
     */
    private void populateGrid(String sortCriteria) {
        MoviesListFetcher moviesListFetcher = new MoviesListFetcher(this);
        moviesListFetcher.execute(sortCriteria);
    }

    private void populateGrid(boolean showFavorites) {
        // TODO: Show user's favorites
    }

    private void initializeStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    private void showMovieDetailsScreen(int movieId) {
        Intent intent = new Intent(MovieGridActivity.this, MovieDetailsActivity.class);
        intent.putExtra("selectedMovieId", movieId);
        startActivity(intent);
    }

    /**
     * Called when the AsyncTask finishes execution and returns list of movies.
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

        MoviesGridAdapter adapter = new MoviesGridAdapter(this, movies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        movieGrid.setLayoutManager(layoutManager);
        movieGrid.setHasFixedSize(true);

        adapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showMovieDetailsScreen(movies.get(position).getId());
            }
        });
        movieGrid.setAdapter(adapter);
    }

    /**
     * Called when the user selects an item from the sort dialog fragment.
     *
     * @param sortCriteria the criteria to sort with.
     */
    @Override
    public void onSortCriteriaClicked(String sortCriteria) {
        progressBar.setVisibility(View.VISIBLE);
        populateGrid(sortCriteria);
    }
}
