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
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation") // TODO: Uses deprecated WebResourceRequest API
public class AdBlockWebViewClient extends WebViewClient {
  private final boolean mAdBlockEnabled;
  private final Map<String, Boolean> mLoadedUrls = new HashMap<>();

  public AdBlockWebViewClient(boolean adBlockEnabled) {
    mAdBlockEnabled = adBlockEnabled;
  }

  private boolean isFileUrlSecure(WebView view, String url) {
    if (url != null && url.startsWith("file://")) {
      try {
        String path = url.substring("file://".length());
        File file = new File(path);
        String canonicalPath = file.getCanonicalPath();
        String cacheDir = view.getContext().getCacheDir().getCanonicalPath() + File.separator;
        return canonicalPath.startsWith(cacheDir) || canonicalPath.startsWith("/android_asset/");
      } catch (IOException e) {
        return false;
      }
    }
    return true;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    if (!isFileUrlSecure(view, url)) {
      return AdBlocker.createEmptyResource();
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
    String url = request.getUrl() != null ? request.getUrl().toString() : null;
    if (!isFileUrlSecure(view, url)) {
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
