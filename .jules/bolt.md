## 2025-02-27 - RecyclerView SortedList O(N) Lookup Optimization
**Learning:** `SortedList` provides efficient O(log N) operations based on sort order (e.g., Rank), but linear O(N) scans for ID lookups unless the sort order is the ID. When handling frequent updates (e.g., database changes via Observer) on the main thread, linear scans on large lists cause jank.
**Action:** Maintain a parallel `HashMap<ID, Item>` alongside `SortedList` to enable O(1) ID-to-Item lookups. Use the Item reference to perform efficient `indexOf(item)` calls on `SortedList`.
