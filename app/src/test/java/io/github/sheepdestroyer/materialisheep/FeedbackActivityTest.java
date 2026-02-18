package io.github.sheepdestroyer.materialisheep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
public class FeedbackActivityTest {

  @Test
  public void testSendFeedback_launchesIntent() {
    FeedbackActivity activity =
        Robolectric.buildActivity(FeedbackActivity.class).create().start().resume().visible().get();

    EditText title = activity.findViewById(R.id.edittext_title);
    EditText body = activity.findViewById(R.id.edittext_body);

    title.setText("Test Title");
    body.setText("Test Body");

    activity.findViewById(R.id.feedback_button).performClick();

    ShadowActivity shadowActivity = shadowOf(activity);
    Intent intent = shadowActivity.getNextStartedActivity();

    assertNotNull("Intent should be started. If null, resolveActivity likely failed.", intent);
    assertEquals(Intent.ACTION_VIEW, intent.getAction());

    Uri launchedUri = intent.getData();
    assertNotNull(launchedUri);
    assertEquals("https", launchedUri.getScheme());
    assertEquals("github.com", launchedUri.getHost());
    assertEquals("/sheepdestroyer/materialisheep/issues/new", launchedUri.getPath());
    assertEquals("Test Title", launchedUri.getQueryParameter("title"));
    String bodyParam = launchedUri.getQueryParameter("body");
    assertNotNull(bodyParam);
    assertTrue(bodyParam.contains("Test Body"));
    assertTrue(bodyParam.contains("Device:"));
  }

  @Test
  public void testSendFeedback_validation() {
    FeedbackActivity activity =
        Robolectric.buildActivity(FeedbackActivity.class).create().start().resume().visible().get();

    // Empty fields
    activity.findViewById(R.id.feedback_button).performClick();

    ShadowActivity shadowActivity = shadowOf(activity);
    Intent intent = shadowActivity.getNextStartedActivity();
    assertNull("Intent should not be started if fields are empty", intent);

    TextInputLayout titleLayout = activity.findViewById(R.id.textinput_title);
    TextInputLayout bodyLayout = activity.findViewById(R.id.textinput_body);

    assertNotNull(titleLayout.getError());
    assertNotNull(bodyLayout.getError());
  }
}
