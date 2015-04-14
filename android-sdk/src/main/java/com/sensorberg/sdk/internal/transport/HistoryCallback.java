package com.sensorberg.sdk.internal.transport;

import com.sensorberg.sdk.model.realm.RealmAction;
import com.sensorberg.sdk.model.realm.RealmScan;
import com.sensorberg.sdk.resolver.BeaconEvent;

import java.util.List;

public interface HistoryCallback {
    void onFailure(Throwable throwable);

    void onInstantActions(List<BeaconEvent> instantActions);

    void onSuccess(List<RealmScan> scans, List<RealmAction> actions);
}
