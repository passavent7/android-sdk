package com.sensorberg.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String uuidWithoutDashesString(UUID uuid){
        return uuid.toString().replace("-", "").toLowerCase();
    }

    public static String addUuidDashes(String uuidStringWithoutDashes){
        if (uuidStringWithoutDashes.contains("-")){
            return uuidStringWithoutDashes;
        }
        return uuidStringWithoutDashes.substring(0, 8) + "-" +
                uuidStringWithoutDashes.substring(8, 12) + "-" +
                uuidStringWithoutDashes.substring(12, 16) + "-" +
                uuidStringWithoutDashes.substring(16, 20) + "-" +
                uuidStringWithoutDashes.substring(20, 32);
    }
}
