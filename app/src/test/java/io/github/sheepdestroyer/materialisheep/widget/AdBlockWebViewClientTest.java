package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class AdBlockWebViewClientTest {

  private AdBlockWebViewClient client;
  private WebView webView;
  private Context context;

  @Before
  public void setUp() {
    context = RuntimeEnvironment.application;
    webView = new WebView(context);
    client = new AdBlockWebViewClient(false);
  }

  @Test
  public void testShouldOverrideUrlLoading_BlocksArbitraryFile() {
    WebResourceRequest request = mock(WebResourceRequest.class);
    when(request.getUrl()).thenReturn(Uri.parse("file:///etc/hosts"));

    // Expect true (blocked) for arbitrary file access
    assertTrue(
        "Should block arbitrary file:// URLs", client.shouldOverrideUrlLoading(webView, request));
  }

  @Test
  public void testShouldOverrideUrlLoading_AllowsAssets() {
    WebResourceRequest request = mock(WebResourceRequest.class);
    when(request.getUrl()).thenReturn(Uri.parse("file:///android_asset/pdf/index.html"));

    assertFalse(
        "Should allow file:///android_asset/", client.shouldOverrideUrlLoading(webView, request));
  }

  @Test
  public void testShouldOverrideUrlLoading_AllowsCache() {
    File cacheDir = context.getApplicationContext().getCacheDir();
    String cachePath = "file://" + cacheDir.getAbsolutePath() + "/test.mht";
    WebResourceRequest request = mock(WebResourceRequest.class);
    when(request.getUrl()).thenReturn(Uri.parse(cachePath));

    assertFalse(
        "Should allow cache directory files", client.shouldOverrideUrlLoading(webView, request));
  }

  @Test
  public void testShouldOverrideUrlLoading_AllowsHttp() {
    WebResourceRequest request = mock(WebResourceRequest.class);
    when(request.getUrl()).thenReturn(Uri.parse("http://example.com"));

    assertFalse("Should allow http:// URLs", client.shouldOverrideUrlLoading(webView, request));
  }
}
