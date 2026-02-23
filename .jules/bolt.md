## 2025-05-24 - AdBlocker Synchronization Bottleneck
**Learning:** `AdBlocker.AD_HOSTS` used `Collections.synchronizedSet`, forcing thread serialization on every `isAd()` check (called for every resource load across all WebViews). This is a bottleneck for parallel requests.
**Action:** Use `Collections.newSetFromMap(new ConcurrentHashMap<>())` for read-heavy static sets to enable lock-free reads.

## 2025-05-24 - WebViewClient Thread Safety
**Learning:** `AdBlockWebViewClient` accesses an unsynchronized `HashMap` (`mLoadedUrls`) within `shouldInterceptRequest`, which runs on background threads. This creates a race condition risk during concurrent resource loading.
**Action:** Verify thread safety of any fields accessed in `shouldInterceptRequest`. Use `ConcurrentHashMap` or explicit synchronization for caches within `WebViewClient` implementations.
