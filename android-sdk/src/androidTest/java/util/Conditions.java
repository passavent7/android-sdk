package util;

import org.fest.assertions.core.Condition;
import org.json.JSONArray;

public class Conditions {

    public static Condition<? super JSONArray> size(final int size) {
        return new Condition<JSONArray>() {
            @Override
            public boolean matches(JSONArray value) {
                return value.length() == size;
            }
        };
    }
}
