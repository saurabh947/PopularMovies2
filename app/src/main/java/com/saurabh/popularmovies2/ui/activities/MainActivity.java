package com.saurabh.popularmovies2.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;

import com.saurabh.popularmovies2.R;

public class MainActivity extends AppCompatActivity {
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBar = getSupportActionBar();

        MovieGridFragment movieGridFragment = MovieGridFragment.newInstance(
                new MovieGridFragment.MovieGridFragmentCallbacks() {

                    /**
                     * Called when a movie is clicked in the grid;
                     * navigates to {@link MovieDetailsFragment}
                     *
                     * @param movieId The movie id of the clicked grid item.
                     */
                    @Override
                    public void onGridItemClick(int movieId) {
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        float screenWidthInDp = displayMetrics.widthPixels / displayMetrics.density;

                        MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(movieId);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        if (screenWidthInDp >= 600) {
                            transaction.replace(R.id.movie_detail_container, movieDetailsFragment, MovieDetailsFragment.TAG);
                            transaction.commit();
                        } else {
                            transaction.replace(R.id.movie_grid_container, movieDetailsFragment, MovieDetailsFragment.TAG);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.movie_grid_container, movieGridFragment, MovieGridFragment.TAG);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_grid_activity_menu, menu);
        return true;
    }
}
