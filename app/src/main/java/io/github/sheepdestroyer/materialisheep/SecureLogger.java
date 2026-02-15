package io.github.sheepdestroyer.materialisheep;

import android.util.Log;
import okhttp3.logging.HttpLoggingInterceptor;

class SecureLogger implements HttpLoggingInterceptor.Logger {
    private final String tag;

    SecureLogger(String tag) {
        this.tag = tag;
    }

    @Override
    public void log(String message) {
        if (message.contains("pw=")) {
            message = message.replaceAll("pw=[^&]*", "pw=*****");
        }
        Log.d(tag, message);
    }
}
