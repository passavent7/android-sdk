package com.sensorberg.sdk.model.realm;

import android.test.AndroidTestCase;

import util.TestConstants;
import com.sensorberg.sdk.internal.transport.HeadersJsonObjectRequest;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;

import org.fest.assertions.api.Assertions;

import io.realm.Realm;
import io.realm.RealmResults;

public class TheRealmScanobjectShould extends AndroidTestCase {

    private RealmScan tested;
    private Realm realm;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        realm = Realm.getInstance(getContext(), "test" + System.currentTimeMillis());

        ScanEvent scanevent = new ScanEvent.Builder()
                .withEventMask(ScanEventType.ENTRY.getMask())
                .withBeaconId(TestConstants.ANY_BEACON_ID)
                .withEventTime(100)
                .build();
        realm.beginTransaction();
        tested = RealmScan.from(scanevent, realm, 0);
        realm.commitTransaction();

    }

    public void test_should_generate_a_bid() throws Exception {
        Assertions.assertThat(tested.getBid()).isEqualToIgnoringCase("192E463C9B8E4590A23FD32007299EF50133701337");
    }

    public void test_should_be_json_serializeable() throws Exception {

        String objectAsJSON = HeadersJsonObjectRequest.gson.toJson(tested);

        Assertions.assertThat(objectAsJSON)
                .isNotEmpty()
                .isEqualToIgnoringCase("{\"bid\":\"192e463c9b8e4590a23fd32007299ef50133701337\",\"trigger\":1,\"dt\":\"1970-01-01T00:00:00Z\"}");

    }

    public void test_should_serialize_a_list_of_objects() throws Exception {
        RealmResults<RealmScan> objects = realm.allObjects(RealmScan.class);

        String objectsAsJson = HeadersJsonObjectRequest.gson.toJson(objects);

        Assertions.assertThat(objectsAsJson)
                .isNotEmpty()
                .isEqualToIgnoringCase("[{\"bid\":\"192e463c9b8e4590a23fd32007299ef50133701337\",\"trigger\":1,\"dt\":\"1970-01-01T00:00:00Z\"}]");

    }
}
