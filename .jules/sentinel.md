## 2024-05-24 - [Concurrency Bug in WebViewClient]
**Vulnerability:** `ConcurrentModificationException` risk in `AdBlockWebViewClient.mLoadedUrls`.
**Learning:** Android `WebViewClient` methods like `shouldInterceptRequest` are called on multiple background threads. Using a non-synchronized `HashMap` for shared state can lead to race conditions or application freezes.
**Prevention:** Always use `ConcurrentHashMap` for shared state accessed within `WebViewClient` lifecycle methods.
