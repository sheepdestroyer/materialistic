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

    private boolean isBlockedFile(WebView view, String url) {
        if (url == null) return false;
        String lowerUrl = url.toLowerCase(java.util.Locale.ROOT);
        if (!lowerUrl.startsWith("file://")) return false;
        try {
            // Security: Decode URI path to properly inspect for LFI traversal
            String path = android.net.Uri.parse(url).getPath();
            if (path == null) return true;
            if (lowerUrl.startsWith("file:///android_asset/")) {
                // Security: Prevent accessing files outside the asset directory
                if (path.contains("..")) return true;
            } else {
                java.io.File file = new java.io.File(path);
                String canonicalPath = file.getCanonicalPath();
                java.io.File cacheDir = view.getContext().getCacheDir();
                // Security: Validate canonical path matches expected cache directory to prevent arbitrary local file reads
                if (cacheDir == null || !canonicalPath.startsWith(cacheDir.getCanonicalPath() + java.io.File.separator)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            // Security: Fail securely on any parsing or I/O error
            return true;
        }
    }

    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (isBlockedFile(view, url)) return AdBlocker.createEmptyResource();
        if (url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            return super.shouldInterceptRequest(view, url);
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
        String url = request.getUrl() != null ? request.getUrl().toString() : null;
        if (isBlockedFile(view, url)) return AdBlocker.createEmptyResource();
        if (url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            return super.shouldInterceptRequest(view, request);
        }
        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, request);
        }
        boolean ad;
        if (!mLoadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            mLoadedUrls.put(url, ad);
        } else {
            ad = mLoadedUrls.get(url);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, request);
    }
}
