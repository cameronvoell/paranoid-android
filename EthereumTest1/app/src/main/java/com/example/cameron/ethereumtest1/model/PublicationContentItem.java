package com.example.cameron.ethereumtest1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cameron on 10/26/17.
 */

public class PublicationContentItem implements Parcelable {

    public long index;
    public ContentItem contentItem;
    public String contentRevenue;
    public int uniqueSupporters;

    public PublicationContentItem(long index, ContentItem contentItem,
                                  String contentRevenue,
                                  int uniqueSupporters) {
        this.index = index;
        this.contentItem = contentItem;
        this.contentRevenue = contentRevenue;
        this.uniqueSupporters = uniqueSupporters;
    }

    protected PublicationContentItem(Parcel in) {
        index = in.readLong();
        contentItem = in.readParcelable(ContentItem.class.getClassLoader());
        contentRevenue = in.readString();
        uniqueSupporters = in.readInt();
    }

    public static final Creator<PublicationContentItem> CREATOR = new Creator<PublicationContentItem>() {
        @Override
        public PublicationContentItem createFromParcel(Parcel in) {
            return new PublicationContentItem(in);
        }

        @Override
        public PublicationContentItem[] newArray(int size) {
            return new PublicationContentItem[size];
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
        dest.writeString(contentRevenue);
        dest.writeInt(uniqueSupporters);
    }
}
