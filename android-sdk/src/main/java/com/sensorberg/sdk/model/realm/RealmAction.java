package com.sensorberg.sdk.model.realm;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sensorberg.sdk.Constants;
import com.sensorberg.sdk.internal.Clock;
import com.sensorberg.sdk.model.ISO8601TypeAdapter;
import com.sensorberg.sdk.resolver.BeaconEvent;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmAction extends RealmObject {

    private String actionId;
    private long timeOfPresentation;
    private long sentToServerTimestamp;
    private long createdAt;
    private int trigger;
    private String pid;
    private boolean keepForever;


    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public long getTimeOfPresentation() {
        return timeOfPresentation;
    }

    public void setTimeOfPresentation(long timeOfPresentation) {
        this.timeOfPresentation = timeOfPresentation;
    }

    public int getTrigger(){
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public long getSentToServerTimestamp() {
        return sentToServerTimestamp;
    }

    public void setSentToServerTimestamp(long sentToServerTimestamp) {
        this.sentToServerTimestamp = sentToServerTimestamp;
    }

    public static RealmAction from(BeaconEvent beaconEvent, Realm realm, Clock clock) {
        RealmAction value = realm.createObject(RealmAction.class);
        value.setActionId(beaconEvent.getAction().getUuid().toString());
        value.setTimeOfPresentation(beaconEvent.getPresentationTime());
        value.setSentToServerTimestamp(RealmFields.Action.NO_DATE);
        value.setCreatedAt(clock.now());
        value.setTrigger(beaconEvent.trigger);
        if (beaconEvent.getBeaconId() != null) {
            value.setPid(beaconEvent.getBeaconId().getBid());
        }
        if (beaconEvent.sendOnlyOnce || beaconEvent.getSuppressionTimeMillis() > 0){
            value.setKeepForever(true);
        }
        return value;
    }


    public static Type ADAPTER_TYPE() {
        try {
            return Class.forName("io.realm.RealmActionRealmProxy");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("io.realm.RealmActionRealmProxy was not found");
        }
    }

    public static RealmResults<RealmAction> notSentScans(Realm realm){
        RealmQuery<RealmAction> scans = realm.where(RealmAction.class)
                .equalTo(RealmFields.Action.sentToServerTimestamp, RealmFields.ScanObject.NO_DATE);
        return scans.findAll();
    }

    public static boolean getCountForSuppressionTime(long lastAllowedPresentationTime, UUID actionUUID, Realm realm) {
        RealmQuery<RealmAction> values = realm.where(RealmAction.class)
                .equalTo(RealmFields.Action.actionId, actionUUID.toString())
                .greaterThanOrEqualTo(RealmFields.Action.timeOfPresentation, lastAllowedPresentationTime);
        keepForever(realm, values);
        return values.count() > 0;
    }

    private static void keepForever(Realm realm, RealmQuery<RealmAction> actionRealmQuery) {
        if (actionRealmQuery.count() > 0){
            realm.beginTransaction();
            RealmResults<RealmAction> values = actionRealmQuery.findAll();
            for (int i = 0; i < values.size(); i++) {
                values.get(i).setKeepForever(true);
            }
            realm.commitTransaction();
        }
    }

    public static boolean getCountForShowOnlyOnceSuppression(UUID actionUUID, Realm realm){
        RealmQuery<RealmAction> values = realm.where(RealmAction.class)
                .equalTo(RealmFields.Action.actionId, actionUUID.toString());
        keepForever(realm, values);
        return values.count() > 0;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setKeepForever(boolean keepForever) {
        this.keepForever = keepForever;
    }

    public boolean getKeepForever() {
        return keepForever;
    }

    public static void markAsSent(List<RealmAction> actions, Realm realm, long now, long actionCacheTtl) {
        if (actions.size() > 0) {
            realm.beginTransaction();
            for (int i = actions.size() - 1; i >= 0; i--) {
                actions.get(i).setSentToServerTimestamp(now);
            }
            realm.commitTransaction();
        }
        removeAllOlderThan(realm, now, actionCacheTtl);
    }

    public static void removeAllOlderThan(Realm realm, long now, long time) {
        RealmResults<?> actionsToDelete = realm.where(RealmAction.class)
                .lessThan(RealmFields.Action.createdAt, now - time)
                .equalTo(RealmFields.Action.keepForever, false)
                .not().equalTo(RealmFields.Action.sentToServerTimestamp, RealmFields.Action.NO_DATE)
                .findAll();

        if (actionsToDelete.size() > 0){
            realm.beginTransaction();
            for (int i = actionsToDelete.size() - 1; i >= 0; i--) {
                actionsToDelete.get(i).removeFromRealm();
            }
            realm.commitTransaction();
        }
    }

    public static class RealmActionTypeAdapter extends ISO8601TypeAdapter<RealmAction> {

        public RealmActionTypeAdapter(String dateFormatString) {
            super(dateFormatString);
        }

        @Override
        public void write(JsonWriter out, RealmAction value) throws IOException {
            out.beginObject();
            out.name("eid").value(value.getActionId());
            out.name("trigger").value(value.getTrigger());
            out.name("pid").value(value.getPid());
            out.name("dt").value(iso8601Format.format(value.getTimeOfPresentation()));
            out.endObject();
        }

        @Override
        public RealmAction read(JsonReader in) throws IOException {
            throw new IllegalArgumentException("you must not use this to read a RealmAction");
        }
    }

    public String getPid(){
        return pid;
    }
}
