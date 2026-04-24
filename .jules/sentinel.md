
## 2025-04-24 - [Path Traversal in JavascriptBridge instantiation]
**Vulnerability:** The `PdfAndroidJavascriptBridge` initialized inside `WebFragment.java` accepted a `filePath` argument directly and used it to instantiate a `File` object for loading chunked PDF contents. An attacker could exploit this by providing a file path referencing other internal app directories or sensitive files via `../` combinations.
**Learning:** Instantiation of a file via user-provided strings inside a Javascript Interface is particularly dangerous if that interface reads the underlying file content.
**Prevention:** Always require and validate paths inside Javascript interfaces. Do so by fetching the application's base directory (e.g., `getCacheDir().getCanonicalPath()`) and asserting that the canonical path of the requested file string `startsWith()` the base directory's canonical path.
