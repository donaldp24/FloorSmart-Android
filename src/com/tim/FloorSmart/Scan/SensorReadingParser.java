package com.tim.FloorSmart.Scan;

import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by donald on 5/15/14.
 */
public class SensorReadingParser implements ReadingParser {

    private int kUuidOffset = 0;
    private int kRHValueOffset = 4;
    private int kTempValueOffset = 6;
    private int kBatteryLevelValueOffset = 8;
    private int kDepthModeValueOffset = 9;
    private int kGravityValueOffset = 10;
    private int kMaterialValueOffset = 11;
    private int kMCValueOffset = 12;

    @Override
    public int correctEndian(int value) {
        return ((((value) & 0xFF00) >> 8) + (((value) & 0xFF) << 8));
    }

    @Override
    public HashMap<String, Object> parseData(byte[] manufactureData, int offset) {
        HashMap<String, Object> sensorData = new HashMap<String, Object>();
        CommonMethods.Log("SensorReadingParser");

        int rhValueOffset = kRHValueOffset + offset;
        int tempValueOffset = kTempValueOffset + offset;
        int batteryLevelValueOffset = kBatteryLevelValueOffset + offset;
        int depthModeValueOffset = kDepthModeValueOffset + offset;
        int gravityValueOffset = kGravityValueOffset + offset;
        int materialValueOffset = kMaterialValueOffset + offset;
        int mcValueOffset = kMCValueOffset + offset;

        String uuidString = uuidFromData(manufactureData, offset);

        int rh = manufactureData[rhValueOffset + 1] * 0xFF + manufactureData[rhValueOffset];
        rh = correctEndian(rh);
        int temp = manufactureData[tempValueOffset + 1] * 0xFF + manufactureData[tempValueOffset];
        temp = correctEndian(temp);
        int mc = manufactureData[mcValueOffset + 1] * 0xFF + manufactureData[mcValueOffset];
        mc = correctEndian(mc);

        int batteryLevel = manufactureData[batteryLevelValueOffset];
        int depth = manufactureData[depthModeValueOffset];
        int gravity = manufactureData[gravityValueOffset];
        int material = manufactureData[materialValueOffset];

        String readingTimeStamp = CommonMethods.date2str(new Date(), CommonDefs.DATETIME_FORMAT);

        float convrh = RHFromBytes(rh);
        float convtemp = temperatureFromBytes(temp);
        sensorData.put(kSensorDataReadingTimestampKey, readingTimeStamp);
        sensorData.put(kSensorDataUuidKey, uuidString);
        sensorData.put(kSensorDataRHKey, rh);
        sensorData.put(kSensorDataConvRHKey, convrh);
        sensorData.put(kSensorDataTemperatureKey, temp);
        sensorData.put(kSensorDataConvTempKey, convtemp);
        sensorData.put(kSensorDataBatteryKey, batteryLevel);
        sensorData.put(kSensorDataDepthKey, depth);
        sensorData.put(kSensorDataGravityKey, gravity);
        sensorData.put(kSensorDataMaterialKey, material);
        sensorData.put(kSensorDataMCKey, mc);

        CommonMethods.Log("SensorReadingParser parsing result ; RH:%hu(%.2f), Temp:%hu(%.2f), BT:%hhu, D:%hhu, SG:%hhu, MT:%hhu, MC:%hu", rh, convrh, temp, convtemp, batteryLevel, depth, gravity, material, mc);

        // return immutable dictionary
        return sensorData;
    }

    private  String uuidFromData(byte[] data, int offset)
    {
        String uuidString = "";

        int uuidOffset = kUuidOffset + offset;

        for(int i = 0; i < 4; i++)
        {
            byte temp = data[uuidOffset + (i * 1)];
            uuidString += String.format("%02X",temp);
        }
        // return immutable string
        return uuidString;
    }

    private float RHFromBytes(int rh)
    {
        // There is a problem with this method and the temperature method;
        // The value calculated should be to 0.1 precision, so the calculation and return value
        // should be a floating point (float) value, and when displayed on screen the
        // value should be something like 72.4, etc...
        // as it is, since you are casting to a UInt16 value, the "tenths" place gets rounded to nearest "ones" unit
        // with no 0.1 precision (i.e. precision = 0 in your case, but should equal 1

        // bytes need to be swapped
    /*
    if(CFByteOrderGetCurrent() == CFByteOrderLittleEndian) {
        rh = CFSwapInt16BigToHost(rh);
    }
     */
        float convrh = (-6.0f + (125.0f * rh / 65536.0f));
        //  rh = (UInt16)roundf(-6.0f + (125.0f * (rh/256 + (rh & 0xff) * 256) / 65536.0f));
        return convrh;
    }

    private float temperatureFromBytes(int temp)
    {
        float temperature = temp;
        // bytes need to be swapped
    /*
    if(CFByteOrderGetCurrent() == CFByteOrderLittleEndian) {
        temperature = CFSwapInt16BigToHost(temperature);
    }
     */
        temperature = (-46.85f + (175.72f * temperature / 65536.0f));  // celsius
        //temperature = temperature * 1.8f + 32.0f;  // convert to fahrenheit
        return temperature;
    }
}
