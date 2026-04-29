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
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
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

    private WebResourceResponse handleFileRequest(WebView view, String url) {
        String lowerUrl = url.toLowerCase(java.util.Locale.ROOT);
        if (!lowerUrl.startsWith("file://")) {
            return null;
        }

        // Let WebView handle asset and res files automatically, as setAllowFileAccess(false)
        // still permits them natively (file:///android_asset/ and file:///android_res/).
        if (lowerUrl.startsWith("file:///android_asset/") || lowerUrl.startsWith("file:///android_res/")) {
            return null; // Return null so super.shouldInterceptRequest proceeds natively
        }

        try {
            String path = Uri.parse(url).getPath();
            if (path == null) return AdBlocker.createEmptyResource();

            File file = new File(path);
            String canonicalPath = file.getCanonicalPath();
            File cacheDir = view.getContext().getCacheDir();
            String cacheCanonicalPath = cacheDir.getCanonicalPath() + File.separator;

            // Allow access ONLY if the file is genuinely within the cache directory
            if (canonicalPath.startsWith(cacheCanonicalPath)) {
                InputStream is = new FileInputStream(file);

                // MHT files need application/x-mimearchive
                String mimeType = "application/octet-stream";
                if (canonicalPath.endsWith(".mht")) {
                    mimeType = "application/x-mimearchive";
                } else {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                    if (extension != null) {
                        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    }
                }

                return new WebResourceResponse(mimeType, "UTF-8", is);
            }
        } catch (Exception e) {
            Log.e("AdBlockWebViewClient", "Error handling file request", e);
        }

        return AdBlocker.createEmptyResource();
    }

    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            WebResourceResponse fileRes = handleFileRequest(view, url);
            if (fileRes != null) return fileRes;
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
        String url = request.getUrl().toString();
        if (url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
            WebResourceResponse fileRes = handleFileRequest(view, url);
            if (fileRes != null) return fileRes;
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
