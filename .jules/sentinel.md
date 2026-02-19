## 2025-02-18 - Unsafe WebView Base URL
**Vulnerability:** `WebView.reloadHtml` used `file:///` as the base URL for loading HTML content via `loadDataWithBaseURL`. Since `CacheableWebView` enables file access (`setAllowFileAccess(true)`), this allowed potential local file theft if the loaded HTML contained malicious scripts (XSS).
**Learning:** Even if `setAllowFileAccess(true)` is needed for one feature (loading cached MHT files), it dangerously exposes other features if they use `file:` scheme base URLs.
**Prevention:** Use a safe, distinct base URL (like `https://localhost/`) for loading untrusted HTML content instead of `file:///` or `about:blank` (if loop avoidance is needed).
