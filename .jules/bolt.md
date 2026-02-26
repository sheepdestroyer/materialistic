## 2025-05-23 - SynchronizedSet bottleneck in Read-Heavy Static Cache
**Learning:** `Collections.synchronizedSet(new HashSet<>())` creates a global lock for every read operation (`contains()`). In a high-concurrency scenario like network interception (`AdBlockWebViewClient`), this serializes all checks, even for different URLs.
**Action:** Use `ConcurrentHashMap.newKeySet()` (or `Collections.newSetFromMap(new ConcurrentHashMap<>())` for lower API levels without desugaring) for read-heavy static caches to allow non-blocking concurrent reads.
