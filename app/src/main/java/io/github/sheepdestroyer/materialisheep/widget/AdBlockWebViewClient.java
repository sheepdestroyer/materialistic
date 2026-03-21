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

import android.net.Uri;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        if (url != null && url.startsWith("file://")) {
            try {
                String path = Uri.parse(url).getPath();
                if (path == null) {
                    return AdBlocker.createEmptyResource();
                }
                String canonicalPath = new File(path).getCanonicalPath();
                String assetDir = new File("/android_asset/").getCanonicalPath() + File.separator;
                String cacheDir = view.getContext().getApplicationContext().getCacheDir().getCanonicalPath() + File.separator;
                if (canonicalPath.startsWith(assetDir) ||
                    (canonicalPath.startsWith(cacheDir) && canonicalPath.contains(CacheableWebView.CACHE_PREFIX) && canonicalPath.endsWith(CacheableWebView.CACHE_EXTENSION))) {
                    return null; // Let Chromium handle allowed local files natively
                }
                return AdBlocker.createEmptyResource();
            } catch (Exception e) {
                return AdBlocker.createEmptyResource();
            }
        }
        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, url);
        }
        if (url == null) {
            return super.shouldInterceptRequest(view, url);
        }
        Boolean ad = mLoadedUrls.get(url);
        if (ad == null) {
            ad = AdBlocker.isAd(url);
            mLoadedUrls.put(url, ad);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl() != null ? request.getUrl().toString() : null;
        if (url != null && url.startsWith("file://")) {
            try {
                String path = Uri.parse(url).getPath();
                if (path == null) {
                    return AdBlocker.createEmptyResource();
                }
                String canonicalPath = new File(path).getCanonicalPath();
                String assetDir = new File("/android_asset/").getCanonicalPath() + File.separator;
                String cacheDir = view.getContext().getApplicationContext().getCacheDir().getCanonicalPath() + File.separator;
                if (canonicalPath.startsWith(assetDir) ||
                    (canonicalPath.startsWith(cacheDir) && canonicalPath.contains(CacheableWebView.CACHE_PREFIX) && canonicalPath.endsWith(CacheableWebView.CACHE_EXTENSION))) {
                    return null; // Let Chromium handle allowed local files natively
                }
                return AdBlocker.createEmptyResource();
            } catch (Exception e) {
                return AdBlocker.createEmptyResource();
            }
        }
        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, request);
        }
        if (url == null) {
            return super.shouldInterceptRequest(view, request);
        }
        Boolean ad = mLoadedUrls.get(url);
        if (ad == null) {
            ad = AdBlocker.isAd(url);
            mLoadedUrls.put(url, ad);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, request);
    }
}
