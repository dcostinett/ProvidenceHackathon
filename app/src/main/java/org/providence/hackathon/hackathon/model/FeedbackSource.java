package org.providence.hackathon.hackathon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */

public class FeedbackSource implements Parcelable {
    public long content_length;
    public String objectKey;
    public String content_type;
    public String createdDate;

    public FeedbackSource() {
    }

    public FeedbackSource(String objectKey) {
        this.objectKey = objectKey;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    //"createdDate": "2017-08-10T17:46:11+00:00"
    public Date getCreatedDate() {
        Date theDate = null;
        try {
            theDate = DATE_FORMAT.parse(createdDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return theDate;
    }

    public long getContentLength() {
        return content_length;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getContentType() {
        return content_type;
    }

    protected FeedbackSource(Parcel in) {
        content_length = in.readLong();
        objectKey = in.readString();
        content_type = in.readString();
        createdDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(content_length);
        dest.writeString(objectKey);
        dest.writeString(content_type);
        dest.writeString(createdDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FeedbackSource> CREATOR = new Parcelable.Creator<FeedbackSource>() {
        @Override
        public FeedbackSource createFromParcel(Parcel in) {
            return new FeedbackSource(in);
        }

        @Override
        public FeedbackSource[] newArray(int size) {
            return new FeedbackSource[size];
        }
    };
}
