## 2026-02-24 - AdBlocker Synchronization Bottleneck
**Learning:** The `AdBlocker` singleton used `Collections.synchronizedSet` for its host list. This causes thread contention on every resource load in `WebView` because `isAd` acquires a lock even for read operations. In a `WebView` with many resources, this serializes ad checking.
**Action:** Use `Collections.newSetFromMap(new ConcurrentHashMap<>())` for read-heavy, write-once (or rare write) collections to allow non-blocking concurrent reads.
