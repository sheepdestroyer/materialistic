package io.github.sheepdestroyer.materialisheep;

import android.os.Bundle;
import android.webkit.WebViewClient;
import androidx.fragment.app.FragmentActivity;
import io.github.sheepdestroyer.materialisheep.data.FileDownloader;
import io.github.sheepdestroyer.materialisheep.data.ItemManager;
import io.github.sheepdestroyer.materialisheep.data.ReadabilityClient;
import io.github.sheepdestroyer.materialisheep.data.WebItem;
import io.github.sheepdestroyer.materialisheep.widget.PopupMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowWebView;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
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

    private MaterialisticApplication application;
    private WebFragment fragment;
    private ShadowWebView shadowWebView;
    private Object bridge;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        application = (MaterialisticApplication) RuntimeEnvironment.application;
        application.applicationComponent = applicationComponent;

        doAnswer(invocation -> {
            WebFragment f = invocation.getArgument(0);
            f.mItemManager = itemManager;
            f.mPopupMenu = popupMenu;
            f.mReadabilityClient = readabilityClient;
            f.mFileDownloader = fileDownloader;
            return null;
        }).when(applicationComponent).inject(any(WebFragment.class));

        // Create fragment
        Bundle args = new Bundle();
        WebItem item = mock(WebItem.class);
        when(item.getUrl()).thenReturn("http://example.com");
        args.putParcelable(WebFragment.EXTRA_ITEM, item);
        args.putBoolean(LazyLoadFragment.EXTRA_EAGER_LOAD, true);

        fragment = new WebFragment();
        fragment.setArguments(args);

        ActivityController<FragmentActivity> controller = Robolectric.buildActivity(FragmentActivity.class);
        FragmentActivity activity = controller.create().start().resume().get();

        activity.getSupportFragmentManager().beginTransaction()
                .add(fragment, "web_fragment")
                .commitNow();

        // Ensure view is created
        if (fragment.getView() == null) {
            throw new RuntimeException("Fragment view not created");
        }

        shadowWebView = Shadows.shadowOf(fragment.mWebView);

        // Setup bridge
        Class<?> bridgeClass = Class.forName("io.github.sheepdestroyer.materialisheep.WebFragment$PdfAndroidJavascriptBridge");
        java.lang.reflect.Constructor<?> constructor = bridgeClass.getDeclaredConstructor(String.class, Class.forName("io.github.sheepdestroyer.materialisheep.WebFragment$PdfAndroidJavascriptBridge$Callbacks"));
        constructor.setAccessible(true);
        bridge = constructor.newInstance("test.pdf", null);

        // Set field
        Field bridgeField = WebFragment.class.getDeclaredField("mPdfAndroidJavascriptBridge");
        bridgeField.setAccessible(true);
        bridgeField.set(fragment, bridge);

        // Add to WebView
        fragment.mWebView.addJavascriptInterface(bridge, "PdfAndroidJavascriptBridge");
    }

    @Test
    public void testBridgeRemovedOnNavigation() {
        assertNotNull("Bridge should be present initially", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));

        // Navigate to attacker site
        WebViewClient client = Shadows.shadowOf(fragment.mWebView).getWebViewClient();
        client.onPageStarted(fragment.mWebView, "http://attacker.com", null);

        // It should be removed
        assertNull("Bridge should be removed on navigation", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));
    }

    @Test
    public void testBridgeRetainedOnPdfLoader() {
        assertNotNull("Bridge should be present initially", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));

        // Navigate to PDF loader
        WebViewClient client = Shadows.shadowOf(fragment.mWebView).getWebViewClient();
        client.onPageStarted(fragment.mWebView, WebFragment.PDF_LOADER_URL, null);

        // It should be retained
        assertNotNull("Bridge should be retained on PDF loader", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));
    }

    @Test
    public void testBridgeRetainedOnAboutBlank() {
        assertNotNull("Bridge should be present initially", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));

        // Navigate to about:blank
        WebViewClient client = Shadows.shadowOf(fragment.mWebView).getWebViewClient();
        client.onPageStarted(fragment.mWebView, "about:blank", null);

        // It should be retained
        assertNotNull("Bridge should be retained on about:blank", shadowWebView.getJavascriptInterface("PdfAndroidJavascriptBridge"));
    }
}
