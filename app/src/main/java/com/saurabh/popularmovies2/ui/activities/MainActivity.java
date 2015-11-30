package com.saurabh.popularmovies2.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.facebook.stetho.Stetho;
import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.ui.fragments.MovieDetailsFragment;
import com.saurabh.popularmovies2.ui.fragments.MovieGridFragment;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.MovieGridFragmentCallbacks {

    private float mScreenWidthInDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mScreenWidthInDp = displayMetrics.widthPixels / displayMetrics.density;

        if (savedInstanceState == null) {
            MovieGridFragment movieGridFragment = MovieGridFragment.newInstance(this);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.movie_grid_container, movieGridFragment, MovieGridFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * Called when a movie is clicked in the {@link MovieGridFragment};
     * navigates to {@link MovieDetailsFragment}
     *
     * @param movieId The movie id of the clicked grid item.
     */
    @Override
    public void onGridItemClick(int movieId) {
        MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(movieId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mScreenWidthInDp >= 600) {
            transaction.replace(R.id.movie_detail_container, movieDetailsFragment, MovieDetailsFragment.TAG);
            transaction.commitAllowingStateLoss();
        } else {
            transaction.replace(R.id.movie_grid_container, movieDetailsFragment, MovieDetailsFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }
}
