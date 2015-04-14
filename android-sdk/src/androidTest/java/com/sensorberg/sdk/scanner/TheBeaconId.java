package com.sensorberg.sdk.scanner;

import android.test.AndroidTestCase;

import com.sensorberg.sdk.model.BeaconId;

import org.fest.assertions.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TheBeaconId extends AndroidTestCase{

    private static final BeaconId BEACON_ID_1 = new BeaconId(UUID.fromString("73676723-7400-0000-ffff-0000ffff0001"), 1, 1);

    private static final BeaconId BEACON_ID_2 = new BeaconId(UUID.fromString("73676723-7400-0000-FFFF-0000FFFF0001"), 1, 1);

    public void test_should_be_equals() throws Exception {
       Assertions.assertThat(BEACON_ID_1.equals(BEACON_ID_2)).isTrue();

    }

    public void test_hash_sbould_be_the_same() throws Exception {
        Assertions.assertThat(BEACON_ID_1.hashCode()).isEqualTo(BEACON_ID_2.hashCode());
    }

    public void test_hashmap_foo() throws Exception {
        Map<BeaconId, String> map = new HashMap<BeaconId, String>();

        map.put(BEACON_ID_1, "foo");

        Assertions.assertThat(map.get(BEACON_ID_2)).isNotNull();
    }

    public void test_bid_generation(){
        Assertions.assertThat(BEACON_ID_1.getBid()).isEqualToIgnoringCase("7367672374000000ffff0000ffff00010000100001");
        Assertions.assertThat(BEACON_ID_2.getBid()).isEqualToIgnoringCase("7367672374000000ffff0000ffff00010000100001");
    }

    public void test_proximityUUID_withoutDashes_Generation(){
        Assertions.assertThat(BEACON_ID_1.getProximityUUIDWithoutDashes()).isEqualTo("7367672374000000ffff0000ffff0001");
        Assertions.assertThat(BEACON_ID_2.getProximityUUIDWithoutDashes()).isEqualTo("7367672374000000ffff0000ffff0001");
    }
}
