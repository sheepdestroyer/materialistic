#!/bin/bash
cat app/src/main/java/io/github/sheepdestroyer/materialisheep/data/SyncDelegate.java | grep -n "mWebView" -A 5 -B 5
