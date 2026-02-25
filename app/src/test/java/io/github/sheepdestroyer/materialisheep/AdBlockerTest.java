package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.webkit.WebResourceResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdBlockerTest {

  @Before
  public void setUp() throws Exception {
    // Clear and populate AD_HOSTS using reflection
    Field adHostsField = AdBlocker.class.getDeclaredField("AD_HOSTS");
    adHostsField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Set<String> adHosts = (Set<String>) adHostsField.get(null);
    adHosts.clear();
    adHosts.add("ad.com");
    adHosts.add("foo.bar.com");
  }

  @Test
  public void testIsAd() {
    assertTrue(AdBlocker.isAd("http://ad.com"));
    assertTrue(AdBlocker.isAd("https://ad.com/some/path"));
    assertTrue(AdBlocker.isAd("http://sub.ad.com"));
    assertTrue(AdBlocker.isAd("http://foo.bar.com"));
    assertTrue(AdBlocker.isAd("http://sub.foo.bar.com"));
    assertFalse(AdBlocker.isAd("http://safe.com"));
    assertFalse(
        AdBlocker.isAd(
            "http://bar.com")); // substring match shouldn't trigger unless it's a subdomain
  }

  @Test
  public void testIsAdNull() {
    assertFalse(AdBlocker.isAd(null));
  }

  @Test
  public void testCreateEmptyResource() throws Exception {
    WebResourceResponse response = AdBlocker.createEmptyResource();
    assertNotNull(response);
    assertEquals("text/plain", response.getMimeType());
    assertEquals("utf-8", response.getEncoding());
    InputStream data = response.getData();
    assertNotNull(data);
    assertEquals(0, data.available());
  }
}
