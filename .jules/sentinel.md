# Sentinel's Security Journal 🛡️

## 2025-10-27 - WebView File Access Vulnerability
**Vulnerability:** `CacheableWebView` enabled `setAllowFileAccess(true)` globally for all loaded content, and `WebView` used `file:///` as a base URL for `loadDataWithBaseURL`. This combination potentially allowed malicious HTML content to access local files if it could bypass other restrictions.
**Learning:** `setAllowFileAccess(true)` is often enabled for legitimate features (like offline caching) but must be scoped tightly. A global setting persists across page loads. Using `file:///` as a base URL grants broad file access permissions to the loaded content.
**Prevention:**
1. Default to `setAllowFileAccess(false)`.
2. Only enable it strictly when loading a trusted local file (e.g., from cache), and ensure it's disabled otherwise.
3. Use more restrictive base URLs like `file:///android_asset/` or `about:blank` when loading HTML content.
