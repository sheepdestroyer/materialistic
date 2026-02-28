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
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new ConcurrentHashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  private boolean isSafeFileUrl(WebView view, String url) {
    if (url == null || !url.startsWith("file://")) {
      return true;
    }
    try {
      String filePath = url.substring("file://".length());
      if (filePath.startsWith("/android_asset/")) {
        // Remove /android_asset/ prefix to canonicalize the relative path
        // to make sure no directory traversal bypasses the asset folder
        String relativePath = filePath.substring("/android_asset/".length());
        File baseDir = new File("/android_asset/");
        File assetFile = new File(baseDir, relativePath);
        if (assetFile.getCanonicalPath().startsWith(baseDir.getCanonicalPath() + File.separator)) {
          return true;
        }
      }

      File file = new File(filePath);
      String canonicalPath = file.getCanonicalPath();
      File cacheDir = view.getContext().getApplicationContext().getCacheDir();
      if (cacheDir != null) {
        String canonicalCacheDir = cacheDir.getCanonicalPath();
        if (canonicalPath.startsWith(canonicalCacheDir + File.separator)) {
          return true;
        }
      }
    } catch (IOException e) {
      // Fallthrough to block
    }
    return false;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (!isSafeFileUrl(view, url)) {
      return AdBlocker.createEmptyResource();
    }
    if (!mAdBlockEnabled) {
      return super.shouldInterceptRequest(view, url);
    }
    if (url == null) {
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
    if (!isSafeFileUrl(view, url)) {
      return AdBlocker.createEmptyResource();
    }
    if (!mAdBlockEnabled) {
      return super.shouldInterceptRequest(view, request);
    }
    if (url == null) {
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
