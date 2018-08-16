package com.example.cameron.ethereumtest1.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.cameron.ethereumtest1.model.ContentItem;

public class DBUserContentItem implements Parcelable {

    public String publishedByEthAddress;
    public int userContentIndex;
    public String contentIPFS;
    public String imageIPFS;
    public String json;
    public String title;
    public String primaryText;
    public long publishedDate;
    public boolean confirmed;
    public boolean draft;


    public DBUserContentItem(String publishedByEthAddress, int userContentIndex, String contentIPFS,
                             String imageIPFS, String json, String title, String primaryText,
                             long publishedDate, boolean confirmed, boolean draft) {
        this.publishedByEthAddress = publishedByEthAddress;
        this.userContentIndex = userContentIndex;
        this.contentIPFS = contentIPFS;
        this.imageIPFS = imageIPFS;
        this.json = json;
        this.title = title;
        this.primaryText = primaryText;
        this.publishedDate = publishedDate;
        this.confirmed = confirmed;
        this.draft = draft;
    }

    protected DBUserContentItem(Parcel in) {
        publishedByEthAddress = in.readString();
        userContentIndex = in.readInt();
        contentIPFS = in.readString();
        imageIPFS = in.readString();
        json = in.readString();
        title = in.readString();
        primaryText = in.readString();
        publishedDate = in.readLong();
        confirmed = in.readByte() != 0;
        draft = in.readByte() != 0;
    }

    public static final Creator<DBUserContentItem> CREATOR = new Creator<DBUserContentItem>() {
        @Override
        public DBUserContentItem createFromParcel(Parcel in) {
            return new DBUserContentItem(in);
        }

        @Override
        public DBUserContentItem[] newArray(int size) {
            return new DBUserContentItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(publishedByEthAddress);
        dest.writeInt(userContentIndex);
        dest.writeString(contentIPFS);
        dest.writeString(imageIPFS);
        dest.writeString(json);
        dest.writeString(title);
        dest.writeString(primaryText);
        dest.writeLong(publishedDate);
        dest.writeByte((byte) (confirmed ? 1 : 0));
        dest.writeByte((byte) (draft ? 1 : 0));
    }
}
