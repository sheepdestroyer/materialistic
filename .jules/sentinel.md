
## 2024-05-24 - Bypass of Local File Validation via URL Encoding
**Vulnerability:** In `AdBlockWebViewClient`, the interception and validation of `file://` URLs was vulnerable to path traversal because the raw URL string (`url.substring(7)`) was passed directly to `java.io.File`.
**Learning:** `java.io.File` does not automatically URL-decode strings. An attacker could bypass string-based prefix checks by encoding traversal characters (e.g., `%2e%2e` instead of `..`) and appending allowed substrings as URL fragments. The native WebView would decode and process the traversed path, exposing restricted files.
**Prevention:** Always parse and decode URLs properly before applying file validation logic. Use `android.net.Uri.parse(url).getPath()` to obtain the clean, decoded path stripped of fragments and query parameters before invoking `new File(path).getCanonicalPath()`.
