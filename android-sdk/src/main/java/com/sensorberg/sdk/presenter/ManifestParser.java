package com.sensorberg.sdk.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ManifestParser {

    public static final String actionString = "com.sensorberg.android.PRESENT_ACTION";
    public static final Intent actionIntent = new Intent();
    static {
        actionIntent.setAction(actionString);
    }


    public static List<BroadcastReceiver> findBroadcastReceiver(Context context) {
        List<BroadcastReceiver> result = new ArrayList<>();

        List<ResolveInfo> infos = context.getPackageManager().queryBroadcastReceivers(actionIntent, PackageManager.SIGNATURE_MATCH);
        for (ResolveInfo resolveInfo : infos) {
            try {
                if (!resolveInfo.activityInfo.processName.endsWith(".sensorberg")){
                    continue;
                }
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) Class.forName(resolveInfo.activityInfo.name).newInstance();
                result.add(broadcastReceiver);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {

            }
        }
        return result;
    }

    public static String get(String key, Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle == null){
                return null;
            }
            return bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
