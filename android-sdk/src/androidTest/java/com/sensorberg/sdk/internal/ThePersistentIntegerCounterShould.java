package com.sensorberg.sdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.sensorberg.sdk.Constants;

import org.fest.assertions.api.Assertions;

public class ThePersistentIntegerCounterShould extends AndroidTestCase{

    PersistentIntegerCounter tested;
    private PersistentIntegerCounter testedCloseToTheEnd;
    private SharedPreferences settings;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        tested = new PersistentIntegerCounter(getContext().getSharedPreferences(String.valueOf(System.currentTimeMillis()), Context.MODE_PRIVATE));

        settings = getContext().getSharedPreferences(String.valueOf(System.currentTimeMillis()), Context.MODE_PRIVATE);
        settings.edit().putInt(Constants.SharedPreferencesKeys.Platform.POST_TO_SERVICE_COUNTER, Integer.MAX_VALUE -1).apply();

        testedCloseToTheEnd = new PersistentIntegerCounter(settings);


    }

    public void test_should_count_up() throws Exception {
        int first = tested.next();

        Assertions.assertThat(first).isEqualTo(tested.next() - 1);

    }

    public void test_should_jump_back_to_0_when_getting_to_the_end_of_the_integer_number_space() throws Exception {
        testedCloseToTheEnd.next();
        Assertions.assertThat(testedCloseToTheEnd.next()).isEqualTo(0);
    }

    public void test_should_not_jump_back_to_0_at_Integer_MAX_VALUE_Minus2() throws Exception {

        Assertions.assertThat(testedCloseToTheEnd.next()).isEqualTo(Integer.MAX_VALUE);
    }

    public void test_values_should_be_persistet() throws Exception {
        testedCloseToTheEnd.next();
        testedCloseToTheEnd.next();
        testedCloseToTheEnd.next();

        int lastOfOtherInstance = testedCloseToTheEnd.next();

        PersistentIntegerCounter otherInstanceSameSharedPrefs = new PersistentIntegerCounter(settings);

        Assertions.assertThat(otherInstanceSameSharedPrefs.next()).isEqualTo(lastOfOtherInstance + 1);

    }
}
