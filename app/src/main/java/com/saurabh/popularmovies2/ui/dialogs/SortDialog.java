package com.saurabh.popularmovies2.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.saurabh.popularmovies2.R;
import com.saurabh.popularmovies2.constants.Constants;

/**
 * A {@link DialogFragment} for showing the movie sorting options to the user.
 */
public class SortDialog extends DialogFragment {
    public static final String TAG = SortDialog.class.getSimpleName();

    private SortDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] sort_array = {getResources().getString(R.string.sort_popularity),
                getResources().getString(R.string.sort_rating)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setItems(sort_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        if (position == 0) {
                            mListener.onSortCriteriaClicked(Constants.SORT_POPULARITY);
                        } else {
                            mListener.onSortCriteriaClicked(Constants.SORT_RATING);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SortDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SortDialogListener");
        }
    }

    /**
     * The interface used for returning the the result from the dialog.
     */
    public interface SortDialogListener {
        /**
         * Called when a sorting criteria is clicked.
         *
         * @param sortCriteria The clicked criteria.
         */
        void onSortCriteriaClicked(String sortCriteria);
    }
}
