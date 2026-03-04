## 2024-05-24 - WebView Local File Inclusion (LFI) via setAllowFileAccess

**Vulnerability:** The application enabled broad file access via `setAllowFileAccess(true)` in `CacheableWebView`, creating a Local File Inclusion (LFI) risk.
**Learning:** `WebView` allows the interception of `file://` URLs, allowing an attacker to read any file the application has access to.
**Prevention:** Disable broad file access using `setAllowFileAccess(false)`. For applications that need to serve cached content, manually intercept specific requests using `WebViewClient.shouldInterceptRequest()` and restrict file access to the specific files requested by enforcing strong canonical path checks and file extensions. Always ensure to add `File.separator` to the end of the directory path string when checking via `startsWith()` to prevent partial directory matching exploits.
