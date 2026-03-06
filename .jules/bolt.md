## 2023-10-27 - [Iterative AdBlocker Subdomain Matching]
**Learning:** The `AdBlocker` domain matching logic should use an iterative approach (checking subdomains) rather than recursion to avoid stack frame instantiation overhead. Note that `substring()` still creates strings in both versions, but iteration removes stack growth.
**Action:** Use an iterative while loop for string tokenization or substring checks on domain names.
