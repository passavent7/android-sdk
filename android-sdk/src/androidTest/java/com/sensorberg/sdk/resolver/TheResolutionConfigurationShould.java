package com.sensorberg.sdk.resolver;

import android.test.AndroidTestCase;

import org.fest.assertions.api.Assertions;

public class TheResolutionConfigurationShould extends AndroidTestCase {

    ResolutionConfiguration tested;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tested = new ResolutionConfiguration();
        tested.maxRetries = 3;
    }

    public void test_should_by_default_setup_to_allow_an_request() throws Exception {
        Assertions.assertThat(tested.canTry()).isTrue();
    }

    public void test_should_allow_3_retries() throws Exception {
        tested.retry++;
        tested.retry++;
        tested.retry++;
        Assertions.assertThat(tested.canTry()).isTrue();
        Assertions.assertThat(tested.canRetry()).isFalse();

    }

    public void test_should_plus_plus_the_retries(){
        long origValue = tested.retry;
        tested.retry++;

        Assertions.assertThat(origValue).isEqualTo(tested.retry-1);
    }
}
