package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;
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
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class WebFragmentTest {
  @Mock private ApplicationComponent applicationComponent;
  @Mock private ItemManager itemManager;
  @Mock private PopupMenu popupMenu;
  @Mock private ReadabilityClient readabilityClient;
  @Mock private FileDownloader fileDownloader;

  private MaterialisticApplication application;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    application = ApplicationProvider.getApplicationContext();
    application.applicationComponent = applicationComponent;

    doAnswer(
            invocation -> {
              WebFragment fragment = invocation.getArgument(0);
              fragment.mItemManager = itemManager;
              fragment.mPopupMenu = popupMenu;
              fragment.mReadabilityClient = readabilityClient;
              fragment.mFileDownloader = fileDownloader;
              return null;
            })
        .when(applicationComponent)
        .inject(any(WebFragment.class));
  }

  @Test
  public void testRemoveJavascriptInterfaceOnNavigation() {
    // Create a WebItem for arguments
    WebItem item = mock(WebItem.class);
    when(item.getUrl()).thenReturn("http://example.com");
    when(item.getId()).thenReturn("123");

    Bundle args = new Bundle();
    args.putParcelable(WebFragment.EXTRA_ITEM, item);

    // Start fragment
    FragmentActivity activity =
        Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();
    WebFragment fragment = new WebFragment();
    fragment.setArguments(args);

    activity
        .getSupportFragmentManager()
        .beginTransaction()
        .add(android.R.id.content, fragment)
        .commitNow();

    // Access WebView
    WebView realWebView = fragment.mWebView;
    assertNotNull("WebView should be initialized", realWebView);

    // Spy on WebView to verify removeJavascriptInterface
    WebView spyWebView = spy(realWebView);
    ReflectionHelpers.setField(fragment, "mWebView", spyWebView);

    // Mock the bridge and set it
    // Reflection to get the class
    try {
      Class<?> bridgeClass =
          Class.forName(
              "io.github.sheepdestroyer.materialisheep.WebFragment$PdfAndroidJavascriptBridge");
      Object mockBridge = mock(bridgeClass);

      ReflectionHelpers.setField(fragment, "mPdfAndroidJavascriptBridge", mockBridge);

      // Get WebViewClient
      WebViewClient client = spyWebView.getWebViewClient(); // API 26+
      assertNotNull("WebViewClient should be set", client);

      // Trigger onPageStarted with a non-PDF URL
      client.onPageStarted(spyWebView, "http://evil.com", null);

      // Verify removeJavascriptInterface was called
      verify(spyWebView).removeJavascriptInterface("PdfAndroidJavascriptBridge");

      // Verify field is null
      Object bridgeAfter = ReflectionHelpers.getField(fragment, "mPdfAndroidJavascriptBridge");
      assertNull("Bridge should be nullified", bridgeAfter);

      // Trigger onPageStarted with PDF URL
      // Reset field
      ReflectionHelpers.setField(fragment, "mPdfAndroidJavascriptBridge", mockBridge);
      client.onPageStarted(spyWebView, WebFragment.PDF_LOADER_URL, null);

      // Verify removeJavascriptInterface was NOT called (for this invocation)
      // We need to verify counts. previous was 1. now should still be 1.
      verify(spyWebView, times(1)).removeJavascriptInterface("PdfAndroidJavascriptBridge");
      // Verify field is NOT null
      bridgeAfter = ReflectionHelpers.getField(fragment, "mPdfAndroidJavascriptBridge");
      assertNotNull("Bridge should NOT be nullified", bridgeAfter);

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
