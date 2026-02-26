package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class AdBlockWebViewClientTest {

  @Mock private WebView webView;
  @Mock private WebResourceRequest request;

  private Context context;
  private File cacheDir;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    context = RuntimeEnvironment.application;
    cacheDir = context.getCacheDir();
    // Ensure cacheDir exists
    cacheDir.mkdirs();

    when(webView.getContext()).thenReturn(context);
  }

  @Test
  public void testBlockUnsafeFileUrl() {
    // Ad blocking disabled, but security check should be active
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    String unsafeUrl = "file:///etc/passwd";

    WebResourceResponse response = client.shouldInterceptRequest(webView, unsafeUrl);
    assertNotNull("Should block unsafe file URL", response);
  }

  @Test
  public void testAllowSafeCacheUrl() {
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    String safeUrl = "file://" + cacheDir.getAbsolutePath() + "/test.html";

    WebResourceResponse response = client.shouldInterceptRequest(webView, safeUrl);
    assertNull("Should allow safe cache URL", response);
  }

  @Test
  public void testAllowAssetUrl() {
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    String safeUrl = "file:///android_asset/test.html";

    WebResourceResponse response = client.shouldInterceptRequest(webView, safeUrl);
    assertNull("Should allow asset URL", response);
  }

  @Test
  public void testBlockUnsafeFileUrlRequest() {
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    String unsafeUrl = "file:///etc/passwd";
    when(request.getUrl()).thenReturn(Uri.parse(unsafeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNotNull("Should block unsafe file URL request", response);
  }

  @Test
  public void testAllowSafeCacheUrlRequest() {
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    String safeUrl = "file://" + cacheDir.getAbsolutePath() + "/test.html";
    when(request.getUrl()).thenReturn(Uri.parse(safeUrl));

    WebResourceResponse response = client.shouldInterceptRequest(webView, request);
    assertNull("Should allow safe cache URL request", response);
  }

  @Test
  public void testBlockPathTraversalUrl() {
    AdBlockWebViewClient client = new AdBlockWebViewClient(false);
    // Path traversal: URL starts with cache dir path, but resolves to outside
    // E.g. file:///cache/../secret
    String traversalUrl = "file://" + cacheDir.getAbsolutePath() + "/../secret.db";

    WebResourceResponse response = client.shouldInterceptRequest(webView, traversalUrl);
    assertNotNull("Should block path traversal URL", response);
  }
}
