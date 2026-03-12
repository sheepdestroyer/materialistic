## 2024-03-12 - Fix JavascriptInterface Vulnerability

**Vulnerability:** A `PdfAndroidJavascriptBridge` JavascriptInterface was added to a WebView to support PDF rendering. However, it was only removed when explicitly loading a PDF, not universally. This allowed the interface to potentially persist when users navigated from the PDF viewer to untrusted websites within the same WebView instance.
**Learning:** `addJavascriptInterface` binds an interface to the WebView instance, persisting across navigations unless explicitly removed. The codebase had logic to remove it in the specific case of `reloadUrl(url, pdfFilePath)`, but normal untrusted navigations (like links clicked inside the WebView) could maintain the bridge.
**Prevention:** Always remove Javascript interfaces explicitly during `onPageStarted` for untrusted domains to ensure clean slate navigations.
