package org.providence.hackathon.hackathon.model;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 */

public class ImageFeedback {
    public MultipartBody.Part image;
    public RequestBody name;

    public ImageFeedback() {
    }

    public ImageFeedback(MultipartBody.Part image, RequestBody name) {
        this.image = image;
        this.name = name;
    }
}
