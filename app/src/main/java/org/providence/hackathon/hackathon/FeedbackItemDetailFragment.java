package org.providence.hackathon.hackathon;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.providence.hackathon.hackathon.model.FeedbackItem;


/**
 * A fragment representing a single FeedbackItem detail screen.
 * This fragment is either contained in a {@link FeedbackItemListActivity}
 * in two-pane mode (on tablets) or a {@link FeedbackItemDetailActivity}
 * on handsets.
 */
public class FeedbackItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private FeedbackItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedbackItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // TODO call the details endpoint with the ARG_ITM_ID (objectKey) and show that object
            mItem = new FeedbackItem(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getContent().getObjectKey());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.feedbackitem_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.feedbackitem_detail)).setText(mItem.getContent().getObjectKey());
        }

        return rootView;
    }
}
