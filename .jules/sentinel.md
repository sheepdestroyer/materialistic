## 2024-03-08 - WebView JavascriptInterface Persistence
**Vulnerability:** The `PdfAndroidJavascriptBridge` interface remained attached to the `WebView` when navigating from a trusted PDF viewer page to an untrusted external website.
**Learning:** `WebView` `JavascriptInterfaces` persist across navigations. Adding them for a specific page and assuming they will be cleared when navigating away is incorrect.
**Prevention:** Explicitly call `removeJavascriptInterface` in `WebViewClient.onPageStarted()` when transitioning away from the specific trusted URL (e.g., unless the URL is `PDF_LOADER_URL` or `about:blank`).
