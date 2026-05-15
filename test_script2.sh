#!/bin/bash
cat app/src/main/java/io/github/sheepdestroyer/materialisheep/data/SyncDelegate.java | grep -n "mWebView = new CacheableWebView" -B 5 -A 5
