package com.sensorberg.sdk.model.realm;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sensorberg.sdk.model.ISO8601TypeAdapter;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmScan extends RealmObject {

    private long eventTime;
    private boolean isEntry;
    private String proximityUUID;
    private int proximityMajor;
    private int proximityMinor;
    private long sentToServerTimestamp;
    private long createdAt;

    public static RealmScan from(ScanEvent scanEvent, Realm realm, long now) {
        RealmScan value = realm.createObject(RealmScan.class);
        value.setEventTime(scanEvent.getEventTime());
        value.setEntry(scanEvent.getEventMask() == ScanEventType.ENTRY.getMask());
        value.setProximityUUID(scanEvent.getBeaconId().getUuid().toString());
        value.setProximityMajor(scanEvent.getBeaconId().getMajorId());
        value.setProximityMinor(scanEvent.getBeaconId().getMinorId());
        value.setSentToServerTimestamp(RealmFields.ScanObject.NO_DATE);
        value.setCreatedAt(now);
        return value;
    }

    public boolean isEntry() {
        return isEntry;
    }

    public void setEntry(boolean isEntry) {
        this.isEntry = isEntry;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(String proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public int getProximityMajor() {
        return proximityMajor;
    }

    public void setProximityMajor(int proximityMajor) {
        this.proximityMajor = proximityMajor;
    }

    public int getProximityMinor() {
        return proximityMinor;
    }

    public void setProximityMinor(int proximityMinor) {
        this.proximityMinor = proximityMinor;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public long getSentToServerTimestamp() {
        return sentToServerTimestamp;
    }

    public void setSentToServerTimestamp(long sentToServerTimestamp) {
        this.sentToServerTimestamp = sentToServerTimestamp;
    }

    public String getBid(){
        return this.getProximityUUID().replace("-", "") + String.format("%1$05d%2$05d", this.getProximityMajor() , this.getProximityMinor());
    }

    public int getTrigger(){
        return isEntry() ? ScanEventType.ENTRY.getMask() : ScanEventType.EXIT.getMask();
    }


    public static Type ADAPTER_TYPE() {
        try {
            return Class.forName("io.realm.RealmScanRealmProxy");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("io.realm.RealmScanRealmProxy was not found");
        }
    }

    public static RealmResults<RealmScan> notSentScans(Realm realm){
        RealmQuery<RealmScan> scans = realm.where(RealmScan.class)
                .equalTo(RealmFields.ScanObject.sentToServerTimestamp, RealmFields.ScanObject.NO_DATE);
        return scans.findAll();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public static void maskAsSent(List<RealmScan> scans, Realm realm, long now, long cacheTtl) {
        if (scans.size() > 0) {
            realm.beginTransaction();
            for (int i = scans.size() - 1; i >= 0; i--) {
                scans.get(i).setSentToServerTimestamp(now);
            }
            realm.commitTransaction();
        }
        removeAllOlderThan(realm, now, cacheTtl);
    }

    public static void removeAllOlderThan(Realm realm, long now, long cacheTtl) {
        RealmResults<?> actionsToDelete = realm.where(RealmScan.class)
                .lessThan(RealmFields.ScanObject.createdAt, now - cacheTtl)
                .not().equalTo(RealmFields.ScanObject.sentToServerTimestamp, RealmFields.Action.NO_DATE)
                .findAll();

        if (actionsToDelete.size() > 0){
            realm.beginTransaction();
            for (int i = actionsToDelete.size() - 1; i >= 0; i--) {
                actionsToDelete.get(i).removeFromRealm();
            }
            realm.commitTransaction();
        }
    }

    public static class RealmScanObjectTypeAdapter extends ISO8601TypeAdapter<RealmScan> {

        public RealmScanObjectTypeAdapter(String dateFormatString) {
            super(dateFormatString);
        }

        @Override
            public void write(JsonWriter out, RealmScan value) throws IOException {
                out.beginObject();
                out.name("bid").value(value.getBid());
                out.name("trigger").value(value.getTrigger());
                out.name("dt").value(iso8601Format.format(value.getEventTime()));
                out.endObject();
            }

            @Override
            public RealmScan read(JsonReader in) throws IOException {
                throw new IllegalArgumentException("you must not use this to read a RealmScanObject");
            }

    }
}
