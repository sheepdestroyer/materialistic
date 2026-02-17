package io.github.sheepdestroyer.materialisheep.data;

import android.content.Context;
import android.os.Parcel;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class HackerNewsItemTest {

    @Test
    public void testGetDisplayedAuthor_DoesNotAccumulateSpans() {
        // Arrange
        HackerNewsItem item = new HackerNewsItem(12345);
        TestItem source = new TestItem(12345) {
             @Override
             public String getBy() {
                 return "author";
             }
        };
        item.populate(source);
        Context context = RuntimeEnvironment.application;

        // Act & Assert
        // First call
        Spannable firstResult = item.getDisplayedAuthor(context, false, 0);
        int spansAfterFirstCall = firstResult.getSpans(0, firstResult.length(), ForegroundColorSpan.class).length;
        assertEquals("Should have 1 ForegroundColorSpan initially", 1, spansAfterFirstCall);

        // Second call
        Spannable secondResult = item.getDisplayedAuthor(context, false, 0);
        int spansAfterSecondCall = secondResult.getSpans(0, secondResult.length(), ForegroundColorSpan.class).length;
        assertEquals("Should still have 1 ForegroundColorSpan after second call", 1, spansAfterSecondCall);

        // Multiple calls
        for (int i = 0; i < 100; i++) {
             item.getDisplayedAuthor(context, false, 0);
        }
        Spannable finalResult = item.getDisplayedAuthor(context, false, 0);
        int spansAfterManyCalls = finalResult.getSpans(0, finalResult.length(), ForegroundColorSpan.class).length;
        assertEquals("Should still have 1 ForegroundColorSpan after many calls", 1, spansAfterManyCalls);
    }

    // Minimal Item implementation for populating HackerNewsItem
    static class TestItem implements Item {
        private final long id;
        TestItem(long id) { this.id = id; }

        @Override public String getBy() { return "author"; }
        @Override public String getTitle() { return "title"; }
        @Override public long getTime() { return 0; }
        @Override public long[] getKids() { return new long[0]; }
        @Override public String getRawUrl() { return "http://example.com"; }
        @Override public String getText() { return "text"; }
        @Override public android.text.Spannable getDisplayedAuthor(android.content.Context context, boolean linkify, int color) { return null; }
        @Override public CharSequence getDisplayedText() { return null; }
        @Override public String getRawType() { return "story"; }
        @Override public int getDescendants() { return 0; }
        @Override public String getParent() { return "0"; }
        @Override public boolean isDeleted() { return false; }
        @Override public boolean isDead() { return false; }
        @Override public int getScore() { return 0; }
        @Override public boolean isViewed() { return false; }
        @Override public boolean isFavorite() { return false; }

        @Override public String getId() { return String.valueOf(id); }
        @Override public long getLongId() { return id; }
        @Override public String getUrl() { return "http://example.com"; }
        @Override public String getDisplayedTitle() { return "title"; }
        @Override public String getType() { return "story"; }
        @Override public android.text.Spannable getDisplayedTime(android.content.Context context) { return null; }
        @Override public int getKidCount() { return 0; }
        @Override public int getLastKidCount() { return 0; }
        @Override public void setLastKidCount(int lastKidCount) {}
        @Override public boolean hasNewKids() { return false; }
        @Override public String getSource() { return "example.com"; }
        @Override public Item[] getKidItems() { return new Item[0]; }
        @Override public boolean isStoryType() { return true; }
        @Override public void setFavorite(boolean favorite) {}
        @Override public int getLocalRevision() { return 0; }
        @Override public void setLocalRevision(int localRevision) {}
        @Override public void setIsViewed(boolean isViewed) {}
        @Override public int getLevel() { return 0; }
        @Override public Item getParentItem() { return null; }
        @Override public void incrementScore() {}
        @Override public boolean isVoted() { return false; }
        @Override public boolean isPendingVoted() { return false; }
        @Override public void clearPendingVoted() {}
        @Override public boolean isCollapsed() { return false; }
        @Override public void setCollapsed(boolean collapsed) {}
        @Override public int getRank() { return 0; }
        @Override public boolean isContentExpanded() { return false; }
        @Override public void setContentExpanded(boolean expanded) {}
        @Override public long getNeighbour(int direction) { return 0; }
        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(Parcel dest, int flags) {}
        @Override public void populate(Item info) {}
    }
}
