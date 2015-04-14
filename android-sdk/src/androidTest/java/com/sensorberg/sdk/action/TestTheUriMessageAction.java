package com.sensorberg.sdk.action;

import android.os.Parcel;
import android.test.AndroidTestCase;

import org.fest.assertions.api.Assertions;

import java.util.UUID;


public class TestTheUriMessageAction extends AndroidTestCase {

    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String URL = "http://www.sensorberg.com";
    UriMessageAction tested;

    @Override
    protected void setUp() throws Exception {
        tested = new UriMessageAction(UUID.randomUUID(), MESSAGE, TITLE, URL, null, 0);
    }

    public void test_parcelable(){
        Parcel output = Parcel.obtain();

        tested.writeToParcel(output, 0);
        output.setDataPosition(0);

        UriMessageAction copy = UriMessageAction.CREATOR.createFromParcel(output);

        Assertions.assertThat(copy.getUri()).isEqualTo(tested.getUri());
        Assertions.assertThat(copy.getTitle()).isEqualTo(tested.getTitle());
        Assertions.assertThat(copy.getContent()).isEqualTo(tested.getContent());

        Assertions.assertThat(copy.getType()).isEqualTo(ActionType.MESSAGE_URI);
    }
}
