package org.providence.hackathon.hackathon;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.providence.hackathon.hackathon.model.AudioFeedback;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecordingActivity extends BaseActivity {
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();

    private MediaRecorder mRecorder;
    private MediaPlayer   mPlayer = null;
    private static String mAudioFileName = null;

    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    @BindView(R.id.btnToggleRecording)
    Button btnRecordAudio;

    @BindView(R.id.btnPlayFeedback)
    Button btnPlayFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        mAudioFileName = getExternalCacheDir().getAbsolutePath();
        mAudioFileName += "/feedbackAudio.mp3";
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mStartRecording = true;
        mStartPlaying = true;
    }

    @Override
    protected void onPause() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        mStartRecording = false;
        mStartPlaying = false;

        super.onPause();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mAudioFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void onRecordClicked(View view) {
        onRecord(mStartRecording);
        if (mStartRecording) {
            btnRecordAudio.setText("Stop");
        } else {
            btnRecordAudio.setText("Record");
        }
        mStartRecording = !mStartRecording;
    }

    public void onPlayClicked(View view) {
        onPlay(mStartPlaying);
        if (mStartPlaying) {
            btnPlayFeedback.setText("Stop");
        } else {
            btnPlayFeedback.setText("Play");
        }
        mStartPlaying = !mStartPlaying;
    }

    public void onFinishClicked(View view) {
        // send audio file to server, finish();
        File audioFeedback = new File(mAudioFileName);

        RequestBody feedbackRequest = RequestBody.create(MediaType.parse("audio/mpeg"), audioFeedback);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", audioFeedback.getName(), feedbackRequest);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "feedback_audio");

        if (audioFeedback.exists()) {
            service.postAudioFeedback(this, new AudioFeedback(body, name));
        }

        finish();
    }
}
