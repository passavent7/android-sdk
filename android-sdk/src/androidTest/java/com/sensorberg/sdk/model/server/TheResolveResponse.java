package com.sensorberg.sdk.model.server;

import android.support.test.runner.AndroidJUnit4;

import com.sensorberg.sdk.internal.transport.HeadersJsonObjectRequest;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import util.Utils;

import static android.support.test.InstrumentationRegistry.getContext;

@RunWith(AndroidJUnit4.class)
public class TheResolveResponse {

    @Test
    public void should_parse_response_from_the_resolver() throws IOException {
        ResolveResponse tested = HeadersJsonObjectRequest.gson.fromJson(Utils.getRawResourceAsString(com.sensorberg.sdk.test.R.raw.response_layout_parse_error, getContext()), ResolveResponse.class);
        Assertions.assertThat(tested).isNotNull();
    }
}
