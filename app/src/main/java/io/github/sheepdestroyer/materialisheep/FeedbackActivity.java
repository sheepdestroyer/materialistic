/*
 * Copyright (c) 2016 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sheepdestroyer.materialisheep;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for sending feedback.
 */
public class FeedbackActivity extends ThemedActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise it is null.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        AppUtils.setTextWithLinks((TextView) findViewById(R.id.feedback_note),
                AppUtils.fromHtml(getString(R.string.feedback_note)));
        final TextInputLayout titleLayout = (TextInputLayout)
                findViewById(R.id.textinput_title);
        final TextInputLayout bodyLayout = (TextInputLayout)
                findViewById(R.id.textinput_body);
        final EditText title = (EditText) findViewById(R.id.edittext_title);
        final EditText body = (EditText) findViewById(R.id.edittext_body);
        final View sendButton = findViewById(R.id.feedback_button);
        findViewById(R.id.button_rate).setOnClickListener(v -> {
            AppUtils.openPlayStore(FeedbackActivity.this);
            finish();
        });
        sendButton.setOnClickListener(v -> {
            titleLayout.setErrorEnabled(false);
            bodyLayout.setErrorEnabled(false);
            if (title.length() == 0) {
                titleLayout.setError(getString(R.string.title_required));
            }
            if (body.length() == 0) {
                bodyLayout.setError(getString(R.string.comment_required));
            }
            if (title.length() == 0 || body.length() == 0) {
                return;
            }

            String feedbackBody = String.format("%s\nDevice: %s %s, SDK: %s, app version: %s",
                    body.getText().toString(),
                    Build.MANUFACTURER,
                    Build.MODEL,
                    Build.VERSION.SDK_INT,
                    BuildConfig.VERSION_CODE);

            Uri uri = Uri.parse("https://github.com/sheepdestroyer/materialisheep/issues/new")
                    .buildUpon()
                    .appendQueryParameter("title", title.getText().toString())
                    .appendQueryParameter("body", feedbackBody)
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(intent);
                finish();
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, R.string.no_playstore, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if the activity should be displayed as a dialog.
     *
     * @return True if the activity should be displayed as a dialog, false otherwise.
     */
    @Override
    protected boolean isDialogTheme() {
        return true;
    }
}
