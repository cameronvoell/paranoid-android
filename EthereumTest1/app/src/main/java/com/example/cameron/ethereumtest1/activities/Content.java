package com.example.cameron.ethereumtest1.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ContentItem> ITEMS = new ArrayList<ContentItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ContentItem> ITEM_MAP = new HashMap<String, ContentItem>();

    private static final int COUNT = 25;

    public static void addItem(ContentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    public static void clear() {
        ITEMS.clear();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ContentItem {
        public final String id;
        public final String title;

        public ContentItem(String id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
