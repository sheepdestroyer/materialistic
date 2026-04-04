## 2025-02-12 - Prevent Local File Inclusion (LFI) via path validation
**Vulnerability:** Insecure File handling allowed path traversal, reading arbitrary files.
**Learning:** Java `new File(url).getName()` does not decode URL encoded characters, failing basic path traversal checks. It is bypassed by url encoded traversals.
**Prevention:** Use `File.getCanonicalPath()` and check if it `startsWith(baseDirCanonicalPath + File.separator)`.
