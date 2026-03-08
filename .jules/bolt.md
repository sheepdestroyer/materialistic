## 2024-03-08 - Pre-compile Regex Patterns
**Learning:** Found dynamic `Pattern.compile()` calls inside loops and methods in `UserServicesClient` and `SubmitActivity`. Compiling Regex Patterns is an expensive CPU operation and generates unnecessary garbage.
**Action:** When defining static Regex string constants, compile them into `private static final Pattern` instances so they can be reused across method calls and loops without the overhead of recompilation.
