package com.sensorberg.sdk.internal.http.helper;

import com.android.sensorbergVolley.AuthFailureError;
import com.android.sensorbergVolley.Request;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.util.Map;

public class OkHttpStackWithFailures extends OkHttpStackWithInterceptor{

    public OkHttpStackWithFailures(final int amountOfRequestsBeeingBlocked) {
        super();
        setInterceptor(new Interceptor() {
            private int requestCount;

            @Override
            public void intercept(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
                if (requestCount++ < amountOfRequestsBeeingBlocked) {
                    throw new ConnectTimeoutException();
                }
            }
        });
    }
}
