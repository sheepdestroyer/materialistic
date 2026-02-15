## 2025-05-21 - Exposed Sensitive Data in Logs
**Vulnerability:** User credentials (password) and session tokens were being logged in cleartext in debug builds via OkHttp's `HttpLoggingInterceptor` set to `BODY` level.
**Learning:** `HttpLoggingInterceptor` at `BODY` level logs the entire request body, including form parameters like `pw`. This is dangerous even in debug builds as logs can be leaked.
**Prevention:** Use a custom `Logger` to redact sensitive fields (like `pw`) from the log message before writing to `Log.d`. Also redact sensitive headers (`Authorization`, `Cookie`, `Set-Cookie`).
