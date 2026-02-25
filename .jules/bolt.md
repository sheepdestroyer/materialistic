## 2025-02-21 - Recursion vs Iteration in AdBlocker
**Learning:** Recursive string matching for subdomains in `AdBlocker` can be replaced with an iterative loop to avoid stack overhead and potential StackOverflowError, although domain depth is usually small. The iterative approach is also easier to reason about and debug.
**Action:** When implementing domain matching or similar hierarchical checks, prefer iterative loops over recursion for performance and safety.

## 2025-02-21 - Concurrent Collections for Read-Heavy Sets
**Learning:** `Collections.synchronizedSet` blocks all access, including reads. For read-heavy static sets like `AdBlocker.AD_HOSTS`, `Collections.newSetFromMap(new ConcurrentHashMap<>())` allows concurrent non-blocking reads, significantly improving performance in high-concurrency scenarios (e.g., WebView resource interception).
**Action:** Use `ConcurrentHashMap`-backed sets for static, read-heavy collections accessed by multiple threads.
