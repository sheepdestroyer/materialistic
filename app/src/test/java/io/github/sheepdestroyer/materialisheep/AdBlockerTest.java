package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.AssetManager;
import android.webkit.WebResourceResponse;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdBlockerTest {
  @Mock private Context context;
  @Mock private AssetManager assetManager;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    when(context.getAssets()).thenReturn(assetManager);

    // Reset AD_HOSTS using reflection
    Field adHostsField = AdBlocker.class.getDeclaredField("AD_HOSTS");
    adHostsField.setAccessible(true);
    Set<String> adHosts = (Set<String>) adHostsField.get(null);
    adHosts.clear();
  }

  @Test
  public void testInitLoadsHosts() throws IOException {
    String hosts = "ad.com\ndoubleclick.net";
    InputStream stream = new ByteArrayInputStream(hosts.getBytes(StandardCharsets.UTF_8));
    when(assetManager.open(anyString())).thenReturn(stream);

    AdBlocker.init(context, Schedulers.trampoline());

    assertTrue(AdBlocker.isAd("http://ad.com"));
    assertTrue(AdBlocker.isAd("https://doubleclick.net/some/path"));
    assertFalse(AdBlocker.isAd("http://google.com"));
  }

  @Test
  public void testSubdomainMatching() throws IOException {
    String hosts = "ad.com";
    InputStream stream = new ByteArrayInputStream(hosts.getBytes(StandardCharsets.UTF_8));
    when(assetManager.open(anyString())).thenReturn(stream);

    AdBlocker.init(context, Schedulers.trampoline());

    assertTrue(AdBlocker.isAd("http://sub.ad.com"));
    assertTrue(AdBlocker.isAd("http://sub.sub.ad.com"));
    assertFalse(AdBlocker.isAd("http://notad.com"));
  }

  @Test
  public void testCreateEmptyResource() throws IOException {
    WebResourceResponse response = AdBlocker.createEmptyResource();
    assertNotNull(response);
    assertEquals("text/plain", response.getMimeType());
    assertEquals("utf-8", response.getEncoding());

    InputStream data = response.getData();
    assertNotNull(data);
    assertEquals(-1, data.read());
  }
}
