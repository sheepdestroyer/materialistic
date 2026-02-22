package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Use a recent SDK
public class CacheableWebViewTest {

  private CacheableWebView webView;
  private Context context;
  private ShadowConnectivityManager shadowConnectivityManager;

  @Before
  public void setUp() {
    context = RuntimeEnvironment.application;
    shadowConnectivityManager =
        shadowOf((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    webView = new CacheableWebView(context);
  }

  @Test
  public void testDefaultFileAccess() {
    // Currently true by default (before fix), we expect this to fail after we add the assertion for
    // false.
    // But for TDD, let's assert what we WANT (false).
    // If the code is not fixed yet, this test should fail.
    assertFalse(
        "File access should be disabled by default", webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testReloadUrlNormalUrlDisablesFileAccess() {
    webView.getSettings().setAllowFileAccess(true); // Set to true to verify it gets disabled
    webView.reloadUrl("http://example.com");
    assertFalse(
        "File access should be disabled for http URLs", webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testReloadUrlFileSchemeDisablesFileAccess() {
    webView.getSettings().setAllowFileAccess(true);
    webView.reloadUrl("file:///"); // Constant FILE in WebView
    assertFalse(
        "File access should be disabled for file:/// scheme",
        webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testReloadUrlBlankSchemeDisablesFileAccess() {
    webView.getSettings().setAllowFileAccess(true);
    webView.reloadUrl("about:blank"); // Constant BLANK in WebView
    assertFalse(
        "File access should be disabled for about:blank",
        webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testReloadUrlCachedFileEnablesFileAccess() throws Exception {
    // 1. Set offline
    shadowConnectivityManager.setActiveNetworkInfo(null);

    // 2. Create a dummy cache file
    // Logic from CacheableWebView.generateCacheFilename
    String url = "http://cached.com/article";
    java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(url.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    String name = hexString.toString();
    String cacheFileName =
        context.getCacheDir().getAbsolutePath() + File.separator + "webarchive-" + name + ".mht";
    File cacheFile = new File(cacheFileName);
    // Create the file
    if (!cacheFile.getParentFile().exists()) {
      cacheFile.getParentFile().mkdirs();
    }
    cacheFile.createNewFile();

    // 3. Reload URL
    // Initially set to false
    webView.getSettings().setAllowFileAccess(false);
    webView.reloadUrl(url);

    // Simulate completion of about:blank load, which triggers the actual reload
    // Since WebView wraps the client, we might need to access the wrapper or just trigger it on the
    // view if possible.
    // Robolectric's shadowOf(webView).getWebViewClient() returns the client set on the WebView.
    // CacheableWebView sets a client in init().
    shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");

    // 4. Assert
    assertTrue(
        "File access should be enabled for cached file",
        webView.getSettings().getAllowFileAccess());

    // Clean up
    cacheFile.delete();
  }
}
