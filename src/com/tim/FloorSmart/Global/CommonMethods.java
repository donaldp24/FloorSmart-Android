package com.tim.FloorSmart.Global;

import android.util.Log;

import java.sql.Date;

/**
 * Created by donald on 5/8/14.
 */
public class CommonMethods {

    /*
     * show alert with title and content of alert
     */
    public static void showAlertUsingTitle(String titleString, String messageString) {
        //
    }

    /*
     * convert date to string
     */
    public static String date2str(Date date, String formatString) {
        return "";
    }

    /*
     * convert string to date
     */
    public static Date str2date(String dateString, String formatString) {
        //
    }

    /*
     * play tap sound
     */
    public static void playTapSound() {
        //
    }

    /*
     * get string for temperature unit ("F" or "C")
     */
    public static String getTempUnit(int temp_unit) {
        if (temp_unit == GlobalData.TEMP_FAHRENHEIT)
            return "F";
        else
            return "C";
    }

    /*
     * convert square feet to square meter
     */
    public static float sqft2sqm(float sqft) {
        //
    }

    /*
     * convert square meter to square feet
     */
    public static float sqm2sqft(float sqm) {
        //
    }

    public static void Log(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void Log(String msg) {
        Log.v(GlobalData.TAG, msg);
    }
}
