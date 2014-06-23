package com.tim.FloorSmart.Global;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.tim.FloorSmart.ReadingActivity;

/**
 * Created by donald on 5/8/14.
 */
public class GlobalData {

    private static final String KEY_TEMPERATURE = "temp";
    private static final String KEY_AREA = "area";
    private static final String KEY_DATEFORMAT = "dateformat";
    private static final String KEY_ISSAVED = "issaved";
    private static final String KEY_JOBID = "jobid";
    private static final String KEY_LOCID = "locid";
    private static final String KEY_LOCPRODUCTID = "locproductid";

    // temperature constant
    public static final int TEMP_FAHRENHEIT = 0;
    public static final int TEMP_CELSIUS = 1;

    // area constant
    public static final int AREA_FEET = 0;
    public static final int AREA_METER = 1;

    // dateformat constant
    public static final String DATEFORMAT_US = "MM/dd/yyyy";
    public static final String DATEFORMAT_EU = "dd.MM.yyyy";

    private static final String APP_SHARED_PREFS = "Floorsmart";

    public static int MODE_SELECT_JOB = 0;
    public static int MODE_SELECT_LOCATION = 1;
    public static int MODE_SELECT_PRODUCT = 2;

    // singleton
    private static GlobalData _sharedData;
    public static Context _mainContext;
    public static Activity _currentActivity;
    public static boolean bFromRecord = false;
    public static String pdfCacheDir = "";

    // properties (setting)
    public int  settingTemp; // temperature unit
    public int settingArea; // area unit
    public String settingDateFormat; // date format

    public boolean isSaved;         // start recording or not
    public long selectedJobID;       // recording job id
    public long selectedLocID;       // recording location id
    public long selectedLocProductID;    // recording id of product of location.

    private SharedPreferences shared_preferences;
    private SharedPreferences.Editor shared_preferences_editor;

    private GlobalData(Context context) {
        this.shared_preferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.shared_preferences_editor = shared_preferences.edit();
    }

    public static void initialize(Context context) {
        if (_sharedData == null) {
            _sharedData = new GlobalData(context);

            // load settings;
            _sharedData.loadInitData();
        }
    }

    public static GlobalData sharedData() {
        return _sharedData;
    }

    /*
     * load properties from preferences
     */
    private void loadInitData() {
        settingTemp = shared_preferences.getInt(KEY_TEMPERATURE, TEMP_FAHRENHEIT);
        settingArea = shared_preferences.getInt(KEY_AREA, AREA_FEET);
        settingDateFormat = shared_preferences.getString(KEY_DATEFORMAT, DATEFORMAT_US);
        /*
        isSaved = shared_preferences.getBoolean(KEY_ISSAVED, false);
        selectedJobID = shared_preferences.getLong(KEY_JOBID, 0);
        selectedLocID = shared_preferences.getLong(KEY_LOCID, 0);
        selectedLocProductID = shared_preferences.getLong(KEY_LOCPRODUCTID, 0);
        */
        this.resetSavedData();
    }

    public void setSettingTemp(int settingTemp)
    {
        this.settingTemp = settingTemp;
        shared_preferences_editor.putInt(KEY_TEMPERATURE, settingTemp);
        shared_preferences_editor.commit();
    }

    public void setSettingArea(int settingArea)
    {
        this.settingArea = settingArea;
        shared_preferences_editor.putInt(KEY_AREA, settingArea);
        shared_preferences_editor.commit();
    }

    public void setSettingDateFormat(String settingDateFormat)
    {
        this.settingDateFormat = settingDateFormat;
        shared_preferences_editor.putString(KEY_DATEFORMAT, this.settingDateFormat);
        shared_preferences_editor.commit();
    }


    /*
     * set selected variables and store it to preferences
     */
    public void saveSelection(long selectedJobID, long selectedLocID, long selectedLocProductID) {
        this.selectedJobID = selectedJobID;
        this.selectedLocID = selectedLocID;
        this.selectedLocProductID = selectedLocProductID;

        shared_preferences_editor.putLong(KEY_JOBID, selectedJobID);
        shared_preferences_editor.putLong(KEY_LOCID, selectedLocID);
        shared_preferences_editor.putLong(KEY_LOCPRODUCTID, selectedLocProductID);
        shared_preferences_editor.commit();
    }

    /*
     * reset saved selection
     *  set ids to 0
     */
    public void resetSavedData() {
        this.isSaved = false;
        this.selectedJobID = 0;
        this.selectedLocID = 0;
        this.selectedLocProductID = 0;

        shared_preferences_editor.putBoolean(KEY_ISSAVED, this.isSaved);
        shared_preferences_editor.putLong(KEY_JOBID, selectedJobID);
        shared_preferences_editor.putLong(KEY_LOCID, selectedLocID);
        shared_preferences_editor.putLong(KEY_LOCPRODUCTID, selectedLocProductID);
        shared_preferences_editor.commit();
    }

    /*
     * reset recording status (isSaved = 0)
     */
    public void pauseRecording() {
        this.isSaved = false;
        shared_preferences_editor.putBoolean(KEY_ISSAVED, this.isSaved);
        shared_preferences_editor.commit();
    }

    /*
     * set recording status (isSaved = 1)
     */
    public void startRecording() {
        this.isSaved = true;
        shared_preferences_editor.putBoolean(KEY_ISSAVED, this.isSaved);
        shared_preferences_editor.commit();
    }

    public String getTempUnit()
    {
        if (this.settingTemp == TEMP_FAHRENHEIT)
            return "F";
        else
            return "C";
    }

    public float sqft2sqm(float sqft)
    {
        float sqm = 0.09290304f * sqft;
        return sqm;
    }

    public float sqm2sqft(float sqm)
    {
        float sqft = sqm / 0.09290304f;
        return sqft;
    }
}
