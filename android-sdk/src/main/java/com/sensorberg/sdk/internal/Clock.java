package com.sensorberg.sdk.internal;

public interface Clock {

    long now();

    long elapsedRealtime();
}
