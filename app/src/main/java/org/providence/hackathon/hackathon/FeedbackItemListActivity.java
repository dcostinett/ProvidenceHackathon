package org.providence.hackathon.hackathon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.providence.hackathon.hackathon.model.FeedbackItem;
import org.providence.hackathon.hackathon.model.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of FeedbackItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FeedbackItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FeedbackItemListActivity extends BaseActivity {

    public static final String EXTRA_FEEDBACK_RESULT = "feedbackResult";
    public static final String FEEDBACK_LIST_RETRIEVED_ACTION = "feedbackRetrieved";
    public static final String LIST_KEY = "feedbackList";

    private RecyclerView mRecyclerView = null;
    private List<FeedbackItem> mFeedbackItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Show items on a map, maybe", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showDetails(FeedbackItemListActivity.this, "AV3NNGZ5d5-Zkqn7f7CB", "images");
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.feedbackitem_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(listRetrievedReceiver, new IntentFilter(FEEDBACK_LIST_RETRIEVED_ACTION));
        service.getFeedbackItems(this, new SearchCriteria());
    }

    @Override
    protected void onPause() {
        unregisterReceiver(listRetrievedReceiver);
        super.onPause();
    }

    private void setupAdapter() {
        SimpleItemRecyclerViewAdapter adapter = new SimpleItemRecyclerViewAdapter(mFeedbackItems);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.invalidate();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mFeedbackItems));
    }

    public static Intent buildIntent(Context context) {
        return new Intent(context, FeedbackItemListActivity.class);
    }

    private void showDetails(Context context, final String key, final String type) {
        Intent intent = new Intent(context, FeedbackItemDetailActivity.class);
        intent.putExtra(FeedbackItemDetailFragment.ARG_ITEM_OBJECT_KEY, key);
        intent.putExtra(FeedbackItemDetailFragment.ARG_ITEM_TYPE, type);

        context.startActivity(intent);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<FeedbackItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<FeedbackItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feedbackitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getId());
            holder.mIcon.setImageResource(getItemViewType(position));
            holder.mContentView.setText(mValues.get(position).getContent().getObjectKey());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetails(v.getContext(), holder.mItem.getContent().getObjectKey(), holder.mItem.getType());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public int getItemViewType(int position) {
            switch (mValues.get(position).getType()) {
                case "VOICE":
                    return R.drawable.ic_mic_black_24px;
                case "images":
                    return R.drawable.ic_camera_enhance_black_24px;
                case "TEXT":
                    return R.drawable.ic_list_black_24px;
                case "VIDEO":
                    return R.drawable.ic_videocam_black_24px;
            }

            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public TextView mIdView;
            public AppCompatImageView mIcon;
            public TextView mContentView;

            public FeedbackItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mIdView = (TextView) view.findViewById(R.id.id);
                mIcon = (AppCompatImageView) view.findViewById(R.id.item_icon);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    BroadcastReceiver listRetrievedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int result = intent.getIntExtra(EXTRA_FEEDBACK_RESULT, Activity.RESULT_CANCELED);
            final String action = intent.getAction();

            switch (action) {
                case FEEDBACK_LIST_RETRIEVED_ACTION:
                    if (intent.getParcelableArrayListExtra(LIST_KEY) != null) {
                        mFeedbackItems = intent.getParcelableArrayListExtra(LIST_KEY);
                    }
                    setupAdapter();
                    break;
            }
        }
    };
}
