## 2025-05-15 - WebView File Access Persistence
**Vulnerability:** `setAllowFileAccess(true)` persists across navigations in `WebView`. If enabled for a file URL (e.g., cached content), subsequent navigation to an HTTP URL retains the permission, exposing the file system to the remote site.
**Learning:** WebSettings changes are sticky and `WebView` does not reset them on navigation. `loadUrl` logic alone is insufficient if internal navigation occurs.
**Prevention:** Explicitly disable dangerous settings in `WebViewClient.onPageStarted` to enforce security policies on every navigation event.
