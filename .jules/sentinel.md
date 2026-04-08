
## 2025-02-14 - Fix Local File Inclusion in WebView
**Vulnerability:** CacheableWebView had `setAllowFileAccess(true)` enabled without intercepting and restricting `file://` scheme requests, allowing a Local File Inclusion (LFI) vulnerability through path traversal.
**Learning:** Permitting unrestricted file access in a WebView can lead to unauthorized access to the application's internal data.
**Prevention:** Disable `setAllowFileAccess(true)` unless strictly necessary. If local files must be accessed, securely validate their canonical paths against allowed base directories (like the application cache directory or `/android_asset/`) in `shouldInterceptRequest`.
