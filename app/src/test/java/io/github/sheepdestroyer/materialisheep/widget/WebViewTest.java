package io.github.sheepdestroyer.materialisheep.widget;

import android.app.Activity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)
public class WebViewTest {
    private WebView webView;
    private Activity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(Activity.class).create().get();
        webView = new WebView(activity);
    }

    @Test
    public void testReloadHtmlUsesSafeBaseUrl() {
        webView.reloadHtml("<html></html>");
        // Verify mPendingUrl is https://localhost/
        // Initially this will fail because it's currently file:///
        assertEquals("https://localhost/", webView.mPendingUrl);
        assertNotEquals("file:///", webView.mPendingUrl);
    }
}
