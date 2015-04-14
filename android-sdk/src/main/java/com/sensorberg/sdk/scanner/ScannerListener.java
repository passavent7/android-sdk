package com.sensorberg.sdk.scanner;

/**
 * Interface {@link ScannerListener} defines an {@link Object} that can handle {@link Scanner} events.
 */
public interface ScannerListener {
    /**
     * Event-method being called when {@link ScanEvent}s have been detected.
     *
     * @param event the {@link com.sensorberg.sdk.scanner.ScanEvent} detected
     */
    void onScanEventDetected(ScanEvent event);
}