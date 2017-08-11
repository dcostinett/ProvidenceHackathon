package org.providence.hackathon.hackathon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.providence.hackathon.hackathon.model.FeedbackItem;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public static final String ARG_ITEM_OBJECT_KEY = "item_id";
    public static final String ARG_ITEM_TYPE = "item_type";
    public static final String EXTRA_FEEDBACK_RESULT = "objectDetailsResult";
    public static final String EXTRA_FEEDBACK_OBJECT = "objectDetails";
    public static final String TEXT_RETRIEVED_ACTION = "textItemRetrieved";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedbackItemDetailFragment() {
    }

    @BindView(R.id.feedbackImage)
    AppCompatImageView feedbackImage;

    @BindView(R.id.feedbackText)
    AppCompatTextView feedbackText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(textFeedbackReceiver, new IntentFilter(TEXT_RETRIEVED_ACTION));
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(textFeedbackReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.feedbackitem_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (getArguments().containsKey(ARG_ITEM_OBJECT_KEY)) {
            String path = getArguments().getString(ARG_ITEM_OBJECT_KEY);
            String type = getType(path);
            switch (type) {
                case "images":
                    feedbackImage.setVisibility(View.VISIBLE);
                    feedbackText.setVisibility(View.INVISIBLE);
                    Glide.with(getActivity()).load(Uri.parse(
                            NetworkService.BASE_URL + path)).into(feedbackImage);

                    break;
                case "audio":
                    feedbackImage.setVisibility(View.INVISIBLE);
                    feedbackText.setVisibility(View.INVISIBLE);
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        player.setDataSource(NetworkService.BASE_URL + path);
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.start();
                    break;
                case "text":
                    feedbackImage.setVisibility(View.INVISIBLE);
                    feedbackText.setVisibility(View.VISIBLE);
                    ((BaseActivity) getActivity()).service.getTextFeedback(getActivity(), path);
                    break;

            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(path);
            }

            if (path != null) {
                ((TextView) rootView.findViewById(R.id.feedbackitem_detail)).setText(path);
            }
        }

        return rootView;
    }

    private String getType(String path) {
        String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
        switch (ext) {
            case "jpg":
                return "images";
            case "mp3":
                return "audio";
            case "txt":
                return "text";
        }

        return "unknown";
    }

    BroadcastReceiver textFeedbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case TEXT_RETRIEVED_ACTION:
                    if (intent.getStringExtra(EXTRA_FEEDBACK_RESULT) != null) {
                        String textFeedback = intent.getStringExtra(EXTRA_FEEDBACK_RESULT);
                        feedbackText.setText(textFeedback);
                    }
            }
        }
    };
}
