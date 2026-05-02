## 2025-02-14 - WebView Local File Inclusion (LFI) via CacheableWebView
**Vulnerability:** `CacheableWebView` used `setAllowFileAccess(true)`, allowing any loaded webpage to access arbitrary local files on the device via `file://` URLs.
**Learning:** Even when building custom WebViews for offline archiving, natively enabling file access is extremely dangerous. Interception mechanisms must explicitly handle the exact authorized files.
**Prevention:** Always use `setAllowFileAccess(false)`. If offline file loading (e.g., `.mht` files) is required, use a `WebViewClient`'s `shouldInterceptRequest` to strictly validate `file://` URIs against expected canonical paths (preventing path traversal with `..`) and serve the content via a `WebResourceResponse` instead.
