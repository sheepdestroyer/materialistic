package io.github.sheepdestroyer.materialisheep;

import android.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SecureLoggerTest {

    @Test
    public void testLog_redactsPassword() {
        SecureLogger logger = new SecureLogger("TestTag");
        String message = "acct=user&pw=secret123&goto=news";

        logger.log(message);

        List<ShadowLog.LogItem> logs = ShadowLog.getLogsForTag("TestTag");
        assertEquals(1, logs.size());
        assertEquals("acct=user&pw=*****&goto=news", logs.get(0).msg);
    }

    @Test
    public void testLog_redactsPasswordAtEnd() {
        SecureLogger logger = new SecureLogger("TestTag");
        String message = "acct=user&pw=secret123";

        logger.log(message);

        List<ShadowLog.LogItem> logs = ShadowLog.getLogsForTag("TestTag");
        assertEquals(1, logs.size());
        assertEquals("acct=user&pw=*****", logs.get(0).msg);
    }

    @Test
    public void testLog_doesNotRedactOtherFields() {
        SecureLogger logger = new SecureLogger("TestTag");
        String message = "acct=user&other=value";

        logger.log(message);

        List<ShadowLog.LogItem> logs = ShadowLog.getLogsForTag("TestTag");
        assertEquals(1, logs.size());
        assertEquals("acct=user&other=value", logs.get(0).msg);
    }
}
