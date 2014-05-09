package com.tim.FloorSmart.Global;

/**
 * Created by donald on 5/8/14.
 */
public class GlobalData {

    // temperature constant
    public static final int TEMP_FAHRENHEIT = 0;
    public static final int TEMP_CELSIUS = 1;

    // area constant
    public static final int AREA_FEET = 0;
    public static final int AREA_METER = 1;

    // dateformat constant
    public static final String DATEFORMAT_US = "MM/dd/yyyy";
    public static final String DATEFORMAT_EU = "dd.MM.yyyy";
    public static final String DATEFORMAT_DB = "yyyy-MM-dd HH:mm:ss";

    // singleton
    private static GlobalData _sharedData;

    // properties (setting)
    public int settingTemp; // temperature unit
    public int settingArea; // area unit
    public String settingDateFormat; // date format


    public boolean isSaved;         // start recording or not
    public long selectedJobID;       // recording job id
    public long selectedLocID;       // recording location id
    public long selectedLocProductID;    // recording id of product of location.

    // tag
    public static final String TAG = "FloorSmart";


    private GlobalData() {
        //
    }

    public static GlobalData sharedData() {
        if (_sharedData == null) {
            _sharedData = new GlobalData();

            // load settings;
            _sharedData.loadInitData();
        }
        return _sharedData;
    }

    /*
     * load properties from preferences
     */
    private void loadInitData() {
    }

    /*
     * set selected variables and store it to preferences
     */
    public void saveSelection(long selectedJobID, long selectedLocID, long selectedLocProductID) {

    }

    /*
     * reset saved selection
     *  set ids to 0
     */
    public void resetSavedData() {
        //
    }

    /*
     * reset recording status (isSaved = 0)
     */
    public void pauseRecording() {
        //
    }

    /*
     * set recording status (isSaved = 1)
     */
    public void startRecording() {
        //
    }
}
