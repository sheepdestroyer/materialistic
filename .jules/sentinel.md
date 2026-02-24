## 2025-05-15 - [WebView File Access Mitigation]
**Vulnerability:** `CacheableWebView` enables `setAllowFileAccess(true)` globally, and `AdBlockWebViewClient` did not filter `file://` URLs, potentially allowing arbitrary local file access via user navigation or XSS. Additionally, `PdfAndroidJavascriptBridge` persisted across navigations.
**Learning:** WebViews with file access enabled must have strict URL filtering in `shouldOverrideUrlLoading` (for navigation) and `shouldInterceptRequest` (for resources). JS interfaces should be removed in `onPageStarted` if they are context-specific.
**Prevention:** Override `shouldOverrideUrlLoading` to block `file://` by default, allow-listing only necessary paths (assets, cache). Explicitly remove JS interfaces when loading new pages.
