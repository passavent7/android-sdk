package com.sensorberg.sdk.resolver;

import android.os.Message;

import com.sensorberg.sdk.Logger;
import com.sensorberg.sdk.internal.Platform;
import com.sensorberg.sdk.internal.RunLoop;
import com.sensorberg.sdk.internal.Transport;

import java.util.ArrayList;
import java.util.List;

public final class Resolver implements RunLoop.MessageHandlerCallback {

    final Object listenersMonitor = new Object();
    final Object resolutionsMonitor = new Object();

    final List<ResolverListener> listeners = new ArrayList<ResolverListener>();

    final CurrentResolutions currentResolutions = new CurrentResolutions();
    private final Transport transport;
    private RunLoop runLoop;

    public Resolver(Platform platform) {
        runLoop = platform.getResolverRunLoop(this);
        transport = platform.getTransport();
    }

    /**
     * Adds a {@link ResolverListener} to the {@link List} of {@link ResolverListener}s.
     *
     * @param listener the {@link ResolverListener} to be added
     */
    public void addResolverListener(ResolverListener listener) {
        synchronized (listenersMonitor) {
            listeners.add(listener);
        }
    }

    /**
     * Creates a new {@link Resolution}; the {@link ResolutionConfiguration} is copied and therefore cannot be changed after creation of the {@link Resolution}.
     *
     * @param resolutionConfiguration the {@link ResolutionConfiguration} to configure the {@link Resolution} with
     * @return the {@link Resolution} created
     */
    public Resolution createResolution(ResolutionConfiguration resolutionConfiguration) {
        return (new Resolution(this, resolutionConfiguration, transport));
    }

    @Override
    public void handleMessage(Message queueEvent) {
        synchronized (resolutionsMonitor) {
            switch (queueEvent.arg1) {
                case ResolverEvent.RESOLUTION_START_REQUESTED: {
                    Resolution resolution = (Resolution) queueEvent.obj;

                    if (currentResolutions.contains(resolution)) {
                        Logger.log.beaconResolveState(resolution.configuration.getScanEvent(), "request already running, not stating a new one");
                        return;
                    }

                    currentResolutions.add(resolution);
                    resolution.queryServer();

                    break;
                }
                default: {
                    throw new IllegalArgumentException("unhandled default case");
                }
            }
        }
    }

    void onResolutionFailed(Resolution resolution, Throwable cause) {
        synchronized (listenersMonitor) {
            for (ResolverListener listener : listeners) {
                listener.onResolutionFailed(resolution, cause);
            }
        }
        currentResolutions.remove(resolution);
    }

    void onResolutionFinished(Resolution resolution, List<BeaconEvent> beaconEvents) {
        synchronized (listenersMonitor) {
            for (ResolverListener listener : listeners) {
                listener.onResolutionsFinished(beaconEvents);
            }
        }
        currentResolutions.remove(resolution);
    }

    /**
     * Removes a {@link ResolverListener} from the {@link List} of {@link ResolverListener}s.
     *
     * @param listener the {@link ResolverListener} to be removed
     */
    public void removeResolverListener(ResolverListener listener) {
        synchronized (listenersMonitor) {
            listeners.remove(listener);
        }
    }

    /**
     * Starts a {@link Resolution}.
     *
     * @param resolution the {@link Resolution} to be started
     */
    public void startResolution(Resolution resolution) {
        runLoop.add(ResolverEvent.asMessage(ResolverEvent.RESOLUTION_START_REQUESTED, resolution));
    }

    public void retry(ResolutionConfiguration configuration) {
        Resolution resolution = currentResolutions.get(configuration.getScanEvent());
        if (resolution == null){
            resolution = createResolution(configuration);
            Logger.log.beaconResolveState(configuration.getScanEvent(), "creating a new resolution, we have been in the background for too long");
        }

        resolution.configuration.retry++;
        Logger.log.beaconResolveState(resolution.configuration.getScanEvent(), "performing the retry No." + resolution.configuration.retry);
        resolution.queryServer();
    }
}
