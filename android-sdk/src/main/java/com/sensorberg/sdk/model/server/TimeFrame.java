package com.sensorberg.sdk.model.server;

import java.util.Date;

public class TimeFrame {
    /**
     * can be null, if so, only the end is important.
     */
    public Date start;
    /**
     * can be null, if so, only the start is important.
     */
    public Date end;

    public TimeFrame(Long startMillis, Long endMillis) {
        if(startMillis != null){
            start = new Date(startMillis);
        }
        if (endMillis != null){
            end = new Date(endMillis);
        }
    }

    public boolean valid(long now) {
        return (start == null || now >= start.getTime()) &&
                 (end == null || now <= end.getTime());
    }
}
