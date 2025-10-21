/*
 * Copyright (c) 2015 Ha Duy Trung
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

package io.github.hidroh.materialistic;

import android.os.Bundle;

import dagger.ObjectGraph;

/**
 * An abstract base activity that supports dependency injection.
 */
public abstract class InjectableActivity extends ThemedActivity implements Injectable {
    private ObjectGraph mActivityGraph;
    private boolean mDestroyed;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(this);
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        mActivityGraph = null;
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.
     */
    @Override
    public void onBackPressed() {
        // TODO http://b.android.com/176265
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            supportFinishAfterTransition();
        }
    }

    /**
     * Injects the dependencies of the given object.
     *
     * @param object The object to inject dependencies into.
     */
    @Override
    public void inject(Object object) {
        getApplicationGraph().inject(object);
    }

    /**
     * Gets the application's object graph for dependency injection.
     *
     * @return The application's object graph.
     */
    @Override
    public ObjectGraph getApplicationGraph() {
        if (mActivityGraph == null) {
            mActivityGraph = ((Injectable) getApplication()).getApplicationGraph()
                    .plus(new ActivityModule(this), new UiModule());
        }
        return mActivityGraph;
    }

    /**
     * Checks if the activity has been destroyed.
     *
     * @return True if the activity has been destroyed, false otherwise.
     */
    public boolean isActivityDestroyed() {
        return mDestroyed;
    }
}
