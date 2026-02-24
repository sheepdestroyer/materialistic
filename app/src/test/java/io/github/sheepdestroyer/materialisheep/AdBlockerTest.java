package io.github.sheepdestroyer.materialisheep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AdBlockerTest {

    @Before
    public void setUp() throws Exception {
        // Clear AD_HOSTS using reflection
        Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
        field.setAccessible(true);
        Set<String> adHosts = (Set<String>) field.get(null);
        adHosts.clear();
        adHosts.add("doubleclick.net");
        adHosts.add("ad.google.com");
    }

    @Test
    public void testIsAd() {
        assertTrue(AdBlocker.isAd("http://doubleclick.net"));
        assertTrue(AdBlocker.isAd("https://doubleclick.net"));
        assertTrue(AdBlocker.isAd("http://ad.doubleclick.net"));
        assertTrue(AdBlocker.isAd("http://sub.ad.doubleclick.net"));

        assertTrue(AdBlocker.isAd("http://ad.google.com"));
        assertTrue(AdBlocker.isAd("http://ads.ad.google.com"));

        assertFalse(AdBlocker.isAd("http://google.com"));
        assertFalse(AdBlocker.isAd("http://example.com"));
        assertFalse(AdBlocker.isAd("http://net"));
        assertFalse(AdBlocker.isAd(""));
    }
}
