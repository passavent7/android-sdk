package com.sensorberg.android.okvolley;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class UserAgentInterceptor implements Interceptor {
    private String userAgent;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", userAgent)
                .build();
        return chain.proceed(requestWithUserAgent);
    }

    public UserAgentInterceptor setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
}
