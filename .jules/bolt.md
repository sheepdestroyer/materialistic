## 2024-05-24 - Avoid dynamic regex compilation in String.replaceAll

**Learning:** Using `String.replaceAll(regex, replacement)` implicitly compiles the regex every time it is called. When called inside methods like `ComposeActivity.createQuote` or within a loop (like parsing multiple elements), the overhead of dynamic compilation and `Pattern` instantiation degrades performance.

**Action:** Always pre-compile regular expressions as `private static final Pattern` constants. Use `PATTERN_CONSTANT.matcher(input).replaceAll(replacement)` instead of `String.replaceAll(regex, replacement)`. Patterns are thread-safe, immutable, and only compile once, making them perfectly safe to share and drastically more performant for repeated operations.
