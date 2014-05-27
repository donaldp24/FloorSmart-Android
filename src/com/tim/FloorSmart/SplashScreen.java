package com.tim.FloorSmart;

import android.app.Activity;
import android.os.Bundle;
import com.tim.FloorSmart.Scan.ScanManager;
import com.tim.FloorSmart.Scan.ScanManagerListener;

public class SplashScreen extends Activity implements ScanManagerListener {

    private ScanManager manager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        manager = ScanManager.managerWithListner(this, this);
        manager.startScan();

    }

    public void didFindSensor(ScanManager scanManager, byte []sensorData) {
        //
    }

    public void didFindThirdPackage(ScanManager scanManager, byte []thirdData) {
        //
    }

    public void scanManagerDidStartScanning(ScanManager scanManager) {
        //
    }
}
