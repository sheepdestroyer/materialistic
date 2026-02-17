package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertFalse;

import android.app.Application;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34) // Robolectric supports up to 34 currently, checking if 36 works might fail
public class CacheableWebViewTest {
  private CacheableWebView webView;

  @Before
  public void setUp() {
    Application application = ApplicationProvider.getApplicationContext();
    webView = new CacheableWebView(application);
  }

  @Test
  public void testFileAccessDisabledByDefault() {
    assertFalse(webView.getSettings().getAllowFileAccess());
    assertFalse(webView.getSettings().getAllowFileAccessFromFileURLs());
    assertFalse(webView.getSettings().getAllowUniversalAccessFromFileURLs());
  }
}
