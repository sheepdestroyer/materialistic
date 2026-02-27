package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import androidx.test.core.app.ApplicationProvider;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdBlockWebViewClientTest {
  @Mock private WebView webView;
  @Mock private WebResourceRequest request;

  private Context context;
  private AdBlockWebViewClient client;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    context = ApplicationProvider.getApplicationContext();
  }

  @Test
  public void testBlockFileUrl() {
    client = new AdBlockWebViewClient(context, true);

    String unsafeUrl = "file:///etc/hosts";
    when(request.getUrl()).thenReturn(Uri.parse(unsafeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNotNull("Should block unsafe file URL", response);
  }

  @Test
  public void testAllowAssetUrl() {
    client = new AdBlockWebViewClient(context, true);

    String safeUrl = "file:///android_asset/pdf/index.html";
    when(request.getUrl()).thenReturn(Uri.parse(safeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNull("Should allow safe asset URL", response);
  }

  @Test
  public void testAllowCacheUrl() {
    client = new AdBlockWebViewClient(context, true);
    File cacheDir = context.getCacheDir();
    String safeUrl = "file://" + cacheDir.getAbsolutePath() + "/test.html";
    when(request.getUrl()).thenReturn(Uri.parse(safeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNull("Should allow safe cache URL", response);
  }

  @Test
  public void testBlockPathTraversalUrl() {
    client = new AdBlockWebViewClient(context, true);
    File cacheDir = context.getCacheDir();
    // Construct a path that looks like it's inside cacheDir but traverses out
    String unsafeUrl = "file://" + cacheDir.getAbsolutePath() + "/../secret.db";
    when(request.getUrl()).thenReturn(Uri.parse(unsafeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNotNull("Should block path traversal URL", response);
  }
}
