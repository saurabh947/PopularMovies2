/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.saurabh.popularmovies2.ui.activities.MovieGridFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * An Adapter responsible for managing the {@link MovieDb} and
 * displaying it in {@link MovieGridFragment}.
 */
public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.ViewHolder> {

    private Context mContext;
    private List<MovieDb> mMovieDbs;
    private OnItemClickListener mListener;

    public MoviesGridAdapter(Context context, List<MovieDb> movies) {
        mContext = context;
        mMovieDbs = movies;
    }

    @Override
    public MoviesGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.moviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(mContext)
                .load(Uri.withAppendedPath(Constants.THUMBNAIL_URL, mMovieDbs.get(position).getPosterPath()))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .fit().centerCrop()
                .into(viewHolder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return mMovieDbs.size();
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * ViewHolder for storing the view references
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.grid_movie_poster)
        ImageView moviePoster;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(view, getAdapterPosition());
        }
    }
}
