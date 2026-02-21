package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.test.core.app.ApplicationProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class CacheableWebViewTest {
  private Context context;
  private CacheableWebView webView;
  private ShadowConnectivityManager shadowConnectivityManager;

  @Before
  public void setUp() {
    context = ApplicationProvider.getApplicationContext();
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    shadowConnectivityManager = shadowOf(connectivityManager);
    // Ensure connected by default
    setConnected(true);
    webView = new CacheableWebView(context);
  }

  private void setConnected(boolean connected) {
    NetworkInfo networkInfo;
    if (connected) {
      networkInfo =
          ShadowNetworkInfo.newInstance(
              NetworkInfo.DetailedState.CONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, true);
    } else {
      networkInfo =
          ShadowNetworkInfo.newInstance(
              NetworkInfo.DetailedState.DISCONNECTED,
              ConnectivityManager.TYPE_WIFI,
              0,
              true,
              false);
    }
    shadowConnectivityManager.setActiveNetworkInfo(networkInfo);
  }

  @Test
  public void testFileAccessDisabledByDefault() {
    // Initially it should be false (after fix)
    // Before fix, this fails. I'm writing the test assuming the fix is applied or to verify current
    // behavior.
    // For TDD, I should assert the desired state.
    assertFalse(
        "File access should be disabled by default", webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testFileAccessDisabledWhenOnline() {
    setConnected(true);
    String url = "https://example.com";
    webView.loadUrl(url);
    assertFalse(
        "File access should be disabled when loading online content",
        webView.getSettings().getAllowFileAccess());
  }

  @Test
  public void testFileAccessEnabledWhenOfflineAndCached()
      throws IOException, NoSuchAlgorithmException {
    String url = "https://example.com";
    createCacheFileForUrl(url);
    setConnected(false); // Offline

    webView.loadUrl(url);

    assertTrue(
        "File access should be enabled when loading cached content",
        webView.getSettings().getAllowFileAccess());
  }

  private void createCacheFileForUrl(String url) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    String name = hexString.toString();
    File cacheDir = context.getApplicationContext().getCacheDir();
    File cacheFile = new File(cacheDir, "webarchive-" + name + ".mht");
    cacheFile.createNewFile();
  }
}
