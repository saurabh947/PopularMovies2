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

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.MoviesListFetcher;
import com.saurabh.popularmovies2.ui.adapters.MoviesGridAdapter;
import com.saurabh.popularmovies2.ui.listeners.MoviesListFetcherListener;

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
public class MovieGridActivity extends AppCompatActivity implements MoviesListFetcherListener {
    public static final String TAG = MovieGridActivity.class.getSimpleName();

    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.movie_grid) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

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
            case R.id.menu_popularity:
                progressBar.setVisibility(View.VISIBLE);
                populateGrid(Constants.SORT_POPULARITY);
                return true;

            case R.id.menu_rating:
                progressBar.setVisibility(View.VISIBLE);
                populateGrid(Constants.SORT_RATING);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fires the AsyncTask to get the list of movies based on sort criteria.
     *
     * @param sortCriteria The sort criteria
     */
    public void populateGrid(String sortCriteria) {
        MoviesListFetcher moviesListFetcher = new MoviesListFetcher(this);
        moviesListFetcher.execute(sortCriteria);
    }

    /**
     * Called when the AsyncTask finishes execution and returns results.
     *
     * @param movies The List of MovieDb objects.
     */
    @Override
    public void onTaskCompleted(final List<MovieDb> movies) {
        if (movies == null) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.error_movies, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.toast_success, Toast.LENGTH_SHORT).show();

        MoviesGridAdapter adapter = new MoviesGridAdapter(this, movies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter.setOnItemClickListener(new MoviesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MovieGridActivity.this, MovieDetailsActivity.class);
                intent.putExtra("selectedMovieId", movies.get(position).getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
