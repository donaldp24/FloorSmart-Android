package com.tim.FloorSmart.Scan;

import java.util.HashMap;

/**
 * Created by donald on 5/15/14.
 */
public interface ReadingParser {
    public static final String kSensorDataUuidKey = "uuid";
    public static final String kSensorDataRHKey = "rh";
    public static final String kSensorDataConvRHKey = "convrh";
    public static final String kSensorDataTemperatureKey = "temp";
    public static final String kSensorDataConvTempKey = "convtemp";
    public static final String kSensorDataBatteryKey = "battery";
    public static final String kSensorDataDepthKey = "depth";
    public static final String kSensorDataGravityKey = "gravity";
    public static final String kSensorDataMaterialKey = "material";
    public static final String kSensorDataMCKey = "mc";
    public static final String kSensorDataReadingTimestampKey = "timestamp";

    public int correctEndian(int value);
    public HashMap<String, Object> parseData(byte[] manufactureData, int offset);
}
