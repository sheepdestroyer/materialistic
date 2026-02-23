package io.github.sheepdestroyer.materialisheep;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;

import io.github.sheepdestroyer.materialisheep.data.FileDownloader;
import io.github.sheepdestroyer.materialisheep.data.ItemManager;
import io.github.sheepdestroyer.materialisheep.data.ReadabilityClient;
import io.github.sheepdestroyer.materialisheep.data.WebItem;
import io.github.sheepdestroyer.materialisheep.widget.PopupMenu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class WebFragmentTest {
    @Mock
    private ApplicationComponent applicationComponent;
    @Mock
    private ItemManager itemManager;
    @Mock
    private PopupMenu popupMenu;
    @Mock
    private ReadabilityClient readabilityClient;
    @Mock
    private FileDownloader fileDownloader;
    @Mock
    private WebItem webItem;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        MaterialisticApplication application = (MaterialisticApplication) ApplicationProvider.getApplicationContext();
        application.applicationComponent = applicationComponent;

        doAnswer(invocation -> {
            WebFragment fragment = invocation.getArgument(0);
            fragment.mItemManager = itemManager;
            fragment.mPopupMenu = popupMenu;
            fragment.mReadabilityClient = readabilityClient;
            fragment.mFileDownloader = fileDownloader;
            return null;
        }).when(applicationComponent).inject(any(WebFragment.class));

        when(webItem.getUrl()).thenReturn("http://example.com");
        when(webItem.getId()).thenReturn("123");
    }

    @Test
    public void testJavascriptInterfaceRemovedOnNavigation() {
        Bundle args = new Bundle();
        args.putParcelable(WebFragment.EXTRA_ITEM, webItem);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);

        ActivityController<AppCompatActivity> controller = Robolectric.buildActivity(AppCompatActivity.class);
        AppCompatActivity activity = controller.create().start().resume().visible().get();

        activity.getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow();

        WebView webView = fragment.mWebView;
        assertNotNull("WebView should be initialized", webView);

        // 1. Manually add the interface to simulate previous PDF state
        webView.addJavascriptInterface(new Object(), "PdfAndroidJavascriptBridge");

        // Verify it exists
        ShadowWebView shadowWebView = shadowOf(webView);
        assertNotNull("Interface should exist initially", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));

        // 2. Get the WebViewClient
        WebViewClient client = shadowWebView.getWebViewClient();
        assertNotNull("WebViewClient should be set", client);

        // 3. Simulate onPageStarted for a non-PDF URL (e.g. malicious site)
        client.onPageStarted(webView, "http://malicious.com", null);

        // 4. Verify interface is removed
        assertNull("JavascriptInterface should be removed on navigation to untrusted URL",
                shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));
    }
}
