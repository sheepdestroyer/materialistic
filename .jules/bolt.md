## 2024-03-20 - Avoid dynamic `Pattern.compile()` in methods and loops
**Learning:** `Pattern.compile()` is an expensive operation in Java. Dynamically calling it inside methods (or worse, inside loops like in `getInputValue`) is a significant performance bottleneck as it forces regex compilation on every execution/iteration.
**Action:** Pre-compile regular expressions as `private static final Pattern` constants at the class level. They are immutable and thread-safe, making them perfect to share and reuse without recompilation overhead.
