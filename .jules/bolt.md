## 2025-02-24 - [Performance Leak in SpannableString]
**Learning:** `SpannableString` retains spans added via `setSpan`. If a method adds a span (like `ForegroundColorSpan`) every time it's called (e.g., in a RecyclerView `onBindViewHolder`), and the `SpannableString` instance is cached/reused, the number of spans will grow indefinitely, causing a memory leak and rendering performance degradation.
**Action:** When caching `SpannableString`s that are modified dynamically, always remove existing spans of the same type before adding a new one, or check if the span already exists.
