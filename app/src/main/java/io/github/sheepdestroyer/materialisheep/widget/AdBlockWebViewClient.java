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
import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import java.util.HashMap;
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

    private WebResourceResponse handleFileRequest(Context context, String url) {
        if (url == null) return null;
        String lowerUrl = url.toLowerCase(Locale.ROOT);
        if (!lowerUrl.startsWith("file://")) return null;
        if (lowerUrl.startsWith("file:///android_asset/") || lowerUrl.startsWith("file:///android_res/")) return null;

        try {
            String decodedPath = Uri.parse(url).getPath();
            if (decodedPath == null || decodedPath.contains("..")) return null;

            File file = new File(decodedPath);
            File cacheDir = context.getApplicationContext().getCacheDir();

            if (!file.getCanonicalPath().startsWith(cacheDir.getCanonicalPath() + File.separator)) {
                return null;
            }
            if (file.exists() && file.getName().endsWith(".mht")) {
                return new WebResourceResponse("application/x-mimearchive", "UTF-8", new FileInputStream(file));
            }
        } catch (Exception e) {}
        return null;
    }

    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse fileRes = handleFileRequest(view.getContext(), url);
        if (fileRes != null) return fileRes;
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
        WebResourceResponse fileRes = handleFileRequest(view.getContext(), request.getUrl().toString());
        if (fileRes != null) return fileRes;
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
