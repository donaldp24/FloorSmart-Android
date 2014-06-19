package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;
import com.tim.FloorSmart.Scan.ScanManagerListener;

public class SplashScreen extends Activity implements ScanManagerListener {

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;
    private Handler handler;

    final int LOADINGVIEW_TIMEOUT = 3000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mainLayout = (RelativeLayout)findViewById(R.id.RLSplashRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLSplashRoot));
                            bInitialized = true;
                        }
                    }
                }
        );

        manager = ScanManager.managerWithListner(this, this);
        manager.startScan();

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0)
                {
                    startActivity(new Intent(SplashScreen.this, JobsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            }
        };

        handler.sendEmptyMessageDelayed(0, LOADINGVIEW_TIMEOUT);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Initialize Global
        GlobalData.initialize(this);
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
