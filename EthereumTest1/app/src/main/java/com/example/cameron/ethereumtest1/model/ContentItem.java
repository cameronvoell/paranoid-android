package com.example.cameron.ethereumtest1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cameron on 10/13/17.
 */

public class ContentItem implements Parcelable {

    public String publishedBy;
//    public String contentTypeDictionaryAddress;
//    public String contentType;
    public String title;
    public long publishedDate;
    public String primaryText;
    public String primaryImageUrl;
    public String primaryHttpLink;
    public String primaryContentAddressedLink;

    public ContentItem(String publishedBy,
                       //String contentTypeDictionaryAddress, String contentType,
                       String title, long publishedDate, String primaryText, String primaryImageUrl,
                        String primaryHttpLink, String primaryContentAddressedLink) {
        this.publishedBy = publishedBy;
//        this.contentTypeDictionaryAddress = contentTypeDictionaryAddress;
//        this.contentType = contentType;
        this.title = title;
        this.publishedDate = publishedDate;
        this.primaryText = primaryText;
        this.primaryImageUrl = primaryImageUrl;
        this.primaryHttpLink = primaryHttpLink;
        this.primaryContentAddressedLink = primaryContentAddressedLink;
    }

    protected ContentItem(Parcel in) {
        publishedBy = in.readString();
        title = in.readString();
        publishedDate = in.readLong();
        primaryText = in.readString();
        primaryImageUrl = in.readString();
        primaryHttpLink = in.readString();
        primaryContentAddressedLink = in.readString();
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
        dest.writeString(primaryHttpLink);
        dest.writeString(primaryContentAddressedLink);
    }
}
