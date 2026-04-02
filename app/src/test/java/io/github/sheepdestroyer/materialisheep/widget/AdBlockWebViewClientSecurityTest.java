package io.github.sheepdestroyer.materialisheep.widget;

import android.content.Context;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class AdBlockWebViewClientSecurityTest {
    private AdBlockWebViewClient client;
    private WebView webView;

    @Before
    public void setUp() {
        client = new AdBlockWebViewClient(true);
        webView = mock(WebView.class);
        when(webView.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testFileAccessValidation() throws Exception {
        // Valid cache file
        File cacheFile = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "test.mht");
        WebResourceResponse response1 = client.shouldInterceptRequest(webView, "file://" + cacheFile.getAbsolutePath());
        assertNull("Valid cache file should not be blocked", response1);

        // Valid asset file
        WebResourceResponse response2 = client.shouldInterceptRequest(webView, "file:///android_asset/pdf/index.html");
        assertNull("Valid asset file should not be blocked", response2);

        // Invalid file (e.g. attempting to read shared prefs)
        File sensitiveFile = new File(ApplicationProvider.getApplicationContext().getApplicationInfo().dataDir, "shared_prefs/prefs.xml");
        WebResourceResponse response3 = client.shouldInterceptRequest(webView, "file://" + sensitiveFile.getAbsolutePath());
        assertNotNull("Invalid file access should be blocked", response3);

        // Path traversal attack
        WebResourceResponse response4 = client.shouldInterceptRequest(webView, "file:///android_asset/../databases/secret.db");
        assertNotNull("Path traversal should be blocked", response4);
    }
}
