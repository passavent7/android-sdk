package com.sensorberg.sdk.internal.http.helper;

import com.android.sensorbergVolley.AuthFailureError;
import com.android.sensorbergVolley.Request;
import com.sensorberg.android.okvolley.OkHttpStack;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class OkHttpStackWithInterceptor extends OkHttpStack{

    private Interceptor interceptor = Interceptor.NONE;

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public interface Interceptor{
        static final Interceptor NONE = new Interceptor() {
            @Override
            public void intercept(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {

            }
        };

        void intercept(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        this.interceptor.intercept(request, additionalHeaders);

        return super.performRequest(request, additionalHeaders);
    }
}
