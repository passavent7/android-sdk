package com.sensorberg.sdk.resolver;

import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;
import com.sensorberg.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CurrentResolutions implements Iterable<Resolution> {
    List<Resolution> resolutions = new ArrayList<Resolution>();

    /**
     * not needed anymore because we are resolving all events, mit be needed later.
     */
    public Collection<Resolution> entryResolutionsForPossibleExit(final Resolution resolution) {
        if (resolution.configuration.getScanEvent().getEventMask() == ScanEventType.ENTRY.getMask()){
            return Collections.emptyList();
        }

        List<Resolution> values = ListUtils.filter(resolutions, new ListUtils.Filter<Resolution>() {
            @Override
            public boolean matches(Resolution object) {
                return object.configuration.getScanEvent().getBeaconId().equals(resolution.configuration.getScanEvent().getBeaconId());
            }
        });

        return values;
    }

    /**
     * not needed anymore because we are resolving all events, mit be needed later.
     */
    public void removeAll(Collection<Resolution> resolutionsToCancel) {
        resolutions.removeAll(resolutionsToCancel);
    }

    public boolean contains(Resolution resolution) {
        return resolutions.contains(resolution);
    }

    public void add(Resolution resolution) {
        resolutions.add(resolution);
    }

    public boolean remove(Resolution resolution) {
        return resolutions.remove(resolution);
    }

    @Override
    public Iterator<Resolution> iterator() {
        return resolutions.iterator();
    }

    public void clear() {
        resolutions.clear();
    }

    public Resolution get(ScanEvent scanEvent) {
        for (Resolution resolution : this) {
            if (resolution.configuration.getScanEvent().equals(scanEvent)){
                return resolution;
            }
        }
        return null;
    }
}
