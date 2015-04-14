package com.sensorberg.sdk.internal;

import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.FlakyTest;
import android.util.Log;

import com.sensorberg.sdk.Constants;
import com.sensorberg.sdk.settings.Settings;

import org.fest.assertions.api.Assertions;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TheIntentSchedulingBeUpdateable extends AndroidTestCase {

    AndroidPlatform tested;
    private Bundle INTENT_BUNDLE;

    private Bundle INTENT_BUNDLE_2;

    @Override
    public void setUp() throws Exception {

        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        tested = new AndroidPlatform(getContext());
        Settings mockSettings = mock(Settings.class);
        when(mockSettings.getMessageDelayWindowLength()).thenReturn(Constants.Time.ONE_SECOND / 10);
        tested.setSettings(mockSettings);
        tested.genericBroadcastReceiverClass = TestGenericBroadcastReceiver.class;
//        GenericBroadcastReceiver.setManifestReceiverEnabled(true, getContext());
        INTENT_BUNDLE = new Bundle();
        INTENT_BUNDLE.putString("foo", "bar");

        INTENT_BUNDLE_2 = new Bundle();
        INTENT_BUNDLE_2.putString("bar", "foo");
        TestGenericBroadcastReceiver.reset();

    }

    @FlakyTest(tolerance = 5)
    public void testShouldUpdateAnIntent() throws InterruptedException {
        long time = System.currentTimeMillis();
        long index = System.currentTimeMillis();
        tested.scheduleIntent(index, 500L, INTENT_BUNDLE);
        tested.scheduleIntent(index, 500L, INTENT_BUNDLE_2);

        boolean intentFired = TestGenericBroadcastReceiver.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertThat(intentFired)
                .overridingErrorMessage("The intent was not fired")
                .isTrue();
        Assertions.assertThat(TestGenericBroadcastReceiver.getIntent().getStringExtra("bar"))
                .overridingErrorMessage("the second scheduled intent should have been fired")
                .isNotNull()
                .isEqualTo("foo");
        long elapestime = System.currentTimeMillis() - time;
        Log.d("TEST", "The exact time was" + elapestime + "millis");
    }
}
