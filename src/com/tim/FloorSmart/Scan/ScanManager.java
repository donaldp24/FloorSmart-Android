package com.tim.FloorSmart.Scan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by donald on 5/15/14.
 */
public class ScanManager {

    private static ScanManager _sharedInstance = null;
    private ScanManagerListener listener = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final long SCAN_PERIOD = 5000;
    private boolean mCalled = false;

    private static final int kPackageID = 0x9133B9DE;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord)
                {
                    //byte[] records = {0x02,0x01,0x04,0x0F,0xFF,0x91,0x33,0xB9,0xDE,0x55,0xE2,0x68,0xC0,0x64,0x01,0x32,0x00,0x00,0x4E,0x15,0x09,0x57,0x61,0x67,0x6E,0x65,0x72,0x4D,0x65,0x74,0x65,0x72,0x42,0x4C,0x45,0x52,0x65,0x61,0x64,0x65,0x72,0x02,0x0A,0x00,0x00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};

                    if (mCalled)
                        return;

                    mCalled = true;

                    CommonMethods.Log("didDiscoverPeripheral:name=[%s]", device.getName());
                    CommonMethods.Log("scanRecord : %s", ScanManager.bytesToHex(scanRecord));

                    byte[] manufacturedData = null;
                    byte[] emulatorServiceUuid = null;
                    HashMap<String, Object> sensorData = null;
                    int offset = 0;


                    // parsing scanRecord
                    int index = 0;
                    while (index < scanRecord.length)
                    {
                        int length = scanRecord[index++];

                        // Done once we run out of records
                        if (length == 0)
                            break;

                        int type = scanRecord[index];

                        //Done if our record isn't a valid type
                        if (type == 0)
                            break;

                        byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);

                        // manufactureData
                        if ((int)(type & 0xff) == 0xff)
                            manufacturedData = Arrays.copyOfRange(scanRecord, index + 1, index + length);
                        if (type == 0x07)
                        {
                            emulatorServiceUuid = Arrays.copyOfRange(scanRecord, index + 1, index + length);
                            // reverse
                            ArrayUtils.reverse(emulatorServiceUuid);
                        }

                        index += length;
                    }

                    // parse sensor data
                    if (manufacturedData != null)
                    {
                        String dataToParseString = bytesToHex(manufacturedData);
                        CommonMethods.Log("Debug output manufacture data: %s", dataToParseString);

                        if (manufacturedData.length < 4 ||
                                byteArrayToIntWithBigEndian(subArray(manufacturedData, 0, 4)) != kPackageID)
                        {
                            CommonMethods.Log("Third party package was received.");
                            if (listener != null)
                                listener.didFindThirdPackage(ScanManager.this);
                        }
                        else
                        {
                            offset = 0;
                            SensorReadingParser parser = new SensorReadingParser();
                            sensorData = parser.parseData(manufacturedData, offset);

                            if (listener != null)
                                listener.didFindSensor(ScanManager.this, sensorData);
                        }
                    }
                    else if (emulatorServiceUuid != null)
                    {
                        CommonMethods.Log("Debug output sensor data(uuid1): %s", bytesToHex(emulatorServiceUuid));

                        if(emulatorServiceUuid.length < 4 ||
                                byteArrayToIntWithBigEndian(subArray(emulatorServiceUuid, 0, 4)) != kPackageID)
                        {
                            CommonMethods.Log("Third party package was received.");
                            if (listener != null)
                                listener.didFindThirdPackage(ScanManager.this);
                        }
                        else
                        {
                            offset = 0;
                            EmulatorReadingParser parser = new EmulatorReadingParser();
                            sensorData = parser.parseData(emulatorServiceUuid, offset);

                            if (listener != null)
                                listener.didFindSensor(ScanManager.this, sensorData);
                        }
                    }
                    else
                    {
                        CommonMethods.Log("Third party package was received.");
                        if (listener != null)
                            listener.didFindThirdPackage(ScanManager.this);
                    }

                    synchronized (mBluetoothAdapter) {
                        if (mScanning)
                        {
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            CommonMethods.Log("Stopped scan on callback");
                        }
                    }
                }
            };

    private boolean mStopped = false;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();

    private ScanManager()
    {
        //
    }

    /**
     * create scan manager with context and listener
     *
     * @param ctx
     * @param listener
     * @return ScanManager instance if success, null if cannot get bluetooth adapter
     */
    public static ScanManager managerWithListner(Context ctx, ScanManagerListener listener)
    {
        if (_sharedInstance == null)
        {
            ScanManager manager = new ScanManager();

            manager.listener = listener;

            // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
            // BluetoothAdapter through BluetoothManager.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            manager.mBluetoothAdapter = bluetoothManager.getAdapter();

            // Checks if Bluetooth is supported on the device.
            if (manager.mBluetoothAdapter == null) {
                _sharedInstance = null;
            }
            else
                _sharedInstance = manager;
        }
        return _sharedInstance;
    }

    public boolean testBleFeature(Activity activity)
    {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(enableBtIntent);

            return false;
        }

        return true;
    }

    public void startScan()
    {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (mBluetoothAdapter)
                {
                    if (mScanning)
                    {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        CommonMethods.Log("Stopped scan before starting");
                    }

                    if (mStopped == false)
                    {
                        mHandler.postDelayed(this, SCAN_PERIOD);
                        mCalled = false;
                        if (mBluetoothAdapter.startLeScan(mLeScanCallback))
                        {
                            mScanning = true;
                            if (ScanManager.this.listener != null)
                                ScanManager.this.listener.scanManagerDidStartScanning(ScanManager.this);
                            CommonMethods.Log("delay posted Started scan");
                        }
                        else
                        {
                            mScanning = false;
                            CommonMethods.Log("failed to start scan");
                        }
                    }
                    else
                    {
                        mScanning = false;
                        CommonMethods.Log("cancel delay posting with stopping ");
                    }
                }
            }
        }, SCAN_PERIOD);

        synchronized (mBluetoothAdapter)
        {
            mStopped = false;
            if (mScanning == false)
            {
                mCalled = false;
                if (mBluetoothAdapter.startLeScan(mLeScanCallback))
                {
                    mScanning = true;
                    if (this.listener != null)
                        this.listener.scanManagerDidStartScanning(this);
                    CommonMethods.Log("Started scan");
                }
                else
                {
                    mScanning = false;
                    CommonMethods.Log("failed to start scan");
                }
            }
        }
    }

    public void stopScan()
    {
        synchronized (mBluetoothAdapter)
        {
            mStopped = true;
            if (mScanning)
            {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                CommonMethods.Log("Stopped scan");
            }
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int byteArrayToIntWithBigEndian(byte[] b) {
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
                    | (b[3] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

        return 0;
    }

    public static int byteArrayToIntWithLittleEndian(byte[] b) {
        if (b.length == 4)
            return b[3] << 24 | (b[2] & 0xff) << 16 | (b[1] & 0xff) << 8
                    | (b[0] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[1] & 0xff) << 8 | (b[0] & 0xff);

        return 0;
    }

    public static byte[] subArray(byte[] b, int offset, int length) {
        byte[] sub = new byte[length];
        for (int i = offset; i < offset + length; i++) {
            try {
                sub[i - offset] = b[i];
            } catch (Exception e) {

            }
        }
        return sub;
    }
}
