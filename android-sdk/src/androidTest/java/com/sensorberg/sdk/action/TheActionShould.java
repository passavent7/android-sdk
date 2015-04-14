package com.sensorberg.sdk.action;

import android.test.AndroidTestCase;

import util.Utils;

import org.fest.assertions.api.Assertions;
import org.fest.assertions.data.Offset;
import org.json.JSONException;

import java.io.IOException;

public class TheActionShould extends AndroidTestCase {

    public void test_not_parse_an_array_as_an_object() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_001_array, getContext()));
        try{
            arrayPayloadAction.getPayloadJSONObject();
            fail("there was no exception");
        } catch (JSONException e){
            //all is fine
        }
    }

    public void test_not_parse_an_object_as_an_array() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_002_object, getContext()));
        try{
            arrayPayloadAction.getPayloadJSONArray();
            fail("there was no exception");
        } catch (JSONException e){
            //all is fine
        }
    }

    public void test_allow_parsing_of_booleans() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_003_boolean, getContext()));

        Boolean output = Boolean.valueOf(arrayPayloadAction.getPayload());
        Assertions.assertThat(output).isEqualTo(true);
    }

    public void test_allow_parsing_of_integer() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_004_integer, getContext()));

        Integer output = Integer.valueOf(arrayPayloadAction.getPayload());
        Assertions.assertThat(output).isEqualTo(1337);
    }

    public void test_allow_parsing_of_strings() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_005_string, getContext()));


        String output = arrayPayloadAction.getPayload();
        Assertions.assertThat(output).isEqualTo("foo");
    }

    public void test_allow_parsing_of_double_values() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_006_double, getContext()));

        Double output = Double.valueOf(arrayPayloadAction.getPayload());
        Assertions.assertThat(output).isEqualTo(1.2345, Offset.offset(0.00001));
    }

    public void test_allow_parsing_of_integerWithExponentValue() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_008_integer_with_exponent, getContext()));

        Double output = Double.valueOf(arrayPayloadAction.getPayload());
        Assertions.assertThat(output).isEqualTo(1337, Offset.offset(0.1));
    }

    public void test_allow_parsing_of_emptyString() throws IOException, JSONException {
        Action arrayPayloadAction = ActionFactory.actionFromJSONObject(Utils.getRawResourceAsJSON(com.sensorberg.sdk.test.R.raw.action_factory_payload_009_empty_string, getContext()));

        Assertions.assertThat(arrayPayloadAction.getPayload()).isNotNull().hasSize(0);
    }
}
