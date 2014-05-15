package com.tim.FloorSmart.Scan;

/**
 * Created by donald on 5/15/14.
 */
public interface ScanManagerListener {
    public void didFindSensor(ScanManager scanManager, byte []sensorData);
    public void didFindThirdPackage(ScanManager scanManager, byte []thirdData);
    public void scanManagerDidStartScanning(ScanManager scanManager);

}
