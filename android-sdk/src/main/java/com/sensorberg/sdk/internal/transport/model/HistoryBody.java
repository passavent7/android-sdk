package com.sensorberg.sdk.internal.transport.model;

import com.sensorberg.sdk.internal.Clock;
import com.sensorberg.sdk.model.realm.RealmAction;
import com.sensorberg.sdk.model.realm.RealmScan;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class HistoryBody {

    public final List<RealmScan> events;
    public final List<RealmAction> actions;
    public final Date deviceTimestamp;
    public final JSONObject payload;

    public HistoryBody(List<RealmScan> scans, List<RealmAction> actions, Clock clock, JSONObject payload) {
        this.events = scans;
        this.payload = payload;
        this.deviceTimestamp = new Date(clock.now());
        this.actions = actions;
    }
}
