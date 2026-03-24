## 2026-03-24 - Optimize AdBlocker Host Matching
**Learning:** Using `Collections.synchronizedSet` for a read-heavy ad host list blocks concurrent reads from multiple WebView background threads. Additionally, recursive `String.substring` matching causes unnecessary stack frame allocations.
**Action:** Use `Collections.newSetFromMap(new ConcurrentHashMap<>())` for non-blocking concurrent reads and an iterative `while` loop for substring matching to avoid recursion overhead.
