package org.providence.hackathon.hackathon.model;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 */

public class AudioFeedback {
    public MultipartBody.Part audio;
    public RequestBody name;

    public AudioFeedback() {
    }

    public AudioFeedback(MultipartBody.Part audio, RequestBody name) {
        this.audio = audio;
        this.name = name;
    }
}
