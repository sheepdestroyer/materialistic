## 2024-05-19 - Pre-compile Regex in UserServicesClient and SubmitActivity
**Learning:** Dynamic regex compilation via `Pattern.compile()` or `String.replaceAll()` inside methods or loops can add unnecessary object creation overhead and CPU cycles. Pre-compiling as static constants avoids this.
**Action:** When working on Java applications, look for repeated string replacements or regex pattern matching using hardcoded regex. Pre-compile these as private static final `Pattern` constants to improve execution time, especially within repeated tasks or large string processors.
