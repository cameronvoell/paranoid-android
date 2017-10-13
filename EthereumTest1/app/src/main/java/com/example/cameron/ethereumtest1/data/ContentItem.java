package com.example.cameron.ethereumtest1.data;

/**
 * Created by cameron on 10/13/17.
 */

public class ContentItem {

    public String publishedBy;
    public String contentTypeDictionaryAddress;
    public String contentType;
    public String title;
    public long publishedDate;
    public String primaryText;
    public String primaryImageUrl;
    public String primaryHttpLink;
    public String primaryContentAddressedLink;

    public ContentItem(String publishedBy, String contentTypeDictionaryAddress, String contentType,
                       String title, long publishedDate, String primaryText, String primaryImageUrl,
                        String primaryHttpLink, String primaryContentAddressedLink) {
        this.publishedBy = publishedBy;
        this.contentTypeDictionaryAddress = contentTypeDictionaryAddress;
        this.contentType = contentType;
        this.title = title;
        this.publishedDate = publishedDate;
        this.primaryText = primaryText;
        this.primaryImageUrl = primaryImageUrl;
        this.primaryHttpLink = primaryHttpLink;
        this.primaryContentAddressedLink = primaryContentAddressedLink;
    }

    @Override
    public String toString() {
        return title;
    }

}
