package io.github.sheepdestroyer.materialisheep;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import android.text.format.DateUtils;

@RunWith(RobolectricTestRunner.class)
public class AppUtilsTest {
        @Test
        public void testHasConnection() {
                Context context = ApplicationProvider.getApplicationContext();
                android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) context
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                org.robolectric.shadows.ShadowConnectivityManager shadowConnectivityManager = org.robolectric.Shadows
                                .shadowOf(connectivityManager);

                // Test with no connection
                shadowConnectivityManager.setActiveNetworkInfo(null);
                assertFalse(AppUtils.hasConnection(context));

                // Test with connection
                shadowConnectivityManager.setActiveNetworkInfo(
                                org.robolectric.shadows.ShadowNetworkInfo.newInstance(
                                                android.net.NetworkInfo.DetailedState.CONNECTED,
                                                android.net.ConnectivityManager.TYPE_WIFI, 0, true, true));
                assertTrue(AppUtils.hasConnection(context));

                // Test with disconnected network
                shadowConnectivityManager.setActiveNetworkInfo(
                                org.robolectric.shadows.ShadowNetworkInfo.newInstance(
                                                android.net.NetworkInfo.DetailedState.DISCONNECTED,
                                                android.net.ConnectivityManager.TYPE_WIFI, 0, true, false));
                assertFalse(AppUtils.hasConnection(context));
        }

        @Test
        public void testGetAbbreviatedTimeSpan() {
                long now = System.currentTimeMillis();

                // Test Years
                assertEquals("2y", AppUtils.getAbbreviatedTimeSpan(now - (2 * DateUtils.YEAR_IN_MILLIS)));

                // Test Weeks
                assertEquals("3w", AppUtils.getAbbreviatedTimeSpan(now - (3 * DateUtils.WEEK_IN_MILLIS)));

                // Test Days
                assertEquals("4d", AppUtils.getAbbreviatedTimeSpan(now - (4 * DateUtils.DAY_IN_MILLIS)));

                // Test Hours
                assertEquals("5h", AppUtils.getAbbreviatedTimeSpan(now - (5 * DateUtils.HOUR_IN_MILLIS)));

                // Test Minutes
                assertEquals("10m", AppUtils.getAbbreviatedTimeSpan(now - (10 * DateUtils.MINUTE_IN_MILLIS)));

                // Test edge case (just now / 0 difference)
                assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(now));

                // Test edge case (future time)
                assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(now + DateUtils.DAY_IN_MILLIS));
        }
}
