package com.tim.FloorSmart.Database;

import java.sql.Date;
import java.util.ArrayList;


/**
 * Created by donald on 5/8/14.
 */
public class FSReading {

    // material mode
    public static final long FSMATERIALMODE_WOOD = 0;
    public static final long FSMATERIALMODE_RELATIVE = 1;
    public static final long FSMATERIALMODE_CONCRETE = 2;

    // properties
    public long readID;
    public long readLocProductID;
    public Date readTimestamp;
    public String readUuid;
    public long readRH;
    public double readConvRH; //0-100
    public long readTemp;
    public double readConvTemp; // 0-130
    public long readBattery;
    public long readDepth;
    public long readGravity;
    public long readMaterial;
    public long readMC; //0 - 1000

    public float getEmcValue() {
        //
    }

    public float getRealMCValue() {
        //
    }

    public String getDisplayRealMCValue() {
        //
    }

    public static String getDisplayDepth(long depth) {
        //
    }

    public static String getDisplayMaterial(long material) {
        //
    }

    public static float getCTemperature(float ftemp) {
        //
    }

    public static float getFTemperature(float ctemp) {
        //
    }

    public static String getDisplayMC(float mc) {
        //
    }

    public static float getMCAvg(ArrayList<FSReading> array) {
        //
    }

    public static float getMCMax(ArrayList<FSReading> array) {
        //
    }

    public static float getMCMin(ArrayList<FSReading> array) {
        //
    }

    public static float getRHAvg(ArrayList<FSReading> array) {
        //
    }

    public static float getTempAvg(ArrayList<FSReading> array) {
        //
    }

    public static float getEmcAvg(ArrayList<FSReading> array) {
        //
    }
}
