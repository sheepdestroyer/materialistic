# Sentinel Journal

## 2025-02-18 - Removal of Hardcoded GitHub PAT Mechanism
**Vulnerability:** The application contained infrastructure to use a hardcoded GitHub Personal Access Token (`GITHUB_TOKEN`) injected via `BuildConfig` to submit issues directly to the repository via the GitHub API. This pattern encourages shipping sensitive credentials in client-side code, which can be extracted by attackers.
**Learning:** Even if the token defaults to an empty string in the build configuration, the existence of the mechanism tempts developers to populate it for "convenience" or internal builds, creating a high-risk security debt.
**Prevention:** Removed the `FeedbackClient` that used the API. Replaced it with an `Intent` that opens the GitHub Issue creation page in the user's browser. This leverages the user's own authentication and avoids storing any secrets in the app.
