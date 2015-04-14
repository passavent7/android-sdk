package com.sensorberg.android.okvolley;

import com.android.sensorbergVolley.toolbox.HurlStack;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;

/**
 * An {@link com.android.sensorbergVolley.toolbox.HttpStack HttpStack} implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {

    private final OkHttpClient client;
    private final OkUrlFactory urlFactory;


    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.client = client;
        this.urlFactory = new OkUrlFactory(client);

    }

    @Override protected HttpURLConnection createConnection(URL url) throws IOException {
        return urlFactory.open(url);
    }
}