package com.sensorberg.sdk.model;

import com.google.gson.TypeAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public abstract class ISO8601TypeAdapter<T> extends TypeAdapter<T>{

    protected final SimpleDateFormat iso8601Format;

    public ISO8601TypeAdapter(String dateFormatString) {
        this.iso8601Format = new SimpleDateFormat(dateFormatString, Locale.US);
        this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}
