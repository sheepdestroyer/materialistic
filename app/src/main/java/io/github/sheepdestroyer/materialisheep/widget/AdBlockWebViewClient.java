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
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new HashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  private WebResourceResponse handleFileUrl(WebView view, String url) {
    try {
      Uri uri = Uri.parse(url);
      String path = uri.getPath();
      if (path == null || path.contains("..")) {
        return AdBlocker.createEmptyResource();
      }

      File file = new File(path);
      String canonicalPath = file.getCanonicalPath();
      String cacheDirPath =
          view.getContext().getApplicationContext().getCacheDir().getCanonicalPath()
              + File.separator;

      if (canonicalPath.startsWith(cacheDirPath)) {
        FileInputStream is = new FileInputStream(file);
        return new WebResourceResponse("application/x-mimearchive", "UTF-8", is);
      }
    } catch (Exception e) {
      // Ignore
    }

    return AdBlocker.createEmptyResource();
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (url != null
        && url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")
        && !url.toLowerCase(java.util.Locale.ROOT).startsWith("file:///android_asset/")
        && !url.toLowerCase(java.util.Locale.ROOT).startsWith("file:///android_res/")) {
      return handleFileUrl(view, url);
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
    if (request != null
        && request.getUrl() != null
        && request.getUrl().toString().toLowerCase(java.util.Locale.ROOT).startsWith("file://")
        && !request
            .getUrl()
            .toString()
            .toLowerCase(java.util.Locale.ROOT)
            .startsWith("file:///android_asset/")
        && !request
            .getUrl()
            .toString()
            .toLowerCase(java.util.Locale.ROOT)
            .startsWith("file:///android_res/")) {
      return handleFileUrl(view, request.getUrl().toString());
    }

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
