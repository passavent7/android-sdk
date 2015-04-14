package com.sensorberg.sdk.internal.transport;

import org.json.JSONObject;

import java.util.Map;

public interface SettingsCallback {
    final static SettingsCallback NONE = new SettingsCallback() {
        @Override
        public void nothingChanged() {

        }

        @Override
        public void onFailure(Throwable e) {

        }

        @Override
        public void onSettingsFound(JSONObject settings) {

        }
    };

    void nothingChanged();

    void onFailure(Throwable e);

    void onSettingsFound(JSONObject settings);
}
