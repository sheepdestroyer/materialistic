package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.AssetManager;
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
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}) // Use a specific SDK version for consistency
public class AdBlockerTest {

  @Mock Context context;

  @Mock AssetManager assetManager;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    when(context.getAssets()).thenReturn(assetManager);

    // Clear static AD_HOSTS using reflection
    Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
    field.setAccessible(true);
    ((Set) field.get(null)).clear();
  }

  @Test
  public void testIsAd() throws IOException {
    String adHostsContent = "example.com\nads.google.com";
    InputStream inputStream =
        new ByteArrayInputStream(adHostsContent.getBytes(StandardCharsets.UTF_8));
    when(assetManager.open(anyString())).thenReturn(inputStream);

    AdBlocker.init(context, Schedulers.trampoline());

    assertTrue("example.com should be blocked", AdBlocker.isAd("http://example.com"));
    assertTrue(
        "sub.example.com should be blocked (subdomain)", AdBlocker.isAd("http://sub.example.com"));
    assertTrue(
        "ads.google.com should be blocked", AdBlocker.isAd("https://ads.google.com/foo/bar"));

    assertFalse("google.com should not be blocked", AdBlocker.isAd("http://google.com"));
    assertFalse("other.com should not be blocked", AdBlocker.isAd("http://other.com"));
  }

  @Test
  public void testEmptyInit() throws IOException {
    String adHostsContent = "";
    InputStream inputStream =
        new ByteArrayInputStream(adHostsContent.getBytes(StandardCharsets.UTF_8));
    when(assetManager.open(anyString())).thenReturn(inputStream);

    AdBlocker.init(context, Schedulers.trampoline());

    assertFalse("Nothing should be blocked", AdBlocker.isAd("http://example.com"));
  }
}
