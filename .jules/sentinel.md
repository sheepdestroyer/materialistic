## 2025-02-14 - WebView LFI via CacheableWebView Offline Mode
**Vulnerability:** CacheableWebView enables setAllowFileAccess(true), which globally permits Local File Inclusion (LFI). Offline cached pages (.mht files) loaded via file:// protocol bypass standard security controls because of it.
**Learning:** LFI can be mitigated without breaking legitimate offline/cached page loads by setting setAllowFileAccess(false) and intercepting file:// requests in a custom WebViewClient.
**Prevention:** Always default to setAllowFileAccess(false) for WebViews. If loading local files is necessary, strictly whitelist authorized paths using canonical paths to prevent directory traversal and serve them via shouldInterceptRequest using WebResourceResponse.
