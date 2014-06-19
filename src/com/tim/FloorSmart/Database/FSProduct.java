package com.tim.FloorSmart.Database;

import com.tim.FloorSmart.Global.CommonDefs;

/**
 * Created by donald on 5/8/14.
 */
public class FSProduct {

    // type constants
    public static final int FSPRODUCTTYPE_FINISHED = 0;
    public static final int FSPRODUCTTYPE_SUBFLOOR = 1;

    // properties
    public long productID;
    public String productName;
    public long productType;
    public long productDeleted;

    public FSProduct()
    {
        productID = 0;
        productName = "";
        productType = FSPRODUCTTYPE_FINISHED;
        productDeleted = 0;
    }

    public static String getDisplayProductType(int productType)
    {
        if (productType == FSPRODUCTTYPE_FINISHED)
            return CommonDefs.PRODUCT_TYPE_FINISHED;
        else
            return CommonDefs.PRODUCT_TYPE_SUBFLOOR;
    }
}
