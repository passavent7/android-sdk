package com.sensorberg.sdk.test;

import android.os.Bundle;

import com.sensorberg.sdk.internal.URLFactory;

import org.junit.Before;

public class SensorbergTestRunner extends android.support.test.runner.AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        if (com.sensorberg.sdk.BuildConfig.RESOLVER_URL != null) {
            URLFactory.setLayoutURL(com.sensorberg.sdk.BuildConfig.RESOLVER_URL);
        }
        URLFactory.setLayoutURL("https://staging-resolver.sensorberg.com/layout");
    }
}
