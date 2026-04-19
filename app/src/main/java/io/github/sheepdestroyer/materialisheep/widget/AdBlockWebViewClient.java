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
import java.util.Map;

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new java.util.concurrent.ConcurrentHashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    WebResourceResponse lfiCheck = checkLocalFileAccess(view, url);
    if (lfiCheck != null) {
      return lfiCheck;
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
    WebResourceResponse lfiCheck = checkLocalFileAccess(view, url);
    if (lfiCheck != null) {
      return lfiCheck;
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

  private WebResourceResponse checkLocalFileAccess(WebView view, String url) {
    if (url != null && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
      try {
        String decodedPath = android.net.Uri.parse(url).getPath();
        if (decodedPath == null) {
          return AdBlocker.createEmptyResource();
        }
        if (url.toLowerCase(java.util.Locale.ROOT).startsWith("file:///android_asset/")) {
          if (decodedPath.contains("..")) {
            return AdBlocker.createEmptyResource();
          }
          return null; // Safe asset, let Chromium load it natively
        }
        java.io.File requestedFile = new java.io.File(decodedPath);
        String canonicalRequested = requestedFile.getCanonicalPath();
        String cacheDirCanonical =
            view.getContext().getApplicationContext().getCacheDir().getCanonicalPath()
                + java.io.File.separator;

        if (!canonicalRequested.startsWith(cacheDirCanonical)) {
          return AdBlocker.createEmptyResource();
        }
      } catch (java.io.IOException | SecurityException e) {
        return AdBlocker.createEmptyResource();
      }
    }
    return null;
  }
}
