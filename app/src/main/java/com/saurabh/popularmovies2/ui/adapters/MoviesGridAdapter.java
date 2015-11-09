/**
 * Copyright (C) 2015 Saurabh Agrawal
 */

package com.saurabh.popularmovies2.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * The Adapter for displaying the GridView in MovieGridActivity
 */
public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.ViewHolder> {

    private Context mContext;
    private List<MovieDb> mMovieDbs;
    private OnItemClickListener mListener;

    public MoviesGridAdapter(Context context, List<MovieDb> movies) {
        mContext = context;
        mMovieDbs = movies;
    }

    /**
     * ViewHolder for storing the view references
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.grid_movie_poster) ImageView movie_poster;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public MoviesGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.movie_poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(mContext)
                .load(Constants.THUMBNAIL_URL + mMovieDbs.get(position).getPosterPath())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .fit().centerCrop()
                .into(viewHolder.movie_poster);
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
}
