
## 2024-05-24 - Fix PDF Path Traversal Vulnerability
**Vulnerability:** `PdfAndroidJavascriptBridge` accepted an arbitrary `filePath`, and its `getChunk` interface method allowed WebView Javascript to read any local file exposed to the application, creating a critical file disclosure and path traversal vulnerability when `reloadUrl` handles an arbitrary deep link or file download target.
**Learning:** Instantiating `File` and utilizing `RandomAccessFile` based strictly on unvalidated input parameters opens direct paths to arbitrary file reads (LFI) in javascript bridges.
**Prevention:** Always validate that local file paths target an authorized directory (e.g., application cache) by asserting their `getCanonicalPath()` strictly starts with the authorized directory's canonical path and a trailing `File.separator`. Constructor instantiation within untrusted contexts must throw a `SecurityException` upon violation and be caught securely.
