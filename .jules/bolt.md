
## 2024-05-28 - Optimize AdBlocker operations
**Learning:** Initializing synchronized collections (like `Collections.synchronizedSet` or `ConcurrentHashMap` sets) item-by-item causes significant lock contention. Furthermore, using recursive functions for domain matching adds unnecessary stack frame instantiation overhead.
**Action:** Populate a local collection first and use `addAll` for bulk initialization to minimize lock contention. For read-heavy static sets, use `Collections.newSetFromMap(new ConcurrentHashMap<>())` instead of `Collections.synchronizedSet`. Lastly, convert recursive operations to iterative loops when possible to avoid stack growth.
