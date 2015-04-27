package com.sensorberg.sdk.resolver;

import android.test.AndroidTestCase;

import com.sensorberg.sdk.BuildConfig;
import com.sensorberg.sdk.internal.OkHttpClientTransport;
import com.sensorberg.sdk.internal.URLFactory;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;
import com.sensorberg.sdk.testUtils.TestPlatform;

import org.fest.assertions.api.Assertions;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

import util.TestConstants;

import static org.mockito.Mockito.*;

public class TheResolverShould extends AndroidTestCase{

    private Resolver testedWithFakeBackend;

    private static final ScanEvent SCANEVENT_1 = new ScanEvent.Builder()
            .withBeaconId(TestConstants.REGULAR_BEACON_ID)
            .build();
    private Resolver tested;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        URLFactory.switchToProductionEnvironment();

        ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
        TestPlatform androidPlattform = spy(new TestPlatform());
        androidPlattform.setContext(getContext());
        androidPlattform.setTransport(new OkHttpClientTransport(androidPlattform, null));
        androidPlattform.getTransport().setApiToken(TestConstants.API_TOKEN);

        testedWithFakeBackend = new Resolver(resolverConfiguration, androidPlattform);
        ResolverConfiguration realConfiguration = new ResolverConfiguration();
        tested = new Resolver(realConfiguration, androidPlattform);
    }


    public void test_should_try_to_resolve_a_beacon(){
        Resolution resolution = getResolution();
        Resolution spyResolution = spy(resolution);

        testedWithFakeBackend.startResolution(spyResolution);


        verify(spyResolution).queryServer();
    }

    private Resolution getResolution() {
        ResolutionConfiguration resolutionConiguration = new ResolutionConfiguration();
        resolutionConiguration.setScanEvent(SCANEVENT_1);
        return testedWithFakeBackend.createResolution(resolutionConiguration);
    }

    /**
     * https://manage.sensorberg.com/#/campaign/edit/ab68d4ee-8b2d-4f40-adc2-a7ebc9505e89
     * https://manage.sensorberg.com/#/campaign/edit/5dc7f22f-dbcf-4065-8b28-e81b0149fcc8
     * https://manage.sensorberg.com/#/campaign/edit/292ba508-226e-41c3-aac7-969fa712c435
     *
     */

    public void test_resolve_in_app_function() throws Exception {

        ResolverListener mockListener = new ResolverListener() {
            @Override
            public void onResolutionFailed(Resolution resolution, Throwable cause) {
                fail(cause.getMessage());
            }

            @Override
            public void onResolutionsFinished(List<BeaconEvent> events) {
                Assertions.assertThat(events).hasSize(3);
            }

        };
        tested.addResolverListener(mockListener);
        ResolutionConfiguration conf = new ResolutionConfiguration();
        conf.setScanEvent(new ScanEvent.Builder()
                        .withBeaconId(TestConstants.IN_APP_BEACON_ID)
                        .withEventMask(ScanEventType.ENTRY.getMask()).build()
        );
        Resolution resolution = tested.createResolution(conf);
        resolution.start();

    }

    public void test_beacon_with_delay() throws Exception {

        ResolverListener mockListener = mock(ResolverListener.class);
        tested.addResolverListener(mockListener);
        ResolutionConfiguration conf = new ResolutionConfiguration();
        conf.setScanEvent(new ScanEvent.Builder()
                .withBeaconId(TestConstants.DELAY_BEACON_ID)
                .withEventMask(ScanEventType.ENTRY.getMask()).build()
        );
        Resolution resolution = tested.createResolution(conf);
        resolution.start();

        verify(mockListener).onResolutionsFinished(argThat(new BaseMatcher<List<BeaconEvent>>() {
            public long delay;

            @Override
            public boolean matches(Object o) {
                List<BeaconEvent> list = (List<BeaconEvent>) o;
                delay = list.get(0).getAction().getDelayTime();
                return delay == 120000;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("Delaytime was %d and not %d as expected", delay, 120000));
            }
        }));
    }
}
