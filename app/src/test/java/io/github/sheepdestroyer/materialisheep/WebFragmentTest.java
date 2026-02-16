package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.github.sheepdestroyer.materialisheep.data.FileDownloader;
import io.github.sheepdestroyer.materialisheep.data.ItemManager;
import io.github.sheepdestroyer.materialisheep.data.ReadabilityClient;
import io.github.sheepdestroyer.materialisheep.data.WebItem;
import io.github.sheepdestroyer.materialisheep.widget.PopupMenu;

@RunWith(RobolectricTestRunner.class)
@Config(application = MaterialisticApplication.class)
public class WebFragmentTest {

    @Mock
    private ItemManager itemManager;
    @Mock
    private PopupMenu popupMenu;
    @Mock
    private ReadabilityClient readabilityClient;
    @Mock
    private FileDownloader fileDownloader;
    @Mock
    private ApplicationComponent applicationComponent;
    @Mock
    private WebItem webItem;

    private WebFragment webFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        MaterialisticApplication application = ApplicationProvider.getApplicationContext();
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
        when(webItem.getId()).thenReturn("123456");
    }

    @Test
    public void testJavascriptEnabledForLocalContent() {
        Bundle args = new Bundle();
        args.putParcelable(WebFragment.EXTRA_ITEM, webItem);
        webFragment = new WebFragment();
        webFragment.setArguments(args);

        AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class)
                .create().start().resume().get();

        activity.getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, webFragment, "web_fragment")
                .commitNow();

        // Simulate Readability mode loading content
        webFragment.loadContent();

        // Check settings
        assertFalse("JavaScript should be disabled for local content",
                webFragment.mWebView.getSettings().getJavaScriptEnabled());
        assertFalse("File access should be disabled",
                webFragment.mWebView.getSettings().getAllowFileAccess());
    }

    @Test
    public void testJavascriptEnabledForRemoteContent() {
        Bundle args = new Bundle();
        args.putParcelable(WebFragment.EXTRA_ITEM, webItem);
        // Force eager load to trigger load() -> loadUrl() -> setWebSettings(true)
        args.putBoolean(LazyLoadFragment.EXTRA_EAGER_LOAD, true);
        webFragment = new WebFragment();
        webFragment.setArguments(args);

        AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class)
                .create().start().resume().get();

        activity.getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, webFragment, "web_fragment")
                .commitNow();

        // Check settings (initially remote because not Readability mode)
        // loadUrl() is called via load() -> loadUrl() -> setWebSettings(true)
        // load() is triggered by eagerLoad() in onViewCreated due to EXTRA_EAGER_LOAD.

        assertTrue("JavaScript should be enabled for remote content",
                webFragment.mWebView.getSettings().getJavaScriptEnabled());
        assertFalse("File access should be disabled",
                webFragment.mWebView.getSettings().getAllowFileAccess());
    }
}
