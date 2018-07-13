package com.example.cameron.ethereumtest1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cameron on 10/13/17.
 */

public class ContentItem implements Parcelable {

    public String publishedBy;
    public String title;
    public long publishedDate;
    public String primaryText;
    public String primaryImageUrl;
    public int userContentIndex;

    public ContentItem(String publishedBy, String title, long publishedDate, String primaryText, String primaryImageUrl, int userContentIndex) {
        this.publishedBy = publishedBy;
        this.title = title;
        this.publishedDate = publishedDate;
        this.primaryText = primaryText;
        this.primaryImageUrl = primaryImageUrl;
        this.userContentIndex = userContentIndex;
    }

    protected ContentItem(Parcel in) {
        publishedBy = in.readString();
        title = in.readString();
        publishedDate = in.readLong();
        primaryText = in.readString();
        primaryImageUrl = in.readString();
        userContentIndex = in.readInt();
    }

    public static final Creator<ContentItem> CREATOR = new Creator<ContentItem>() {
        @Override
        public ContentItem createFromParcel(Parcel in) {
            return new ContentItem(in);
        }

        @Override
        public ContentItem[] newArray(int size) {
            return new ContentItem[size];
        }
    };

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(publishedBy);
        dest.writeString(title);
        dest.writeLong(publishedDate);
        dest.writeString(primaryText);
        dest.writeString(primaryImageUrl);
        dest.writeInt(userContentIndex);
    }
}
