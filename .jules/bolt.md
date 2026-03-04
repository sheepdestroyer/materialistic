## 2024-05-24 - Optimizing Static Initialization of Thread-Safe Collections
**Learning:** Initializing a `ConcurrentHashMap`-backed set by loading elements from assets line-by-line triggers internal locking mechanisms for every `add()` call, resulting in unnecessary lock contention during app startup.
**Action:** When initializing thread-safe collections with static read-only data, populate a local unsynchronized collection (like `HashSet`) first, and then use `addAll()` to minimize locking overhead on the concurrent collection.

## 2024-05-24 - Avoiding String Encoding Overhead in Network Interceptors
**Learning:** Re-evaluating `"".getBytes()` on every single network request inside a `WebViewClient.shouldInterceptRequest` loop (e.g., when generating an empty `WebResourceResponse` to block an ad) creates significant object allocation and CPU string-encoding overhead.
**Action:** Extract immutable byte arrays to static final fields (e.g., `private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];`) and reuse them to prevent garbage collection churn in high-frequency interceptor methods.
