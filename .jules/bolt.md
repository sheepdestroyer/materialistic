## 2026-04-18 - Precompile Dynamic Regex
**Learning:** Dynamic regex compilation via `Pattern.compile()` or `String.replaceAll()` inside methods or loops causes unnecessary overhead. Java's regex compilation is slow, and `String.replaceAll()` implicitly compiles the regex pattern on every invocation.
**Action:** Pre-compile regular expressions as `private static final Pattern` constants. They are immutable and thread-safe, making them perfectly safe to share and reuse.
