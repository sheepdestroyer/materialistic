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

import android.content.Context;
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
  private final File mCacheDir;
  private final String mCacheDirPath;

  public AdBlockWebViewClient(Context context, boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
    mCacheDir = context.getApplicationContext().getCacheDir();
    String path;
    try {
      path = mCacheDir.getCanonicalPath();
    } catch (IOException e) {
      path = mCacheDir.getAbsolutePath();
    }
    mCacheDirPath = path;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (!mAdBlockEnabled) {
      return super.shouldInterceptRequest(view, url);
    }
    if (isUnsafeFileUrl(url)) {
      return AdBlocker.createEmptyResource();
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
    if (!mAdBlockEnabled) {
      return super.shouldInterceptRequest(view, request);
    }
    String url = request.getUrl().toString();
    if (isUnsafeFileUrl(url)) {
      return AdBlocker.createEmptyResource();
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

  private boolean isUnsafeFileUrl(String url) {
    if (url.startsWith("file://")) {
      if (url.startsWith("file:///android_asset/")) {
        return false;
      }
      try {
        // file:///path -> /path
        String path = url.substring(7);
        File file = new File(path);
        String canonicalPath = file.getCanonicalPath();
        if (canonicalPath.startsWith(mCacheDirPath)) {
          return false;
        }
      } catch (IOException e) {
        // If we can't determine canonical path, treat as unsafe
        return true;
      }
      return true;
    }
    return false;
  }
}
