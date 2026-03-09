## 2024-05-18 - Prevent Path Traversal via WebView file access
**Vulnerability:** `setAllowFileAccess(true)` was enabled in `CacheableWebView`, potentially allowing malicious websites to access local files via `file://` URLs, and cache archive serving in `AdBlockWebViewClient` was vulnerable to path traversal.
**Learning:** Broad file access in WebViews is risky. If local file access is needed for caching or archives, it should be restricted via explicit paths or intercepted and validated securely using `getCanonicalPath()`.
**Prevention:** Use `setAllowFileAccess(false)` by default. Intercept `file://` requests and serve explicitly validated local resources (e.g., verifying canonical path against `getCacheDir().getCanonicalPath()`) instead of granting blanket file access.
