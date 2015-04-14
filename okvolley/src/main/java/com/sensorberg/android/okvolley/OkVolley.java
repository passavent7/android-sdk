package com.sensorberg.android.okvolley;

import android.content.Context;
import com.android.sensorbergVolley.RequestQueue;
import com.android.sensorbergVolley.toolbox.Volley;

/**
 * A convenience class for creating an {@link RequestQueue} using
 * {@link OkHttpStack}.
 */
public class OkVolley extends Volley {

    public static RequestQueue newRequestQueue(Context context, boolean shouldUseHttpCache) {
        return Volley.newRequestQueue(context, new OkHttpStack(), shouldUseHttpCache);
    }
}