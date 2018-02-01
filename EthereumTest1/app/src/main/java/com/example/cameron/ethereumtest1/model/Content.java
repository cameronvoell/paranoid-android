package com.example.cameron.ethereumtest1.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Content {

    public static final List<PublicationContentItem> ITEMS = new ArrayList<PublicationContentItem>();
    public static final List<QualityTag.QualityTagItem> CONTRACT_ITEMS = new ArrayList<QualityTag.QualityTagItem>();
    public static final Map<String, QualityTag.QualityTagItem> CONTRACT_ITEM_MAP = new HashMap<String, QualityTag.QualityTagItem>();


    public static void addContentItem(PublicationContentItem item) {
        ITEMS.add(item);
    }

    public static void addContractItem(QualityTag.QualityTagItem item) {
        CONTRACT_ITEMS.add(item);
        CONTRACT_ITEM_MAP.put(item.name, item);
    }

    public static void clearItems() {
        ITEMS.clear();
    }

    public static void clearContractItems() {
        CONTRACT_ITEMS.clear();
    }

    public static class QualityTag {

        public static class QualityTagItem {
            public final String name;
            public final String description;
            public final long numPosts;
            public final String admin;

            public QualityTagItem(String name, String description, long numPosts, String admin) {
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
