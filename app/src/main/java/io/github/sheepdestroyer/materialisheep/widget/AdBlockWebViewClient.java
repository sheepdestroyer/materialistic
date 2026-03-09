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

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import io.github.sheepdestroyer.materialisheep.AdBlocker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new ConcurrentHashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  @Nullable
  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    if (request == null || request.getUrl() == null) {
      return super.shouldInterceptRequest(view, request);
    }

    String url = request.getUrl().toString();

    if (url.startsWith("file://")) {
      try {
        File file = new File(request.getUrl().getPath());
        File cacheDir = view.getContext().getApplicationContext().getCacheDir();
        String canonicalCacheDir = cacheDir.getCanonicalPath() + File.separator;
        String canonicalFile = file.getCanonicalPath();

        if (canonicalFile.startsWith(canonicalCacheDir)
            && file.getName().startsWith(CacheableWebView.CACHE_PREFIX)
            && file.getName().endsWith(CacheableWebView.CACHE_EXTENSION)) {

          return new WebResourceResponse("multipart/related", "UTF-8", new FileInputStream(file));
        }
      } catch (IOException e) {
        // Ignore, fallback to empty resource or default handling below
      }
      return AdBlocker.createEmptyResource();
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
