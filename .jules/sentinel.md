## 2024-05-18 - Fix Local File Inclusion in AdBlockWebViewClient
**Vulnerability:** AdBlockWebViewClient intercepted requests but missed mitigating LFI via URL.
**Learning:** CacheableWebView enables file access to support offline archives. Attackers could request file:// URIs via the WebView, leading to LFI. Interception mechanisms must explicitly block access to local files.
**Prevention:** In WebView clients, explicitly validate the canonical path for 'file://' URIs. Deny everything outside the application's legitimate asset and cache directories before checking ad block rules.
