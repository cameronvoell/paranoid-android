package com.example.cameron.ethereumtest1.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Content {

    public static final List<ContentItem> ITEMS = new ArrayList<ContentItem>();
    public static final List<ContentContract.ContentContractItem> CONTRACT_ITEMS = new ArrayList<ContentContract.ContentContractItem>();
    public static final Map<String, ContentContract.ContentContractItem> CONTRACT_ITEM_MAP = new HashMap<String, ContentContract.ContentContractItem>();


    public static void addContentItem(ContentItem item) {
        ITEMS.add(item);
    }

    public static void addContractItem(ContentContract.ContentContractItem item) {
        CONTRACT_ITEMS.add(item);
        CONTRACT_ITEM_MAP.put(item.name, item);
    }

    public static void clearItems() {
        ITEMS.clear();
    }

    public static void clearContractItems() {
        CONTRACT_ITEMS.clear();
    }

    public static class ContentContract {

        public static class ContentContractItem {
            public final String name;
            public final String description;
            public final long numPosts;
            public final String admin;

            public ContentContractItem(String name, String description, long numPosts, String admin) {
                this.name = name;
                this.description = description;
                this.numPosts = numPosts;
                this.admin = admin;
            }

            @Override
            public String toString() {
                return name;
            }
        }
    }
}
