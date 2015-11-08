/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.data.DataFetcher;
import com.saurabh.popularmovies2.ui.helpers.DataFetcherListener;
import com.saurabh.popularmovies2.ui.helpers.GridAdapter;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Showed when the app is launched, this activity displays the movie posters &
 * their title in a GridView.
 * <p/>
 * The menu overflow icon can be used to change the sort order of the movies.
 */
public class MovieGridActivity extends AppCompatActivity implements DataFetcherListener {

    private ProgressBar mProgressBar;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mGridView = (GridView) findViewById(R.id.movie_grid);

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
                mProgressBar.setVisibility(View.VISIBLE);
                populateGrid(Constants.SORT_POPULARITY);
                return true;

            case R.id.menu_rating:
                mProgressBar.setVisibility(View.VISIBLE);
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
        DataFetcher dataFetcher = new DataFetcher(this);
        dataFetcher.execute(sortCriteria);
    }

    /**
     * Called when the AsyncTask finishes execution and returns results.
     *
     * @param movies The List of MovieDb objects.
     */
    @Override
    public void onTaskCompleted(final List<MovieDb> movies) {
        if (movies == null) {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.error_movies, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.toast_success, Toast.LENGTH_SHORT).show();

        GridAdapter adapter = new GridAdapter(this, movies);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MovieGridActivity.this, MovieDetailsActivity.class);
                intent.putExtra("selectedMovie", movies.get(position));
                startActivity(intent);
            }
        });
    }
}
