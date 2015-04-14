package com.sensorberg.sdk.testUtils;

import android.content.Context;

import com.sensorberg.sdk.internal.OkHttpClientTransport;
import com.sensorberg.sdk.internal.Transport;

import util.TestConstants;

public class TestPlatformWithSyncchonousHttpTransport extends TestPlatform {
    private final Context context;
    private final Transport transport;


    public TestPlatformWithSyncchonousHttpTransport(Context context) {
        super();
        this.context = context;
        transport = new OkHttpClientTransport(this, null);
        transport.setApiToken(TestConstants.API_TOKEN);
    }

    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public boolean isSyncEnabled() {
        return false;
    }
}
