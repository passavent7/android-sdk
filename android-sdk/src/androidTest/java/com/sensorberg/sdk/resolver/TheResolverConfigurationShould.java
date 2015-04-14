package com.sensorberg.sdk.resolver;

import android.test.AndroidTestCase;

import org.fest.assertions.api.Assertions;

public class TheResolverConfigurationShould  extends AndroidTestCase{

    private static final String INITIAL_API_TOKEN = "intial";
    private static final String OTHER_API_TOKEN = "other";
    private static final String INITIAL_API_TOKEN_OTHER_OBJECT = "intial";
    private ResolverConfiguration tested;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tested = new ResolverConfiguration();
    }

    public void test_return_true_when_changing_the_apiToken() throws Exception {
        boolean result = tested.setApiToken(INITIAL_API_TOKEN);
        Assertions.assertThat(result).isFalse().overridingErrorMessage("setting an inital value should not callback a change");

    }

    public void test_return_true_when_changing_the_value() throws Exception {
        tested.setApiToken(INITIAL_API_TOKEN);
        boolean result = tested.setApiToken(OTHER_API_TOKEN);
        Assertions.assertThat(result).isTrue();
    }

    public void test_return_false_when_setting_the_same_value_again() throws Exception {
        tested.setApiToken(INITIAL_API_TOKEN);
        boolean result = tested.setApiToken(INITIAL_API_TOKEN_OTHER_OBJECT);
        Assertions.assertThat(result).isFalse();
    }
}
