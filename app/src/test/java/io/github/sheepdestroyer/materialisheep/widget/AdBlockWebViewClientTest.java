package io.github.sheepdestroyer.materialisheep.widget;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;

import io.github.sheepdestroyer.materialisheep.AdBlocker;

@RunWith(RobolectricTestRunner.class)
public class AdBlockWebViewClientTest {

    private AdBlockWebViewClient client;
    private WebView mockWebView;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
        client = new AdBlockWebViewClient(true);
        mockWebView = mock(WebView.class);
        when(mockWebView.getContext()).thenReturn(context);
    }

    @Test
    public void testFileAccessBlocked() {
        WebResourceResponse response = client.shouldInterceptRequest(mockWebView, "file:///etc/passwd");
        assertNotNull(response);
        assertEquals("text/plain", response.getMimeType());
    }

    @Test
    public void testCacheAccessAllowed() throws IOException {
        File cacheDir = context.getCacheDir();
        File cacheFile = new File(cacheDir, "webarchive-1234.mht");
        cacheFile.createNewFile();

        WebResourceResponse response = client.shouldInterceptRequest(mockWebView, "file://" + cacheFile.getAbsolutePath());
        assertNotNull(response);
        assertEquals("multipart/related", response.getMimeType());
    }
}
