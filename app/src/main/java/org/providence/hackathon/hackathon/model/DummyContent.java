package org.providence.hackathon.hackathon.model;

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
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyFeedbackItem> ITEMS = new ArrayList<DummyFeedbackItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyFeedbackItem> ITEM_MAP = new HashMap<String, DummyFeedbackItem>();

    public enum FeedbackType {
        VOICE,
        PHOTO,
        TEXT,
        VIDEO
    }

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyFeedbackItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyFeedbackItem createDummyItem(int position) {
        return new DummyFeedbackItem(String.valueOf(position), getType(position), "Item " + position, makeDetails(position));
    }

    private static String getType(int position) {
        if (position % 4 == 0) {
            return FeedbackType.VIDEO.toString();
        } else if (position % 3 == 0) {
            return FeedbackType.TEXT.toString();
        } else if (position % 2 == 0) {
            return FeedbackType.PHOTO.toString();
        } else {
            return FeedbackType.VOICE.toString();
        }
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
    public static class DummyFeedbackItem {
        public final String id;
        public final String type;
        public final String content;
        public final String details;

        public DummyFeedbackItem(String id, String type, String content, String details) {
            this.id = id;
            this.type = type;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
