## 2025-05-23 - Persistent JavascriptInterface in WebView

**Vulnerability:**
The `PdfAndroidJavascriptBridge` JavascriptInterface was not removed when navigating away from the trusted PDF viewer context (`file:///android_asset/pdf/index.html`) to untrusted web content. This allowed malicious websites loaded in the same WebView to access the bridge and potentially read arbitrary files that were previously loaded into the viewer.

**Learning:**
`WebView.addJavascriptInterface` persists across page navigations within the same WebView instance unless explicitly removed. Simply reloading the URL or navigating to a new page does not clear the interface.

**Prevention:**
Always remove JavascriptInterfaces when they are no longer needed, especially when navigating to untrusted content. Use `WebViewClient.onPageStarted` to detect navigation and clean up sensitive interfaces. Restrict `addJavascriptInterface` usage to specific, trusted URLs and ensure the interface is removed immediately upon navigation away from those URLs.
