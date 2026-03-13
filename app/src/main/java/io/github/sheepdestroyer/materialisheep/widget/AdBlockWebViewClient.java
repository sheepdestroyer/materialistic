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

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import io.github.sheepdestroyer.materialisheep.AdBlocker;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new ConcurrentHashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  private WebResourceResponse handleFileRequest(WebView view, String url) {
    Uri uri = Uri.parse(url);
    if ("file".equals(uri.getScheme())) {
      try {
        String path = uri.getPath();
        if (path == null) {
          return AdBlocker.createEmptyResource();
        }

        // Allow android_asset
        if (path.startsWith("/android_asset/")) {
          return null;
        }

        File cacheFile = new File(path);
        String canonicalCacheDir =
            view.getContext().getApplicationContext().getCacheDir().getCanonicalPath()
                + File.separator;

        if (cacheFile.getCanonicalPath().startsWith(canonicalCacheDir)
            && cacheFile.exists()
            && cacheFile.getName().startsWith(CacheableWebView.CACHE_PREFIX)
            && cacheFile.getName().endsWith(CacheableWebView.CACHE_EXTENSION)) {
          return null; // Valid cache file, let WebView load it natively
        } else {
          return AdBlocker.createEmptyResource(); // Block any unauthorized file access
        }
      } catch (IOException e) {
        return AdBlocker.createEmptyResource();
      }
    }
    return null; // Not a file request, continue normal processing
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (url == null) {
      return super.shouldInterceptRequest(view, url);
    }

    WebResourceResponse fileResponse = handleFileRequest(view, url);
    if (fileResponse != null) {
      return fileResponse;
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
    if (request == null || request.getUrl() == null) {
      return super.shouldInterceptRequest(view, request);
    }

    String url = request.getUrl().toString();

    WebResourceResponse fileResponse = handleFileRequest(view, url);
    if (fileResponse != null) {
      return fileResponse;
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
