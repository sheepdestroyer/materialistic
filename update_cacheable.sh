sed -i 's/private static final String CACHE_PREFIX = "webarchive-";/static final String CACHE_PREFIX = "webarchive-";/' app/src/main/java/io/github/sheepdestroyer/materialisheep/widget/CacheableWebView.java
sed -i 's/private static final String CACHE_EXTENSION = ".mht";/static final String CACHE_EXTENSION = ".mht";/' app/src/main/java/io/github/sheepdestroyer/materialisheep/widget/CacheableWebView.java
sed -i 's/webSettings.setAllowFileAccess(true);/webSettings.setAllowFileAccess(false);/' app/src/main/java/io/github/sheepdestroyer/materialisheep/widget/CacheableWebView.java
