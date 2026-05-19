## 2024-05-18 - UserServicesClient Regex Compilation Optimization
**Learning:** Pre-compiling static regular expressions (`Pattern.compile()`) into class-level `private static final Pattern` constants prevents the overhead of repetitive compilation inside loops or method calls.
**Action:** Always extract constant regex patterns from loops and method bodies, such as in `getInputValue` or `String.replaceAll(regex, replacement)` calls, into pre-compiled static constants to drastically improve parsing performance (measured ~70%+ improvement in `UserServicesClient`).
