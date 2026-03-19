
## 2024-06-18 - Pre-compiling Regex Patterns
**Learning:** In Java, compiling regular expressions via `Pattern.compile()` on the fly (e.g., inside methods like `getInputValue` or `parseLoginError`) incurs significant CPU overhead, particularly when executed repeatedly or within loops. `Pattern` objects are thread-safe and immutable, unlike `Matcher` objects.
**Action:** Extract inline `Pattern.compile()` calls into `private static final Pattern` constants to compile them once per class load. Use these pre-compiled constants to create mutable `Matcher` instances safely within methods.
