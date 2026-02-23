package io.github.sheepdestroyer.materialisheep;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Set;
import android.webkit.WebResourceResponse;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class AdBlockerTest {

    @Before
    public void setUp() throws Exception {
        // Reset AD_HOSTS using reflection to ensure clean state
        Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
        field.setAccessible(true);
        Set<String> hosts = (Set<String>) field.get(null);
        hosts.clear();
    }

    @Test
    public void testInitLoadsHosts() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        // Use trampoline scheduler to run synchronously on the test thread
        AdBlocker.init(context, Schedulers.trampoline());

        // Verify hosts are loaded
        Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
        field.setAccessible(true);
        Set<String> hosts = (Set<String>) field.get(null);

        // pgl.yoyo.org.txt has ~3000 lines
        assertTrue("Hosts set should not be empty after init", hosts.size() > 0);
        assertTrue("Should contain common ad host", hosts.contains("doubleclick.net"));
    }

    @Test
    public void testIsAd() throws Exception {
        // Manually populate AD_HOSTS to test logic independently of asset loading
        Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
        field.setAccessible(true);
        Set<String> hosts = (Set<String>) field.get(null);
        hosts.add("example.com");
        hosts.add("ads.google.com");

        // Direct match
        assertTrue(AdBlocker.isAd("http://example.com"));
        assertTrue(AdBlocker.isAd("https://example.com/some/path"));

        // Subdomain match (recursive logic)
        assertTrue(AdBlocker.isAd("http://sub.example.com"));
        assertTrue(AdBlocker.isAd("http://a.b.c.example.com"));

        // Partial match that should fail
        assertFalse(AdBlocker.isAd("http://google.com")); // only ads.google.com is blocked
        assertFalse(AdBlocker.isAd("http://notanad.com"));

        // Empty/Null checks
        assertFalse(AdBlocker.isAd(""));
        assertFalse(AdBlocker.isAd(null));
    }

    @Test
    public void testCreateEmptyResource() throws IOException {
        WebResourceResponse response = AdBlocker.createEmptyResource();
        assertNotNull(response);
        assertEquals("text/plain", response.getMimeType());
        assertEquals("utf-8", response.getEncoding());

        InputStream data = response.getData();
        assertNotNull(data);
        assertEquals(0, data.available());

        // Verify multiple calls return valid responses (and potentially check for instance reuse if applicable later)
        WebResourceResponse response2 = AdBlocker.createEmptyResource();
        assertNotNull(response2);
        assertNotSame(response, response2); // Different response objects

        // But the underlying stream content should be empty
        assertEquals(0, response2.getData().available());
    }
}
