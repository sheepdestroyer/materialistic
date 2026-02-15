package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.text.format.DateUtils;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AppUtilsTest {
  @Test
  public void testHasConnection() {
    Context context = ApplicationProvider.getApplicationContext();
    android.net.ConnectivityManager connectivityManager =
        (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    org.robolectric.shadows.ShadowConnectivityManager shadowConnectivityManager =
        org.robolectric.Shadows.shadowOf(connectivityManager);

    // Test with no connection
    shadowConnectivityManager.setActiveNetworkInfo(null);
    assertFalse(AppUtils.hasConnection(context));

    // Test with connection
    shadowConnectivityManager.setActiveNetworkInfo(
        org.robolectric.shadows.ShadowNetworkInfo.newInstance(
            android.net.NetworkInfo.DetailedState.CONNECTED,
            android.net.ConnectivityManager.TYPE_WIFI,
            0,
            true,
            true));
    assertTrue(AppUtils.hasConnection(context));

    // Test with disconnected network
    shadowConnectivityManager.setActiveNetworkInfo(
        org.robolectric.shadows.ShadowNetworkInfo.newInstance(
            android.net.NetworkInfo.DetailedState.DISCONNECTED,
            android.net.ConnectivityManager.TYPE_WIFI,
            0,
            true,
            false));
    assertFalse(AppUtils.hasConnection(context));
  }

  @Test
  public void testGetAbbreviatedTimeSpan() {
    long now = System.currentTimeMillis();

    // Test minutes
    assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(now));
    assertEquals("5m", AppUtils.getAbbreviatedTimeSpan(now - 5 * DateUtils.MINUTE_IN_MILLIS));

    // Test hours
    assertEquals("1h", AppUtils.getAbbreviatedTimeSpan(now - DateUtils.HOUR_IN_MILLIS));
    assertEquals("2h", AppUtils.getAbbreviatedTimeSpan(now - 2 * DateUtils.HOUR_IN_MILLIS));

    // Test days
    assertEquals("1d", AppUtils.getAbbreviatedTimeSpan(now - DateUtils.DAY_IN_MILLIS));
    assertEquals("6d", AppUtils.getAbbreviatedTimeSpan(now - 6 * DateUtils.DAY_IN_MILLIS));

    // Test weeks
    assertEquals("1w", AppUtils.getAbbreviatedTimeSpan(now - DateUtils.WEEK_IN_MILLIS));
    assertEquals("51w", AppUtils.getAbbreviatedTimeSpan(now - 51 * DateUtils.WEEK_IN_MILLIS));

    // Test years
    assertEquals("1y", AppUtils.getAbbreviatedTimeSpan(now - DateUtils.YEAR_IN_MILLIS));
    assertEquals("5y", AppUtils.getAbbreviatedTimeSpan(now - 5 * DateUtils.YEAR_IN_MILLIS));

    // Test future time
    assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(now + DateUtils.MINUTE_IN_MILLIS));
  }
}
