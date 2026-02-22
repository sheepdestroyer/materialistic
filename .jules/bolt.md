## 2024-05-22 - SynchronizedSet vs ConcurrentHashMap in Read-Heavy Paths
**Learning:** `Collections.synchronizedSet` synchronizes *every* method call, including reads (`contains`). In read-heavy scenarios like AdBlocker checking `isAd` for every WebView resource, this creates unnecessary lock contention.
**Action:** Use `Collections.newSetFromMap(new ConcurrentHashMap<>())` for thread-safe sets that are read frequently. It allows non-blocking reads.
