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

import android.content.Context;
import android.graphics.Typeface;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatDelegate;

import dagger.ObjectGraph;
import io.github.hidroh.materialistic.data.AlgoliaClient;
import rx.schedulers.Schedulers;

/**
 * The main application class. This class is the entry point of the application and is
 * responsible for initializing application-wide components and settings.
 */
public class Application extends android.app.Application implements Injectable {

    public static Typeface TYPE_FACE = null;
    private ObjectGraph mApplicationGraph;

    /**
     * Called when the application is first created.
     *
     * @param base The base context.
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApplicationGraph = ObjectGraph.create();
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(Preferences.Theme.getAutoDayNightMode(this));
        AlgoliaClient.sSortByTime = Preferences.isSortByRecent(this);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        Preferences.migrate(this);
        TYPE_FACE = FontCache.getInstance().get(this, Preferences.Theme.getTypeface(this));
        AppUtils.registerAccountsUpdatedListener(this);
        AdBlocker.init(this, Schedulers.io());
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
        return mApplicationGraph;
    }
}
