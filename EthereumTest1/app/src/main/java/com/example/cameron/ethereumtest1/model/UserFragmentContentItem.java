package com.example.cameron.ethereumtest1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cameron on 10/26/17.
 */

public class UserFragmentContentItem implements Parcelable {

    public long index;
    public ContentItem contentItem;
    public String contentHash;
    public String link1;
    public String link2;

    public UserFragmentContentItem(long index, ContentItem contentItem, String contentHash,
                                   String link1, String link2) {
        this.index = index;
        this.contentItem = contentItem;
        this.contentHash = contentHash;
        this.link1 = link1;
        this.link2 = link2;
    }

    protected UserFragmentContentItem(Parcel in) {
        index = in.readLong();
        contentItem = in.readParcelable(ContentItem.class.getClassLoader());
        contentHash = in.readString();
        link1 = in.readString();
        link2 = in.readString();
    }

    public static final Creator<UserFragmentContentItem> CREATOR = new Creator<UserFragmentContentItem>() {
        @Override
        public UserFragmentContentItem createFromParcel(Parcel in) {
            return new UserFragmentContentItem(in);
        }

        @Override
        public UserFragmentContentItem[] newArray(int size) {
            return new UserFragmentContentItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(index);
        dest.writeParcelable(contentItem, flags);
        dest.writeString(contentHash);
        dest.writeString(link1);
        dest.writeString(link2);
    }
}
