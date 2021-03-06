package com.saurabh.popularmovies2.data.network;

import android.os.AsyncTask;
import android.util.Log;

import com.saurabh.popularmovies2.constants.Keys;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Video;

/**
 * An AsyncTask which queries themoviedb.org API for the list of videos
 * for a particular movie.
 * <p/>
 * Needs movie id as input.
 */
public class VideosFetcher extends AsyncTask<Integer, Void, List<Video>> {
    public static final String TAG = VideosFetcher.class.getSimpleName();

    private VideosFetcherListener mVideosFetcherListener;

    public VideosFetcher(VideosFetcherListener context) {
        this.mVideosFetcherListener = context;
    }

    @Override
    protected List<Video> doInBackground(Integer... params) {
        try {
            return new TmdbApi(Keys.API_KEY)
                    .getMovies()
                    .getVideos(params[0], "en");
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
    protected void onPostExecute(List<Video> reviews) {
        mVideosFetcherListener.onVideoResponse(reviews);
    }

    /**
     * The interface used for returning the the selected movie from the AsyncTask
     */
    public interface VideosFetcherListener {
        /**
         * Called when the AsyncTask is finished executing
         */
        void onVideoResponse(List<Video> response);
    }
}
