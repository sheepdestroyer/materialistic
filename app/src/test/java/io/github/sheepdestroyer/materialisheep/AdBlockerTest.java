package io.github.sheepdestroyer.materialisheep;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class AdBlockerTest {

    private Context context;
    private AssetManager assetManager;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        assetManager = mock(AssetManager.class);
        when(context.getAssets()).thenReturn(assetManager);

        // Reset AD_HOSTS using reflection because it's static
        resetAdHosts();
    }

    @After
    public void tearDown() throws Exception {
        resetAdHosts();
    }

    private void resetAdHosts() throws Exception {
        Field field = AdBlocker.class.getDeclaredField("AD_HOSTS");
        field.setAccessible(true);
        Set<String> hosts = (Set<String>) field.get(null);
        if (hosts != null) {
            hosts.clear();
        }
    }

    @Test
    public void testInitAndIsAd() throws IOException {
        String adHostsContent = "example.com\nad.doubleclick.net";
        when(assetManager.open(anyString())).thenReturn(new ByteArrayInputStream(adHostsContent.getBytes(StandardCharsets.UTF_8)));

        AdBlocker.init(context, Schedulers.trampoline());

        assertTrue("Should block example.com", AdBlocker.isAd("http://example.com/foo"));
        assertTrue("Should block ad.doubleclick.net", AdBlocker.isAd("https://ad.doubleclick.net/bar"));
        assertFalse("Should not block google.com", AdBlocker.isAd("https://google.com"));

        // Test subdomain blocking
        assertTrue("Should block sub.example.com", AdBlocker.isAd("http://sub.example.com"));
    }

    @Test
    public void testIsAdEmpty() {
        assertFalse(AdBlocker.isAd("http://example.com"));
    }
}
