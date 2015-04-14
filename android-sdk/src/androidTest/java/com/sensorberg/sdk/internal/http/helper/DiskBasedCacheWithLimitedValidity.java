package com.sensorberg.sdk.internal.http.helper;

import com.android.sensorbergVolley.VolleyLog;
import com.android.sensorbergVolley.toolbox.DiskBasedCache;

import java.io.File;

public class DiskBasedCacheWithLimitedValidity extends DiskBasedCache {

    public DiskBasedCacheWithLimitedValidity(File rootDirectory) {
        super(rootDirectory);
    }

    @Override
    public synchronized void put(String key, Entry entry) {
        long now = System.currentTimeMillis();
        VolleyLog.wtf("etag:%s, ttl:%s, timediff:%s", entry.etag, entry.ttl, now - entry.ttl);
        entry.ttl = now + 100;
        super.put(key, entry);
    }
}
