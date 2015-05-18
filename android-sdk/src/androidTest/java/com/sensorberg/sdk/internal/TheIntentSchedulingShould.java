package com.sensorberg.sdk.internal;

import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.FlakyTest;

import com.sensorberg.sdk.Constants;
import com.sensorberg.sdk.settings.Settings;

import org.fest.assertions.api.Assertions;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TheIntentSchedulingShould extends AndroidTestCase {

    AndroidPlatform tested;
    private Bundle INTENT_BUNDLE;

    @Override
    public void setUp() throws Exception {

        super.setUp();
        tested = new AndroidPlatform(getContext());
        Settings mockSettings = mock(Settings.class);
        when(mockSettings.getMessageDelayWindowLength()).thenReturn(Constants.Time.ONE_SECOND);
        tested.setSettings(mockSettings);
        tested.genericBroadcastReceiverClass = TestGenericBroadcastReceiver.class;
//        GenericBroadcastReceiver.setManifestReceiverEnabled(true, getContext());
        INTENT_BUNDLE = new Bundle();
        INTENT_BUNDLE.putString("foo", "bar");
        TestGenericBroadcastReceiver.reset();
    }

    @FlakyTest(tolerance = 5)
    public void testShouldScheduleAnIntent() throws Exception {
        tested.scheduleIntent(1, 500L, INTENT_BUNDLE);

        boolean intentFired = TestGenericBroadcastReceiver.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertThat(intentFired)
                .overridingErrorMessage("The intent was not fired")
                .isTrue();
    }

    @FlakyTest(tolerance = 5)
    public void testShouldUnScheduleAnIntent() throws Exception {
        tested.scheduleIntent(2, 500L, INTENT_BUNDLE);

        tested.unscheduleIntent(2);
        boolean intentFired = TestGenericBroadcastReceiver.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertThat(intentFired)
                .overridingErrorMessage("The intent was fired even though it was unscheduled")
                .isFalse();
    }
}
