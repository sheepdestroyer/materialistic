## 2024-04-06 - Prevent LFI in WebFragment PdfAndroidJavascriptBridge
**Vulnerability:** The `PdfAndroidJavascriptBridge` in `WebFragment` instantiated a `File` with a user-supplied path (`pdfFilePath`) and opened it without validating its canonical path. This exposed a Local File Inclusion (LFI) vulnerability where path traversal could be used to read arbitrary files via the javascript interface.
**Learning:** Naively using `new File(filePath)` strips some directory structure but does not handle URL decoding or prevent path traversal when passed to `RandomAccessFile`.
**Prevention:** Explicitly validate that `mFile.getCanonicalPath()` starts with the intended base directory's canonical path (plus `File.separator`) in constructors/methods handling file access.
