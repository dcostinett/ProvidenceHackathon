package org.providence.hackathon.hackathon.model;

import okhttp3.RequestBody;

/**
 *
 */

public class ImageFeedback {
    public RequestBody image;

    public ImageFeedback() {
    }

    public ImageFeedback(RequestBody image) {
        this.image = image;
    }
}
