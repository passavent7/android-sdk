package com.sensorberg.sdk.test;

import android.os.Bundle;

import com.sensorberg.sdk.internal.URLFactory;

import net.danlew.android.joda.JodaTimeAndroid;

import org.junit.Before;

public class SensorbergTestRunner extends android.support.test.runner.AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());

        if (com.sensorberg.sdk.BuildConfig.RESOLVER_URL != null) {
            URLFactory.setLayoutURL(com.sensorberg.sdk.BuildConfig.RESOLVER_URL);
        }
        JodaTimeAndroid.init(getContext());
    }
}