## 2024-05-23 - Avoid dynamic Pattern compilation in loops
**Learning:** `UserServicesClient.java` was compiling `REGEX_VALUE` using `Pattern.compile` inside a `while` loop when iterating over parsed HTML input elements (`matcherInput.find()`). This is a hidden performance anti-pattern that creates significant instantiation overhead during repetitive loops.
**Action:** Always pre-compile regular expressions as `private static final Pattern` constants instead of using `Pattern.compile()` or inline `String.matches/replaceAll` inside methods or loops.
