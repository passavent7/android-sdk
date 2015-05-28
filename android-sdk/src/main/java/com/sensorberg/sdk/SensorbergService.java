package com.sensorberg.sdk;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.sensorberg.sdk.background.ScannerBroadcastReceiver;
import com.sensorberg.sdk.internal.AndroidPlatform;
import com.sensorberg.sdk.internal.FileHelper;
import com.sensorberg.sdk.internal.Platform;
import com.sensorberg.sdk.internal.URLFactory;
import com.sensorberg.sdk.presenter.ManifestParser;
import com.sensorberg.sdk.resolver.BeaconEvent;
import com.sensorberg.sdk.resolver.ResolutionConfiguration;
import com.sensorberg.sdk.resolver.ResolverConfiguration;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.text.TextUtils.isEmpty;

public class SensorbergService extends Service {

    public static final String META_DATA_API_KEY = "com.sensorberg.sdk.ApiKey";
    public static final String META_DATA_RESOLVER_URL = "com.sensorberg.sdk.resolverURL";



    public static final int MSG_APPLICATION_IN_FOREGROUND   = 1;
    public static final int MSG_APPLICATION_IN_BACKGROUND   = 2;
    public static final int MSG_SET_API_TOKEN               = 3;
    public static final int MSG_PRESENT_ACTION              = 4;
    public static final int MSG_SHUTDOWN                    = 6;
    public static final int MSG_PING                        = 7;
    public static final int MSG_BLUETOOTH                   = 8;
    public static final int MSG_SDK_SCANNER_MESSAGE         = 9;
    public static final int MSG_UPLOAD_HISTORY              = 10;
    public static final int MSG_BEACON_LAYOUT_UPDATE        = 11;


    public static final int GENERIC_TYPE_BEACON_ACTION              = 1001;
    public static final int GENERIC_TYPE_RETRY_RESOLVE_SCANEVENT    = 1002;

    public static final int MSG_REGISTER_PRESENTATION_DELEGATE      = 100;
    public static final int MSG_UNREGISTER_PRESENTATION_DELEGATE    = 101;
    public static final int MSG_SETTINGS_UPDATE                     = 102;
    public static final int MSG_TYPE_DISABLE_LOGGING                = 103;
    public static final int MSG_TYPE_ENABLE_LOGGING                 = 104;
    public static final int MSG_TYPE_SET_RESOLVER_ENDPOINT          = 105;
    public static final int MSG_TYPE_SET_PAYLOAD                    = 106;


    public static final String MSG_SET_API_TOKEN_TOKEN = "com.sensorberg.android.sdk.message.setApiToken.apiTokenString";
    public static final String MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL = "com.sensorberg.android.sdk.intent.resolverEndpoint";
    public static final String MSG_TYPE_SET_PAYLOAD_PAYLOAD = "com.sensorberg.android.sdk.message.setPayload.payload";
    public static final String MSG_PRESENT_ACTION_BEACONEVENT = "com.sensorberg.android.sdk.message.presentBeaconEvent.beaconEvent";
    public static final String SERVICE_CONFIGURATION = "serviceConfiguration";




    Platform platform;

    private static class MSG {
        public static String stringFrom(int what) {
            switch (what) {
                case MSG_APPLICATION_IN_FOREGROUND:
                    return "MSG_APPLICATION_IN_FOREGROUND";
                case MSG_APPLICATION_IN_BACKGROUND:
                    return "MSG_APPLICATION_IN_BACKGROUND";
                case MSG_SET_API_TOKEN:
                    return "MSG_SET_API_TOKEN";
                case MSG_PRESENT_ACTION:
                    return "MSG_PRESENT_ACTION";
                case MSG_REGISTER_PRESENTATION_DELEGATE:
                    return "MSG_REGISTER_PRESENTATION_DELEGATE";
                case MSG_UNREGISTER_PRESENTATION_DELEGATE:
                    return "MSG_UNREGISTER_PRESENTATION_DELEGATE";
                case MSG_SHUTDOWN:
                    return "MSG_SHUTDOWN";
                case MSG_PING:
                    return "MSG_PING";
                case MSG_BLUETOOTH:
                    return "MSG_BLUETOOTH";
                case MSG_SETTINGS_UPDATE:
                    return "MSG_SETTINGS_UPDATE";
                case GENERIC_TYPE_BEACON_ACTION:
                    return "GENERIC_TYPE_BEACON_ACTION";
                case GENERIC_TYPE_RETRY_RESOLVE_SCANEVENT:
                    return "GENERIC_TYPE_RETRY_RESOLVE_SCANEVENT";
                case MSG_TYPE_DISABLE_LOGGING:
                    return "MSG_TYPE_DISABLE_LOGGING";
                case MSG_TYPE_ENABLE_LOGGING:
                    return "MSG_TYPE_ENABLE_LOGGING";
                case MSG_SDK_SCANNER_MESSAGE:
                    return "MSG_SDK_SCANNER_MESSAGE";
                case MSG_UPLOAD_HISTORY:
                    return "MSG_UPLOAD_HISTORY";
                case MSG_BEACON_LAYOUT_UPDATE:
                    return "MSG_BEACON_LAYOUT_UPDATE";
                case MSG_TYPE_SET_RESOLVER_ENDPOINT:
                    return "MSG_TYPE_SET_RESOLVER_ENDPOINT";
                case MSG_TYPE_SET_PAYLOAD:
                    return "MSG_TYPE_SET_PAYLOAD";
                default:
                    return "unknown message" + what;
            }
        }
    }

    public static final String EXTRA_API_KEY = "com.sensorberg.android.sdk.intent.apiKey";
    public static final String EXTRA_BLUETOOTH_STATE = "com.sensorberg.android.sdk.intent.bluetoothState";


    public static final String EXTRA_GENERIC_WHAT = "com.sensorberg.android.sdk.intent.generic.what";
    public static final String EXTRA_GENERIC_TYPE = "com.sensorberg.android.sdk.intent.generic.type";
    public static final String EXTRA_GENERIC_INDEX = "com.sensorberg.android.sdk.intent.generic.index";
    public static final String EXTRA_START_SERVICE = "com.sensorberg.android.sdk.intent.startService";
    public static final String EXTRA_MESSENGER = "com.sensorberg.android.sdk.intent.messenger";


    private final MessengerList presentationDelegates = new MessengerList();

    private InternalApplicationBootstrapper bootstrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        platform = new AndroidPlatform(getApplicationContext());
        Logger.log.logServiceState("onCreate");
        String resolverURL = ManifestParser.get(META_DATA_RESOLVER_URL, this);
        if (resolverURL != null) {
            URLFactory.setLayoutURL(resolverURL);
        }
        JodaTimeAndroid.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log.logServiceState("onStartCommand");

        if (!platform.isBluetoothLowEnergySupported() || !platform.hasMinimumAndroidRequirements()){
            Logger.log.logError("platform.isBluetoothLowEnergySupported or platform.hasMinimumAndroidRequirement, shutting down.");
            stopSelf();
            return START_NOT_STICKY;
        }

        List<BroadcastReceiver> broadcastReceiver = platform.getBroadcastReceiver();
        if (broadcastReceiver.isEmpty()){
            Logger.log.logError("no BroadcastReceiver registered for Action:com.sensorberg.android.PRESENT_ACTION");
            stopSelf();
            return START_NOT_STICKY;
        } else {
            platform.registerBroadcastReceiver(broadcastReceiver);
        }


        if (intent != null) {
            Logger.log.serviceHandlesMessage(MSG.stringFrom(intent.getIntExtra(SensorbergService.EXTRA_GENERIC_TYPE, -1)));

            handleDebuggingIntent(intent, this);

            if (shouldShutdownService(intent)){
                stopSelf();
                return START_NOT_STICKY;
            }

            if (bootstrapper == null){
                updateDiskConfiguration(intent);
            }

            if (intent.hasExtra(EXTRA_START_SERVICE)) {
                if (bootstrapper == null) {
                    String apiKey = intent.getStringExtra(EXTRA_API_KEY);

                    if (isEmpty(apiKey)){
                        apiKey = ManifestParser.get(META_DATA_API_KEY, this);
                    }
                    if (!isEmpty(apiKey)) {

                        platform.getTransport().setApiToken(apiKey);

                    }
                    if (platform.getTransport().getApiToken() != null){
                        bootstrapper = new InternalApplicationBootstrapper(platform);
                        bootstrapper.startScanning();
                        return START_STICKY;
                    }
                    else {
                        Logger.log.logError("No ApiToken present, we´e not able to work.");
                    }
                } else {
                    bootstrapper.startScanning();
                    Logger.log.logError("start intent was sent, but the scanner was already set up");
                    return START_STICKY;
                }
                Logger.log.logError("Intent to start the service was not correctly sent. not starting the service");
                stopSelf();
                return START_NOT_STICKY;
            }

            if (intent.hasExtra(SensorbergService.EXTRA_GENERIC_TYPE)) {
                if (bootstrapper == null) {
                    createBootstrapperFromDiskConfiguration();
                    if (bootstrapper == null) {
                        Logger.log.logError("could set up the scanning infrastructure");
                        stopSelf();
                        return START_NOT_STICKY;
                    }
                }

                int what = intent.getIntExtra(SensorbergService.EXTRA_GENERIC_TYPE, -1);
                Logger.log.serviceHandlesMessage(MSG.stringFrom(what));
                switch (what) {
                    case MSG_BEACON_LAYOUT_UPDATE:
                        bootstrapper.updateBeaconLayout();
                        break;
                    case MSG_SDK_SCANNER_MESSAGE:
                        Bundle message = intent.getParcelableExtra(EXTRA_GENERIC_WHAT);
                        bootstrapper.scanner.handlePlatformMessage(message);
                        break;
                    case MSG_SETTINGS_UPDATE:
                        bootstrapper.updateSettings();
                        break;
                    case MSG_UPLOAD_HISTORY:
                        bootstrapper.uploadHistory();
                        break;
                    case GENERIC_TYPE_BEACON_ACTION: {
                        try {
                            BeaconEvent beaconEvent = intent.getParcelableExtra(EXTRA_GENERIC_WHAT);
                            int index = intent.getIntExtra(EXTRA_GENERIC_INDEX, 0);
                            Logger.log.beaconResolveState(beaconEvent, "end of the delay, now showing the BeaconEvent");
                            bootstrapper.presentEventDirectly(beaconEvent, index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case GENERIC_TYPE_RETRY_RESOLVE_SCANEVENT: {
                        ResolutionConfiguration configuration = intent.getParcelableExtra(EXTRA_GENERIC_WHAT);
                        bootstrapper.retryScanEventResolve(configuration);
                        break;
                    }
                    case MSG_APPLICATION_IN_FOREGROUND: {
                        bootstrapper.hostApplicationInForeground();
                        break;
                    }
                    case MSG_APPLICATION_IN_BACKGROUND: {
                        bootstrapper.hostApplicationInBackground();
                        break;
                    }
                    case MSG_SET_API_TOKEN: {
                        if (intent.hasExtra(MSG_SET_API_TOKEN_TOKEN)) {
                            String apiToken = intent.getStringExtra(MSG_SET_API_TOKEN_TOKEN);
                            if (platform.getTransport().setApiToken(apiToken)) {
                                bootstrapper.unscheduleAllPendingActions();
                            }
                        }
                        break;
                    }
                    case MSG_TYPE_SET_RESOLVER_ENDPOINT: {
                        if (intent.hasExtra(MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL)) {
                            try {
                                URL resolverURL = (URL) intent.getSerializableExtra(MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL);
                                URLFactory.setLayoutURL(resolverURL.toString());
                            } catch (Exception e){
                                Logger.log.logError("Could not parse the extra " + MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL, e);
                            }
                        }
                        break;
                    }
                    case MSG_REGISTER_PRESENTATION_DELEGATE:{
                        if (intent.hasExtra(EXTRA_MESSENGER)) {
                            Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
                            presentationDelegates.add(messenger);
                        }
                        break;
                    }
                    case MSG_UNREGISTER_PRESENTATION_DELEGATE: {
                        if (intent.hasExtra(EXTRA_MESSENGER)) {
                            Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
                            presentationDelegates.remove(messenger);
                        }
                        break;
                    }
                    case MSG_PING:{
                        bootstrapper.startScanning();
                        break;
                    }
                    case MSG_BLUETOOTH:{
                        if (intent.hasExtra(EXTRA_BLUETOOTH_STATE)) {
                            boolean bluetoothOn = intent.getBooleanExtra(EXTRA_BLUETOOTH_STATE, true);
                            if (bluetoothOn) {
                                bootstrapper.startScanning();
                            } else {
                                bootstrapper.stopScanning();
                            }
                        }
                        break;
                    }
                }
            }
        } else {
            Logger.log.logError("there was no intent in onStartCommand we must assume we are beeing restarted due to a kill event");
            createBootstrapperFromDiskConfiguration();
            if (bootstrapper != null) {
                bootstrapper.startScanning();
            }
        }
        return START_STICKY;
    }

    private boolean handleDebuggingIntent(Intent intent, Context context) {
        switch (intent.getIntExtra(EXTRA_GENERIC_TYPE, -1)) {
            case MSG_TYPE_DISABLE_LOGGING: {
                Logger.log = Logger.QUIET_LOG;
                Toast.makeText(context, "Log disabled " + platform.getHostApplicationId(), Toast.LENGTH_SHORT).show();
                return true;
            }
            case MSG_TYPE_ENABLE_LOGGING: {
                Logger.enableVerboseLogging();
                Toast.makeText(context, "Log enabled " + platform.getHostApplicationId(), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void updateDiskConfiguration(Intent intent) {
        if (intent.hasExtra(EXTRA_GENERIC_TYPE)) {
            int type = intent.getIntExtra(EXTRA_GENERIC_TYPE, -1);
            ServiceConfiguration diskConf = (ServiceConfiguration) FileHelper.getContentsOfFileOrNull(platform.getFile(SERVICE_CONFIGURATION));
            if (diskConf == null) {
                diskConf = new ServiceConfiguration(null);
            }
            Logger.log.serviceHandlesMessage(MSG.stringFrom(type));
            switch (type) {
                case MSG_TYPE_SET_PAYLOAD: {
                    if (intent.hasExtra(MSG_TYPE_SET_PAYLOAD_PAYLOAD)){
                        String payload = intent.getStringExtra(MSG_TYPE_SET_PAYLOAD_PAYLOAD);
                        try {
                            JSONObject payloadJSON = new JSONObject(payload);
                            platform.getTransport().setPayload(payloadJSON);
                            diskConf.resolverConfiguration.setPayload(payloadJSON);
                        } catch (JSONException e) {
                            Logger.log.logError("payload was not proper JSONObject", e);
                        }
                    } else {
                        Logger.log.logError("No payload content was provided");
                    }
                    break;
                }
                case MSG_TYPE_SET_RESOLVER_ENDPOINT: {
                    if (intent.hasExtra(MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL)){
                        if (diskConf.resolverConfiguration == null){
                            diskConf.resolverConfiguration = new ResolverConfiguration();
                        }
                        URL resolverURL = (URL) intent.getSerializableExtra(MSG_SET_RESOLVER_ENDPOINT_ENDPOINT_URL);
                        diskConf.resolverConfiguration.setResolverLayoutURL(resolverURL);
                        URLFactory.setLayoutURL(diskConf.resolverConfiguration.getResolverLayoutURL().toString());
                    }
                    break;
                }
                case MSG_SET_API_TOKEN: {
                    if (intent.hasExtra(MSG_SET_API_TOKEN_TOKEN)) {
                        String apiToken = intent.getStringExtra(MSG_SET_API_TOKEN_TOKEN);
                        if (diskConf.resolverConfiguration == null){
                            diskConf.resolverConfiguration = new ResolverConfiguration();
                        }
                        diskConf.resolverConfiguration.setApiToken(apiToken);
                    }
                    break;
                }
            }
            persistConfiguration(diskConf);
        }
    }


    private boolean shouldShutdownService(Intent intent) {
        if (intent.hasExtra(EXTRA_GENERIC_TYPE)){
            int type = intent.getIntExtra(EXTRA_GENERIC_TYPE, -1);
            switch (type){
                case MSG_SHUTDOWN:{
                    Logger.log.serviceHandlesMessage(MSG.stringFrom(type));
                    MinimalBootstrapper minimalBootstrapper = bootstrapper != null ? bootstrapper : new MinimalBootstrapper(platform);
                    platform.removeFile(SERVICE_CONFIGURATION);
                    ScannerBroadcastReceiver.setManifestReceiverEnabled(false, this);
                    GenericBroadcastReceiver.setManifestReceiverEnabled(false, this);

                    minimalBootstrapper.unscheduleAllPendingActions();
                    minimalBootstrapper.stopScanning();
                    minimalBootstrapper.stopAllScheduledOperations();
                    bootstrapper = null;
                    return true;
                }
            }
        }
        return false;
    }

    private void createBootstrapperFromDiskConfiguration() {
        try {
            ServiceConfiguration diskConf = (ServiceConfiguration) FileHelper.getContentsOfFileOrNull(platform.getFile(SERVICE_CONFIGURATION));
            if (diskConf == null){
                diskConf = new ServiceConfiguration(new ResolverConfiguration());
            }
            if (diskConf.resolverConfiguration.getResolverLayoutURL() != null){
                URLFactory.setLayoutURL(diskConf.resolverConfiguration.getResolverLayoutURL().toString());
            }
            platform.getTransport().setPayload(diskConf.resolverConfiguration.getPayload());
            if (diskConf.isComplete()) {
                platform.getTransport().setApiToken(diskConf.resolverConfiguration.apiToken);
                bootstrapper = new InternalApplicationBootstrapper(platform);
            } else{
                Logger.log.logError("configuration from disk could not be loaded or is not complete");
            }
        } catch (Exception e) {
            Logger.log.logError("something went wrong when loading the configuration from disk:" + e);
            e.printStackTrace();
        }
    }

    private void persistConfiguration(ServiceConfiguration conf) {
        platform.write(conf, SERVICE_CONFIGURATION);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.log.logServiceState("onUnbind");
        return false;
    }

    @Override
    public void onDestroy() {
        Logger.log.logServiceState("onDestroy");
        if (bootstrapper != null) {
            bootstrapper.stopScanning();
        }
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Logger.log.logServiceState("onTaskRemoved");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    class MessengerList {
        private final Set<Messenger> storage = new HashSet<Messenger>();

        public void add(Messenger replyTo) {
            storage.clear();
            storage.add(replyTo);
            if (storage.size() >= 1) {
                bootstrapper.sentPresentationDelegationTo(this);
            }
        }

        public void remove(Messenger replyTo) {
            storage.remove(replyTo);
            storage.clear();
            if (storage.size() == 0) {
                bootstrapper.sentPresentationDelegationTo(null);
            }
        }

        public void send(BeaconEvent beaconEvent) {
            for (Messenger messenger : storage) {
                try {
                    Message message = Message.obtain(null, SensorbergService.MSG_PRESENT_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MSG_PRESENT_ACTION_BEACONEVENT, beaconEvent);
                    message.setData(bundle);
                    messenger.send(message);
                } catch (DeadObjectException d) {
                    //we need to remove this object!!
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
