package com.sensorberg.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SensorbergCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent loggingIntent = new Intent(context, SensorbergService.class);
        if (intent.getData().getAuthority().endsWith("73676723741")) {
            loggingIntent.putExtra(SensorbergService.EXTRA_GENERIC_TYPE, SensorbergService.MSG_TYPE_ENABLE_LOGGING);
        } else if (intent.getData().getAuthority().endsWith("73676723740")) {
            loggingIntent.putExtra(SensorbergService.EXTRA_GENERIC_TYPE, SensorbergService.MSG_TYPE_DISABLE_LOGGING);
        }
        context.startService(loggingIntent);
    }
}
