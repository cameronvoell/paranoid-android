package com.example.cameron.ethereumtest1.database;

import com.example.cameron.ethereumtest1.model.ContentItem;

public class DBUserContentItem {

    public String publishedByEthAddress;
    public long publishedDate;
    public String txId;
    public String contentIPFS;
    public String imageIPFS;
    public String json;
    public int index;
    public boolean confirmed;
    public ContentItem contentItem;
    //ContentItem fields:
//    public String publishedBy;
//    public String title;
//    public long publishedDate;
//    public String primaryText;
//    public String primaryImageUrl;
//    public String primaryHttpLink;
//    public String primaryContentAddressedLink;


    public DBUserContentItem(String publishedByEthAddress, long publishedDate, String txId,
                             String contentIPFS, String imageIPFS, String json,
                             boolean confirmed, int index, ContentItem contentItem) {
        this.publishedByEthAddress = publishedByEthAddress;
        this.publishedDate = publishedDate;
        this.txId = txId;
        this.contentIPFS = contentIPFS;
        this.imageIPFS = imageIPFS;
        this.json = json;
        this.contentIPFS = contentIPFS;
        this.confirmed = confirmed;
        this.index = index;
        this.contentItem = contentItem;
    }

}
