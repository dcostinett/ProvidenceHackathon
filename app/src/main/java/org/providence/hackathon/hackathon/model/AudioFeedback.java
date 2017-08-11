package org.providence.hackathon.hackathon.model;

import okhttp3.RequestBody;

/**
 *
 */

public class AudioFeedback {
    public RequestBody data;

    public AudioFeedback() {
    }

    public AudioFeedback(RequestBody data) {
        this.data = data;
    }
}
