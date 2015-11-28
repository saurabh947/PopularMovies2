package com.saurabh.popularmovies2.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.ui.activities.MovieDetailsFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.Reviews;

/**
 * An Adapter responsible for managing the {@link Reviews} and
 * displaying it in {@link MovieDetailsFragment}.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private Context mContext;
    private List<Reviews> mReviews;

    public MovieReviewsAdapter(Context context, List<Reviews> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.reviewAuthor.setText(mReviews.get(position).getAuthor());
        viewHolder.reviewText.setText(mReviews.get(position).getContent());

        viewHolder.viewFullReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mReviews.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    /**
     * ViewHolder for storing the view references
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_review_author)
        TextView reviewAuthor;
        @Bind(R.id.movie_review_text)
        TextView reviewText;
        @Bind(R.id.view_full_review)
        TextView viewFullReview;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
