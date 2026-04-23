## 2025-02-12 - Prevent Dynamic Regex Compilation
**Learning:** Using `Pattern.compile()` or `String.replaceAll()` inside frequently called methods or loops causes hidden performance overhead because it dynamically compiles the regular expression on every invocation.
**Action:** Pre-compile all regular expressions as `private static final Pattern` constants. They are immutable, thread-safe, and perfectly safe to share across threads, resulting in reduced CPU cycles and garbage collection overhead.
