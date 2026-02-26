# Sentinel Journal

## 2025-02-23 - WebView File Access Vulnerability
**Vulnerability:** The application enabled `setAllowFileAccess(true)` and `setJavaScriptEnabled(true)` in `CacheableWebView`, which allows loading any `file://` URL. This could be exploited to read private app files if an attacker can navigate the WebView to a malicious local path.
**Learning:** `AdBlockWebViewClient` was used to intercept requests for ad blocking, but it relied on an `mAdBlockEnabled` flag. Security checks must be independent of feature flags like ad blocking. The vulnerability persisted even if ad blocking was disabled because the interceptor would just delegate to super (allowing the request).
**Prevention:** When `setAllowFileAccess(true)` is required, always implement strict whitelisting in `shouldInterceptRequest` to block unauthorized `file://` access (e.g., allow only `android_asset` and cache directory).
