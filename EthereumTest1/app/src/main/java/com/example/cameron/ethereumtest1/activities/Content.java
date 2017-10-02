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

    private static ContentItem createDummyItem(int position) {
        return new ContentItem(String.valueOf(position), "Item " + position);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ContentItem {
        public final String id;
        public final String content;

        public ContentItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
