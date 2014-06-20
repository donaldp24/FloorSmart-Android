package com.tim.FloorSmart;

import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import com.tim.FloorSmart.Database.DataManager;
import com.tim.FloorSmart.Database.FSReading;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ReadingParser;
import com.tim.FloorSmart.Scan.ScanManager;
import com.tim.FloorSmart.Scan.ScanManagerListener;
import android.os.Handler;

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
        reading.readRH = (Integer)(sensorData.get(ReadingParser.kSensorDataRHKey));
        reading.readConvRH = (Float)(sensorData.get(ReadingParser.kSensorDataConvRHKey));
        reading.readTemp = (Integer)(sensorData.get(ReadingParser.kSensorDataTemperatureKey));
        reading.readConvTemp = (Float)(sensorData.get(ReadingParser.kSensorDataConvTempKey));
        reading.readBattery = (Integer)(sensorData.get(ReadingParser.kSensorDataBatteryKey));
        reading.readDepth = (Integer)(sensorData.get(ReadingParser.kSensorDataDepthKey));
        reading.readGravity = (Integer)(sensorData.get(ReadingParser.kSensorDataGravityKey));
        reading.readMaterial = (Integer)(sensorData.get(ReadingParser.kSensorDataMaterialKey));
        reading.readMC = (Integer)(sensorData.get(ReadingParser.kSensorDataMCKey));
        DataManager.sharedInstance().addReadingToDatabase(reading);

        if (GlobalData._currentActivity != null)
        {
            GlobalData._currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (GlobalData._readingActivity != null)
                    {
                        GlobalData._readingActivity.initDateTable();
                        GlobalData._readingActivity.scrolltoEndList();
                        GlobalData._readingActivity.showWarning();
                    }
                    else
                    {
                        Intent readingIntent = new Intent(GlobalData._mainContext, ReadingActivity.class);
                        readingIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, GlobalData.sharedData().selectedLocProductID);
                        readingIntent.putExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, true);
                        GlobalData._currentActivity.startActivity(readingIntent);
                    }
                }
            });
        }
        //
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
