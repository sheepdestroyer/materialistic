package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.webkit.WebResourceResponse;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.reactivex.rxjava3.schedulers.Schedulers;

@RunWith(RobolectricTestRunner.class)
public class AdBlockerTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        // Use trampoline scheduler to ensure synchronous execution of the initialization
        AdBlocker.init(context, Schedulers.trampoline());
    }

    @Test
    public void testIsAd_ExactMatch() {
        // "101com.com" is from the pgl.yoyo.org.txt file
        assertTrue(AdBlocker.isAd("http://101com.com"));
        assertTrue(AdBlocker.isAd("https://101com.com/path/to/resource"));
    }

    @Test
    public void testIsAd_SubdomainMatch() {
        // "101com.com" is blocked, so subdomains should also be blocked
        assertTrue(AdBlocker.isAd("http://sub.101com.com"));
        assertTrue(AdBlocker.isAd("https://a.b.c.101com.com/index.html"));
    }

    @Test
    public void testIsAd_NotAd() {
        // Known safe domains
        assertFalse(AdBlocker.isAd("http://google.com"));
        assertFalse(AdBlocker.isAd("https://github.com/sheepdestroyer/materialisheep"));
    }

    @Test
    public void testIsAd_InvalidUrl() {
        assertFalse(AdBlocker.isAd(""));
        assertFalse(AdBlocker.isAd("not a valid url"));
        // HttpUrl.parse returns null for null strings, so let's verify AdBlocker handles it gracefully (or throws NPE if intended).
        // Since the current code has HttpUrl.parse(url) and does not do null checks on url initially,
        // passing null throws NullPointerException in HttpUrl.parse.
        // We will just test empty and invalid formats here.
    }

    @Test
    public void testCreateEmptyResource() throws Exception {
        WebResourceResponse response = AdBlocker.createEmptyResource();
        assertNotNull(response);
        assertEquals("text/plain", response.getMimeType());
        assertEquals("utf-8", response.getEncoding());

        // Read the input stream to verify it's empty
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getData(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        assertEquals("", sb.toString());
    }
}
