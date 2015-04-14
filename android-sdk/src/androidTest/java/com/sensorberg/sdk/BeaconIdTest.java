package com.sensorberg.sdk;

import android.test.AndroidTestCase;

import com.sensorberg.sdk.model.BeaconId;

import static org.fest.assertions.api.Assertions.assertThat;


public class BeaconIdTest extends AndroidTestCase{
    BeaconId id0 = new BeaconId(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19});
    BeaconId id1 = new BeaconId("000102030405060708090A0B0C0D0E0F10111213");
    BeaconId id2 = new BeaconId("000102030405060700090A0B0C0D0E0F10111213");


    public void testBeaconIdEquals0() {
        assertThat(id0).isEqualTo(id1);
    }


    public void testBeaconIdEquals1() {
        assertThat(id0).isNotEqualTo(id2);
        assertThat(id1).isNotEqualTo(id2);
    }


    public void testBeaconIdEquals2() {
        BeaconId id = new BeaconId(id0.getUuid(), id0.getMajorId(), id0.getMinorId());
        assertThat(id0).isEqualTo(id);
    }


    public void testBeaconIdEquals3() {
        BeaconId id = new BeaconId(id0.getUuid(), id0.getMajorId(), id0.getMinorId());
        assertThat(id0).isEqualTo(id);
    }
}
