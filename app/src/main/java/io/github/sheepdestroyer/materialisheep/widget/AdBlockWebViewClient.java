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

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new ConcurrentHashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  private boolean isInvalidFileUrl(WebView view, String url) {
    if (url == null) return false;
    String lowerUrl = url.toLowerCase(java.util.Locale.ROOT);
    // Security concern: Validate file:// URLs to prevent Local File Inclusion (LFI)
    // and path traversal vulnerabilities (e.g., file:///android_asset/../../etc/passwd).
    if (lowerUrl.startsWith("file://")) {
      if (lowerUrl.startsWith("file:///android_asset/")) {
        try {
          String path = Uri.parse(url).getPath();
          return path != null && path.contains("..");
        } catch (Exception e) {
          return true;
        }
      }
      try {
        String path = Uri.parse(url).getPath();
        if (path == null) return true;
        String canonicalPath = new File(path).getCanonicalPath();
        String cachePath = view.getContext().getCacheDir().getCanonicalPath() + File.separator;
        if (!canonicalPath.startsWith(cachePath)) {
          return true;
        }
      } catch (IOException | SecurityException e) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (isInvalidFileUrl(view, url)) {
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
    if (isInvalidFileUrl(view, request.getUrl().toString())) {
      return AdBlocker.createEmptyResource();
    }
    if (!mAdBlockEnabled) {
      return super.shouldInterceptRequest(view, request);
    }
    String url = request.getUrl().toString();
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
