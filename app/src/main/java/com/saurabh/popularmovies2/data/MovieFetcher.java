package com.saurabh.popularmovies2.data;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;

public class MovieFetcher extends AsyncTask<Integer, Void, MovieDb>{
    public static final String TAG = MovieFetcher.class.getSimpleName();

    private MovieFetcherListener mMovieFetcherListener;

    public MovieFetcher(MovieFetcherListener context) {
        this.mMovieFetcherListener = context;
    }

    @Override
    protected MovieDb doInBackground(Integer... params) {
        try {
            return new TmdbApi(Keys.API_KEY)
                    .getMovies()
                    .getMovie(params[0], "en");
        } catch (Exception e) {
            Log.e(TAG, "Error: Could not connect to remote server.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fires the interface method and sets the result.
     */
    @Override
    protected void onPostExecute(MovieDb movie) {
        mMovieFetcherListener.onMovieResponse(movie);
    }

    /**
     * The interface used for returning the the selected movie from the AsyncTask
     */
    public interface MovieFetcherListener {

        /**
         * Called when the AsyncTask is finished executing
         */
        void onMovieResponse(MovieDb response);
    }
}
