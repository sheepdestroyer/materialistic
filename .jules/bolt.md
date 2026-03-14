## 2024-05-24 - Pre-compile regex for performance
**Learning:** Compiling regex with `Pattern.compile` in Android is relatively expensive and doing so dynamically inside frequently called methods (like parsing HTML strings in `UserServicesClient`) creates unnecessary object allocations and computational overhead.
**Action:** Always pre-compile regular expressions as `private static final Pattern` constants instead of calling `Pattern.compile` inside methods or loops.
