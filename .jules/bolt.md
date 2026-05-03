## 2024-05-18 - [Optimization] Pre-compile Matcher inside UserServicesClient & SubmitActivity & ComposeActivity
**Learning:** Precompiling regex inside the same classes into a static Pattern constants skips dynamic regex compilation upon every match request.
**Action:** Always extract `Pattern.compile()` out of inner loop or methods onto a `private static final Pattern` class-level variable.
