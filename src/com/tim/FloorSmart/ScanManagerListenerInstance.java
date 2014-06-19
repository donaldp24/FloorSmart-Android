package com.tim.FloorSmart;

import com.tim.FloorSmart.Database.DataManager;
import com.tim.FloorSmart.Database.FSReading;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ReadingParser;
import com.tim.FloorSmart.Scan.ScanManager;
import com.tim.FloorSmart.Scan.ScanManagerListener;

import java.util.HashMap;

/**
 * Created by donald on 6/19/14.
 */
public class ScanManagerListenerInstance implements ScanManagerListener {
    public static ScanManagerListenerInstance _instance = null;

    private ScanManagerListenerInstance()
    {
        //
    }

    public static ScanManagerListenerInstance sharedInstance() {
        if (_instance == null)
            _instance = new ScanManagerListenerInstance();
        return _instance;
    }

    @Override
    public void didFindSensor(ScanManager scanManager, HashMap<String, Object> sensorData)
    {
        GlobalData globalData = GlobalData.sharedData();
        if (globalData.isSaved == false)
            return;


        // save new data

        FSReading reading = new FSReading();
        reading.readID = 0;
        reading.readLocProductID = globalData.selectedLocProductID;
        reading.readTimestamp = CommonMethods.str2date((String)sensorData.get(ReadingParser.kSensorDataReadingTimestampKey), CommonDefs.DATETIME_FORMAT);
        reading.readUuid = (String)sensorData.get(ReadingParser.kSensorDataUuidKey);
        reading.readRH = (Long)(sensorData.get(ReadingParser.kSensorDataRHKey));
        reading.readConvRH = (Double)(sensorData.get(ReadingParser.kSensorDataConvRHKey));
        reading.readTemp = (Long)(sensorData.get(ReadingParser.kSensorDataTemperatureKey));
        reading.readConvTemp = (Double)(sensorData.get(ReadingParser.kSensorDataConvTempKey));
        reading.readBattery = (Long)(sensorData.get(ReadingParser.kSensorDataBatteryKey));
        reading.readDepth = (Long)(sensorData.get(ReadingParser.kSensorDataDepthKey));
        reading.readGravity = (Long)(sensorData.get(ReadingParser.kSensorDataGravityKey));
        reading.readMaterial = (Long)(sensorData.get(ReadingParser.kSensorDataMaterialKey));
        reading.readMC = (Long)(sensorData.get(ReadingParser.kSensorDataMCKey));
        DataManager.sharedInstance(null).addReadingToDatabase(reading);
    }

    @Override
    public void didFindThirdPackage(ScanManager scanManager)
    {
        GlobalData globalData = GlobalData.sharedData();
        if (globalData.isSaved == false)
            return;
    }

    @Override
    public void scanManagerDidStartScanning(ScanManager scanManager)
    {
        GlobalData globalData = GlobalData.sharedData();
        if (globalData.isSaved == false)
            return;
    }
}
