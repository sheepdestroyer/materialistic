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

  private WebResourceResponse handleOfflineCacheUrl(WebView view, String url) {
    if (url.toLowerCase(java.util.Locale.ROOT).startsWith("file://")) {
      try {
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        if (path != null && !path.contains("..")) {
          File file = new File(path);
          File cacheDir = view.getContext().getApplicationContext().getCacheDir();
          // Only intercept cache file reads (e.g. .mht archives).
          // Allow normal file:///android_asset/ and file:///android_res/ to fall through (return
          // null).
          if (file.getCanonicalPath().startsWith(cacheDir.getCanonicalPath() + File.separator)) {
            String mimeType = "application/octet-stream";
            if (path.endsWith(".mht")) {
              mimeType = "application/x-mimearchive";
            }
            return new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(file));
          }
        }
      } catch (IOException e) {
        // Ignore parsing errors and let default handling take over
      }
    }
    return null;
  }

  @Override
  public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    WebResourceResponse cacheResponse = handleOfflineCacheUrl(view, url);
    if (cacheResponse != null) {
      return cacheResponse;
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
    WebResourceResponse cacheResponse = handleOfflineCacheUrl(view, url);
    if (cacheResponse != null) {
      return cacheResponse;
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
