package com.tim.FloorSmart.Global;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by donald on 5/8/14.
 */
public class CommonMethods {

    /*
     * show alert with title and content of alert
     */
    public static void showAlertUsingTitle(String titleString, String messageString) {
        // have to re-implement.
    }

    /*
     * convert date to string
     * @param
     *  formatString : format of date : "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"
     */
    public static String date2str(Date date, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    /*
     * convert string to date
     * @param
     *  formatString : format of date ex) "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"
     */
    public static Date str2date(String dateString, String formatString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            CommonMethods.Log("error parsing date : " + e.getMessage());
        }
        return convertedDate;
    }

    /*
     * play tap sound
     */
    public static void playTapSound() {
        // have to re-implement
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
        float sqm = 0.09290304f * sqft;
        return sqm;
    }

    /*
     * convert square meter to square feet
     */
    public static float sqm2sqft(float sqm) {
        float sqft = sqm / 0.09290304f;
        return sqft;
    }

    public static void Log(String formatMsg, Object ...values) {
        Log.v(CommonDefs.TAG, String.format(formatMsg, values));
    }

    public static int compareOnlyDate(Date date1, Date date2)
    {
        String str1 = CommonMethods.date2str(date1, CommonDefs.DATE_FORMAT);
        String str2 = CommonMethods.date2str(date2, CommonDefs.DATE_FORMAT);
        Date date3 = CommonMethods.str2date(str1, CommonDefs.DATE_FORMAT);
        Date date4 = CommonMethods.str2date(str2, CommonDefs.DATE_FORMAT);
        return date3.compareTo(date4);
    }
}
