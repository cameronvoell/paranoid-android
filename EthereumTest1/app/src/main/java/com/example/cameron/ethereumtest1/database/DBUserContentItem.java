package com.example.cameron.ethereumtest1.database;

import com.example.cameron.ethereumtest1.model.ContentItem;

public class DBUserContentItem {

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

}
