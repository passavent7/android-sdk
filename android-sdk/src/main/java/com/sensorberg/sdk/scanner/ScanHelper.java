package com.sensorberg.sdk.scanner;

import android.util.Pair;

import com.sensorberg.sdk.model.BeaconId;

/**
 * Class {@link ScanHelper} provides convenience methods for {@link com.sensorberg.sdk.scanner.Scanner}. It is intended for internal use only.
 */
public final class ScanHelper {
    private static final byte[] ADVERTISEMENT_HEADER = new byte[]{(byte) 0xFF, 0x4C, 0x00, 0x02};
    private static final int ADVERTISEMENT_HEADER_LENGTH = ADVERTISEMENT_HEADER.length;
    private static final int MINIMUM_BEACON_PACKET_LENGTH = 26;

    // From: http://stackoverflow.com/questions/18906988/what-is-the-ibeacon-bluetooth-profile
    //
    // From: Core Bluetooth Spec v4, Vol 3, Part C, 11
    // 0x00, 0x01, 0x02: Length of the following structure
    // 0x01, 0x01, 0x01: Some flags - see Vol 3, Part C, 18.1
    // 0x02, 0x01, 0x1A: LE General Discoverable
    // 0x03, 0x01, 0x1A: Length of the following structure
    // 0x04, 0x01, 0xFF: Manufacturer specific data - see Vol 3, Part C, 18.11
    // 0x05, 0x02, 0x4C00: Apple Inc. - see https://www.bluetooth.org/en-us/specification/assigned-numbers/company-identifiers
    // 0x07, 0x01, 0x02: Data type (02 = Beacon)
    // 0x08, 0x01, 0x15: Data length
    // 0x09, 0x10, 0xD57092ACDFAA446C8EF3C81AA22815B5: UUID
    // 0x19, 0x02, 0x0010: Major id
    // 0x1B, 0x02, 0x0001: Minor id
    // 0x1D, 0x01, 0x80: Calibrated Tx Power (at 1m?)
    //
    // 0x1E, 0x01, 0x0F: Data length
    // 0x1F, 0x0F, 0x094A6841435D584448554A45544D65: Unknown (\tJhAC]XDHUJETMe)
    // 0x2E, 0x01, 0x05: Data length
    // 0x2F, 0x05, 0x1250002C01: Unknown
    // 0x34, 0x0A, 020A0000000000000000: Unknown
    //
    // 02:01:06:1A:FF:4C:00:02:15:D5:70:92:AC:DF:AA:44:6C:8E:F3:C8:1A:A2:28:15:B5:00:10:00:01:80 :0F:09:4A:68:41:43:5D:58:44:48:55:4A:45:54:4D:65:05:12:50:00:2C:01:02:0A:00:00:00:00:00:00:00:00
    private ScanHelper() {
        // Private constructor to make class purely static
    }

    public static Pair<BeaconId, Integer> getBeaconID(byte[] advertisement) {
        try {
            int packetLength;
            int offset = 0;
            do {
                packetLength = advertisement[offset]; //first byte has the length
                offset++;
                if (packetLength >= MINIMUM_BEACON_PACKET_LENGTH && matchesBeaconHeader(advertisement, offset)){
                    return toBeaconData(advertisement, offset + ADVERTISEMENT_HEADER_LENGTH + 1); //ignore one more byte after the header
                }
                offset += packetLength; //let` move to the next package

            } while (packetLength > 0 && offset < advertisement.length);
            return null;
        } catch (ArrayIndexOutOfBoundsException e){ //let´s be extra safe here in case the beacon advertising is broken.
            return null;
        }
    }

    private static Pair<BeaconId, Integer> toBeaconData(byte[] advertisement, int offset) {
        byte[] beaconIdBytes = new byte[20];
        java.lang.System.arraycopy(advertisement, offset, beaconIdBytes, 0, 20);
        BeaconId beaconId = new BeaconId(beaconIdBytes);
        int calibratedTXpower = (int) advertisement[offset + 20];
        return Pair.create(beaconId, calibratedTXpower);
    }


    private static boolean matchesBeaconHeader(byte[] advertisement, int offset) {
        for (int i = 0 ; i < ADVERTISEMENT_HEADER.length ; i++){
            if (advertisement[i + offset] != ADVERTISEMENT_HEADER[i]){
                return false;
            }
        }
        return true;
    }
}
