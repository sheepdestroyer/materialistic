## 2024-05-18 - [WebResourceResponse NullPointer Crash]
**Vulnerability:** NullPointerException risk when blocking requests in `shouldInterceptRequest`.
**Learning:** Android's `WebResourceResponse` constructor throws an exception or crashes the app if the `InputStream` is null when returning a blocked resource response.
**Prevention:** Always use a valid empty stream (e.g., `new ByteArrayInputStream(new byte[0])`) or existing helpers like `AdBlocker.createEmptyResource()` instead of passing `null`.
