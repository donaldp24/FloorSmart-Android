package com.tim.FloorSmart.Scan;

/**
 * Created by donald on 6/19/14.
 */
class ArrayUtils {

    public static void reverse(final byte[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        byte tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final short[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        short tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final int[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        int tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final long[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        long tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final float[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        float tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final double[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        double tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static void reverse(final Object[] pArray) {
        if (pArray == null) {
            return;
        }
        int i = 0;
        int j = pArray.length - 1;
        Object tmp;
        while (j > i) {
            tmp = pArray[j];
            pArray[j] = pArray[i];
            pArray[i] = tmp;
            j--;
            i++;
        }
    }

    public static boolean equals(final byte[] pArrayA, final int pOffsetA, final byte[] pArrayB, final int pOffsetB, final int pLength) {
        final int lastIndexA = pOffsetA + pLength;
        if(lastIndexA > pArrayA.length) {
            throw new ArrayIndexOutOfBoundsException(pArrayA.length);
        }

        final int lastIndexB = pOffsetB + pLength;
        if(lastIndexB > pArrayB.length) {
            throw new ArrayIndexOutOfBoundsException(pArrayB.length);
        }

        for(int a = pOffsetA, b = pOffsetB; a < lastIndexA; a++, b++) {
            if(pArrayA[a] != pArrayB[b]) {
                return false;
            }
        }

        return true;
    }

}
