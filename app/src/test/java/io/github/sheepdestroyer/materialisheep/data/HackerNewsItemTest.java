package io.github.sheepdestroyer.materialisheep.data;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class HackerNewsItemTest {

    @Test
    public void testParcelable() {
        HackerNewsItem item = new HackerNewsItem(1L);
        item.setFavorite(true);
        item.setIsViewed(true);
        item.setLocalRevision(5);
        item.setCollapsed(true);
        item.setContentExpanded(true);
        item.setLastKidCount(10);
        item.incrementScore(); // score +1, voted=true, pendingVoted=true

        Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        HackerNewsItem parceled = HackerNewsItem.CREATOR.createFromParcel(parcel);

        assertEquals(item.getId(), parceled.getId());
        assertEquals(item.isFavorite(), parceled.isFavorite());
        assertEquals(item.isViewed(), parceled.isViewed());
        assertEquals(item.getLocalRevision(), parceled.getLocalRevision());
        assertEquals(item.isCollapsed(), parceled.isCollapsed());
        assertEquals(item.isContentExpanded(), parceled.isContentExpanded());
        assertEquals(item.getLastKidCount(), parceled.getLastKidCount());
        assertEquals(item.getScore(), parceled.getScore());
        assertEquals(item.isVoted(), parceled.isVoted());
        assertEquals(item.isPendingVoted(), parceled.isPendingVoted());
    }

    @Test
    public void testEqualsAndHashCode() {
        HackerNewsItem item1 = new HackerNewsItem(1L);
        HackerNewsItem item2 = new HackerNewsItem(1L);
        HackerNewsItem item3 = new HackerNewsItem(2L);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        org.junit.Assert.assertNotEquals(item1, item3);
        org.junit.Assert.assertNotEquals(item1.hashCode(), item3.hashCode());

        org.junit.Assert.assertNotEquals(item1, new Object());
    }
}
