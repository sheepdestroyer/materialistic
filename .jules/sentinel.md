# Sentinel Journal

## Security Learnings

## 2024-05-21 - [WebView Path Traversal Vulnerability]
**Vulnerability:** `setAllowFileAccess(true)` in `CacheableWebView` enables file access in `WebView`, allowing potentially malicious websites or XSS payloads to access local files via `file://` URLs.
**Learning:** Even if `setAllowFileAccess` is necessary for specific functionality (like loading cached web archives), it exposes the entire filesystem to the `WebView` context.
**Prevention:** Implement strict URL interception in `WebViewClient.shouldInterceptRequest` to validate and restrict all `file://` scheme requests. Only allow access to explicitly required directories, such as `android_asset/` and the application's specific cache directory. Use `getCanonicalPath()` to resolve paths and prevent directory traversal attacks (e.g., `file:///data/data/com.example/cache/../../shared_prefs/prefs.xml`). Always perform security checks before application logic (like ad blocking) to ensure protections are not bypassed by feature flags.
