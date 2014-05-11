package com.tim.FloorSmart.Database;

import java.util.Date;
import java.util.ArrayList;


/**
 * Created by donald on 5/8/14.
 */
public class FSReading {

    // material mode
    public static final long FSMaterialModeWood = 0;
    public static final long FSMaterialModeRelative = 1;
    public static final long FSMaterialModeConcrete = 2;

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

    public FSReading() {
        this.readID = 0;
        this.readLocProductID = 0;
        this.readTimestamp = new Date();
        this.readUuid = "";
        this.readRH = 0;
        this.readConvRH = 0.0;
        this.readTemp = 0;
        this.readConvTemp = 0.0;
        this.readBattery = 0;
        this.readDepth = 0;
        this.readGravity = 0;
        this.readMaterial = 0;
        this.readMC = 0;
    }

    public float getEmcValue() {
        float T = FSReading.getFTemperature((float)this.readConvTemp);
        float H = (float)this.readConvRH / 100.f;
        double W = 330 + 0.452 * T + 0.00415 * T * T;
        double K = 0.791 + 0.000463 * T - 0.000000844 * T * T;
        double KH = K * H;
        double K1 = 6.34 + 0.000775 * T - 0.0000935 * T * T;
        double K2 = 1.09 + 0.0284 * T - 0.0000904 * T * T;
        double M = 1800 / W * (KH / (1 - KH) + ((K1 * KH + 2 * K1 * K2 * K * K * H * H) / (1 + K1 * KH + K1 * K2 * K * K * H * H)));

        return (float)M;
    }

    public float getRealMCValue() {
        if (this.readMaterial == FSReading.FSMaterialModeWood)
            return (float)this.readMC / 10.f;
        else
            return (float)this.readMC;
    }

    public String getDisplayRealMCValue() {
        if (this.readMaterial == FSMaterialModeWood)
            return String.format("%.1f", this.getRealMCValue());
        else
        return String.format("%ld", (long)this.getRealMCValue());
    }

    public static String getDisplayDepth(long depth) {
        if (depth == 1)
            return "1/4";
        return "3/4";
    }

    public static String getDisplayMaterial(long material) {
        if (material == FSMaterialModeWood)
            return "Wood";
        else if (material == FSMaterialModeRelative)
            return "Relative";
        return "Concrete";
    }

    public static float getCTemperature(float fTemp) {
        float ctemp = (fTemp - 32) * 5 / 9.f;
        return ctemp;
    }

    public static float getFTemperature(float ctemp) {
        float ftemp = ctemp * 9 / 5.f + 32;
        return ftemp;
    }

    public static String getDisplayMC(float mc) {
        return String.format("%.1f", mc);
    }

    public static float getMCAvg(ArrayList<FSReading> array) {
        float mcsum = 0f;
        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            mcsum += data.getRealMCValue();
        }
        if (array.size() == 0)
            return 0.0f;
        return mcsum / array.size();
    }

    public static float getMCMax(ArrayList<FSReading> array) {
        float mcmax = 0f;
        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            if (mcmax < data.getRealMCValue())
            mcmax = data.getRealMCValue();
        }
        return mcmax;
    }

    public static float getMCMin(ArrayList<FSReading> array) {
        float mcmin = 0f;
        if (array.size() == 0)
            return mcmin;
        FSReading data1 = array.get(0);
        mcmin = data1.getRealMCValue();

        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            if (mcmin > data.getRealMCValue())
                mcmin = data.getRealMCValue();
        }
        return mcmin;
    }

    public static float getRHAvg(ArrayList<FSReading> array) {
        float rhsum = 0f;
        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            rhsum += data.readConvRH;
        }
        if (array.size() == 0)
            return 0.0f;
        return rhsum / array.size();
    }

    public static float getTempAvg(ArrayList<FSReading> array) {
        float tempsum = 0f;
        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            tempsum += data.readConvTemp;
        }
        if (array.size() == 0)
            return 0.0f;
        return tempsum / array.size();
    }

    public static float getEmcAvg(ArrayList<FSReading> array) {
        float emcsum = 0f;
        for (int i = 0; i < array.size(); i++) {
            FSReading data = array.get(i);
            emcsum += data.getEmcValue();
        }
        if (array.size() == 0)
            return 0.0f;
        return emcsum / array.size();
    }
}
