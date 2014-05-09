package com.tim.FloorSmart.Database;

/**
 * Created by donald on 5/8/14.
 */
public class FSProduct {

    // type constants
    public static final long FSPRODUCTTYPE_FINISHED = 0;
    public static final long FSPRODUCTTYPE_SUBFLOOR = 1;

    // properties
    public long productID;
    public String productName;
    public long productType;
    public long productDeleted;
}
