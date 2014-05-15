package com.tim.FloorSmart.Scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import com.tim.FloorSmart.Global.CommonMethods;

/**
 * Created by donald on 5/15/14.
 */
public class ScanManager {

    private ScanManagerListener listener = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final long SCAN_PERIOD = 5000;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord)
                {
                    CommonMethods.Log("%s",String.format("FloorSmart:didDiscoverPeripheral:name=[%s]",
                            device.getName()));

                    /*
                    String name = [advertisementData valueForKey:CBAdvertisementDataLocalNameKey];
                    NSData *manufacturedData = [advertisementData valueForKey:CBAdvertisementDataManufacturerDataKey];
                    NSDictionary *serviceDict = [advertisementData valueForKey:CBAdvertisementDataServiceDataKey];
                    NSArray *serviceUUIDsArray = [advertisementData valueForKey:CBAdvertisementDataServiceUUIDsKey];
                    NSArray *overflowServiceUUIDsArray = [advertisementData valueForKey:CBAdvertisementDataOverflowServiceUUIDsKey];
                    NSNumber *transmitPower = [advertisementData valueForKey:CBAdvertisementDataTxPowerLevelKey];

                    NSMutableData* dataToParse = nil;
                    NSInteger offset = 0;
                    NSDictionary *sensorData;

                    if (manufacturedData)

                        NSString * dataToParseString = @"";
                        for (int ix=0; ix<manufacturedData.length; ix++)
                        {
                            unsigned char c;
                            [manufacturedData getBytes:&c range:NSMakeRange(ix, 1)];
                            dataToParseString = [dataToParseString stringByAppendingFormat:@"%02X ",c];
                        }
                        NSLog(@"Debug output manufacture data: %@",dataToParseString);

                        if(![[manufacturedData subdataWithRange:NSMakeRange(0, 4)] isEqualToData:
                        [NSData dataWithBytes:&kPackageID length:4]])
                        {
                            NSLog(@"Third party package was received.");
                            [[self delegate] scanManager:self didFindThirdPackage:manufacturedData];
                            return;
                        }

                        dataToParse = (NSMutableData*)manufacturedData;
                        offset = 0;


                        SensorReadingParser *parser = [[SensorReadingParser alloc] init];
                        sensorData = [parser parseData:dataToParse  withOffset:offset];

                    }
                    else
                    {
                        ///note, that uuid1 is 128-bit uuid that contain first 16 bytes according to the specification: 3-bytes flag, length byte and so on.
                        ///uuid2(which is 16-bit uuid) contains last byte of S2T field and battery level byte. This is where specification order is violated.
                        ///Last 3 uuids contains full Serial number order.
                        NSArray* uuidsArray = advertisementData[CBAdvertisementDataServiceUUIDsKey];

                        CBUUID* uuid1 = [uuidsArray firstObject];

                        NSString *outputString = @"";
                        for (int ix = 0 ; ix < [uuid1 data].length; ix++)
                        {
                            unsigned char c;
                            [[uuid1 data] getBytes:&c range:NSMakeRange(ix, 1)];
                            outputString = [outputString stringByAppendingFormat:@"%02X ",c];
                        }
                        NSLog(@"Debug output sensor data(uuid1): %@",outputString);



                        UInt32 packageID = kPackageID;
                        ///uuid comes right after flag, length and dataType bytes.
                        if([[uuid1 data] length] < 4 || ![[[uuid1 data] subdataWithRange:NSMakeRange(0, 4)] isEqualToData:
                        [NSData dataWithBytes:&packageID length:4]])
                        {
                            NSLog(@"Third party package was received.");
                            [[self delegate] scanManager:self didFindThirdPackage:[uuid1 data]];
                            return;
                        }

                        NSData* firstPackage = [uuid1 data];
                        dataToParse = [NSMutableData dataWithData:firstPackage];
                        offset = 0;

                        EmulatorReadingParser *parser = [[EmulatorReadingParser alloc] init];
                        sensorData = [parser parseData:dataToParse  withOffset:offset];
                    }


                    [[self delegate] scanManager:self
                    didFindSensor:sensorData];
                    */
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
        ScanManager manager = new ScanManager();

        manager.listener = listener;

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        manager.mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (manager.mBluetoothAdapter == null) {
            return null;
        }
        return manager;
    }

    /**
     * start scanning
     * 1 - start scanning
     * 2 - stop scanning after SCAN_PERIOD seconds
     * 3 - repeat 1 and 2 until calling stopScan().
     */
    public void startScan()
    {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                if (mStopped == false)
                {
                    mHandler.postDelayed(this, SCAN_PERIOD);
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
                else
                {
                    CommonMethods.Log("Stopped scan");
                }
            }
        }, SCAN_PERIOD);

        mStopped = false;
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

    public void stopScan()
    {
        mStopped = true;
    }
}
