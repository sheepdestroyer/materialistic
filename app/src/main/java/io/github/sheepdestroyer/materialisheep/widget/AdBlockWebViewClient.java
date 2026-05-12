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

package io.github.sheepdestroyer.materialisheep.widget;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import io.github.sheepdestroyer.materialisheep.AdBlocker;

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
    private final boolean mAdBlockEnabled;
    private final Map<String, Boolean> mLoadedUrls = new ConcurrentHashMap<>();

    public AdBlockWebViewClient(boolean adBlockEnabled) {
        mAdBlockEnabled = adBlockEnabled;
    }

    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            if (!url.startsWith("file:///android_asset/") && !url.startsWith("file:///android_res/")) {
                try {
                    String path = android.net.Uri.parse(url).getPath();
                    if (path != null && !path.contains("..")) {
                        java.io.File file = new java.io.File(path);
                        String canonicalPath = file.getCanonicalPath();
                        java.io.File cacheDir = view.getContext().getApplicationContext().getCacheDir();
                        if (canonicalPath.startsWith(cacheDir.getCanonicalPath() + java.io.File.separator) && canonicalPath.endsWith(".mht")) {
                            return new WebResourceResponse("application/x-mimearchive", "UTF-8", new java.io.FileInputStream(file));
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, url);
        }
        boolean ad;
        if (!mLoadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            mLoadedUrls.put(url, ad);
        } else {
            ad = mLoadedUrls.get(url);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String urlStr = request.getUrl().toString();
        if (urlStr.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            if (!urlStr.startsWith("file:///android_asset/") && !urlStr.startsWith("file:///android_res/")) {
                try {
                    String path = request.getUrl().getPath();
                    if (path != null && !path.contains("..")) {
                        java.io.File file = new java.io.File(path);
                        String canonicalPath = file.getCanonicalPath();
                        java.io.File cacheDir = view.getContext().getApplicationContext().getCacheDir();
                        if (canonicalPath.startsWith(cacheDir.getCanonicalPath() + java.io.File.separator) && canonicalPath.endsWith(".mht")) {
                            return new WebResourceResponse("application/x-mimearchive", "UTF-8", new java.io.FileInputStream(file));
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, request);
        }
        boolean ad;
        String url = request.getUrl().toString();
        if (!mLoadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            mLoadedUrls.put(url, ad);
        } else {
            ad = mLoadedUrls.get(url);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, request);
    }
}
