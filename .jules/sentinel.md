## 2025-04-23 - Thread Safety in WebViewClient Interception
**Vulnerability:** `ConcurrentModificationException` potential in `AdBlockWebViewClient`.
**Learning:** `WebViewClient.shouldInterceptRequest` is called on background threads. A standard `HashMap` used to cache loaded URLs across multiple intercept requests could throw `ConcurrentModificationException` or lead to stale reads/writes due to thread contention.
**Prevention:** Always use thread-safe collections like `ConcurrentHashMap` when managing state in `WebViewClient` methods that are executed off the main thread.
