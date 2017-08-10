package org.providence.hackathon.hackathon.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class FeedbackItem implements Parcelable {
    public String _type;
    public String _id;
    public FeedbackSource _source;

    public String getType() {
        return _type;
    }

    public String getId() {
        return _id;
    }

    public FeedbackSource getContent() {
        return _source;
    }

    public FeedbackItem() {
    }

    public FeedbackItem(String _id) {
        this._id = _id;
    }

    protected FeedbackItem(Parcel in) {
        _type = in.readString();
        _id = in.readString();
        _source = (FeedbackSource) in.readValue(FeedbackSource.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_type);
        dest.writeString(_id);
        dest.writeValue(_source);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FeedbackItem> CREATOR = new Parcelable.Creator<FeedbackItem>() {
        @Override
        public FeedbackItem createFromParcel(Parcel in) {
            return new FeedbackItem(in);
        }

        @Override
        public FeedbackItem[] newArray(int size) {
            return new FeedbackItem[size];
        }
    };
}
