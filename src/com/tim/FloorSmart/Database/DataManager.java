package com.tim.FloorSmart.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.tim.FloorSmart.Database.FMDB.FMDatabase;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.Date;
import java.util.ArrayList;

/**
 * Created by donald on 5/8/14.
 */
public class DataManager {

    // singleton
    private static DataManager _sharedInstance;

    // properties
    private FMDatabase _database;

    public static final String FMD_VERSION = "1.1";
    public static final String FMD_DEFAULT_LOCATIONNAME = "Default";
    public static final String FMD_DEFAULT_PRODUCTNAME = "Default";


    private DataManager(Context ctx) {
        //
        _database = new FMDatabase(ctx);
        _database.open();
    }

    public static DataManager sharedInstance(Context ctx) {
        if (_sharedInstance == null) {
            _sharedInstance = new DataManager(ctx);
        }
        return _sharedInstance;
    }

    public static DataManager sharedInstance() {
        if (_sharedInstance == null) {
            _sharedInstance = new DataManager(GlobalData._mainContext);
        }
        return _sharedInstance;
    }

    // job
    public ArrayList<FSJob> getAllJobs() {
        ArrayList<FSJob> arrJobList = new ArrayList<FSJob>();
        Cursor results = _database.executeCommand("SELECT * FROM tbl_job WHERE deleted = 0");
        if (results.moveToFirst()) {
            do {
                int i = 0;
                FSJob job  = new FSJob();
                job.jobID       = results.getLong(i++); //intForColumn:@"job_id"];
                job.jobName     = results.getString(i++); //[results stringForColumn:@"job_name"];
                job.jobArchived = results.getLong(i++); //[results intForColumn:@"job_archived"];

                arrJobList.add(job);
            } while (results.moveToNext());
        }
        return arrJobList;
    }

    public ArrayList<FSJob> getJobs(long archiveFlag, String searchField) {
        ArrayList<FSJob> arrJobList = new ArrayList<FSJob>();
        if (searchField == null)
            searchField = "";
        String sql = String.format("SELECT * FROM tbl_job WHERE deleted = 0 and job_archived = %d AND job_name like '%s%s%s'", (int)archiveFlag, "%", searchField, "%");
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;

                FSJob job  = new FSJob();
                job.jobID       = results.getLong(i++); //[results intForColumn:@"job_id"];
                job.jobName     = results.getString(i++); // [results stringForColumn:@"job_name"];
                job.jobArchived = results.getLong(i++); //[results intForColumn:@"job_archived"];

                arrJobList.add(job);
            } while (results.moveToNext());
        }
        return arrJobList;
    }

    public boolean isExistSameJob(String jobName) {
        String sql = String.format("SELECT COUNT(*) AS samecount  FROM tbl_job WHERE deleted = 0 and job_name = '%s'", jobName);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do  {
                int count       = results.getInt(0); //[results intForColumn:@"samecount"];
                if (count > 0)
                    return true;
                return false;
            } while (false);
        }
        return false;
    }

    public FSJob getJobFromID(long jobID) {

        Cursor results = _database.executeQuery("SELECT * FROM tbl_job WHERE deleted = 0 and job_id = %d", jobID);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSJob job  = new FSJob();

                job.jobID       = results.getLong(i++); //[results intForColumn:@"job_id"];
                job.jobName     = results.getString(i++); // [results stringForColumn:@"job_name"];
                job.jobArchived = results.getLong(i++); //[results intForColumn:@"job_archived"];

                return job;
            } while (false);
        }
        return null;
    }

    public long addJobToDatabase(FSJob job) {
        ContentValues values = new ContentValues();
        values.put("job_archived", job.jobArchived);
        values.put("job_name", job.jobName);
        values.put("deleted", 0);
        long ret = _database.insert("tbl_job", null, values);
        if (ret < 0)
            return 0;
        return ret;
    }

    public void updateJobToDatabase(FSJob job) {
        String sql = String.format("UPDATE tbl_job SET job_archived = %d , job_name = '%s' WHERE job_id = %d", (int)job.jobArchived, job.jobName, job.jobID);
        _database.executeUpdate(sql);
    }

    public void deleteJobFromDatabase(FSJob job) {
        String sql;
        //sql = [NSString stringWithFormat:@"DELETE FROM tbl_job WHERE job_id = %d", job.jobID];
        sql = String.format("UPDATE tbl_job SET deleted = 1 WHERE job_id = %d", job.jobID);
        _database.executeUpdate(sql);
    }

// location
    public ArrayList<FSLocation> getLocations(long jobID) {
        ArrayList<FSLocation> arrLocList = new ArrayList<FSLocation>();
        String sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_jobid  = %d and location_name != '%s' ", jobID, FMD_DEFAULT_LOCATIONNAME);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocation loc  = new FSLocation();
                loc.locID       = results.getLong(i++); //[results intForColumn:@"location_id"];
                loc.locJobID    = results.getLong(i++); //[results intForColumn:@"location_jobid"];
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                //[arrLocList addObject:loc];
                arrLocList.add(loc);
            } while (results.moveToNext());
        }
        return arrLocList;
    }

    public ArrayList<FSLocation> getLocations(long jobID, boolean isContainDefault) {
        ArrayList<FSLocation> arrLocList = new ArrayList<FSLocation>();
        String sql = "";
        if (isContainDefault)
            sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_jobid  = %d", jobID);
        else
            sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_jobid  = %d and location_name != '%s' ", jobID, FMD_DEFAULT_LOCATIONNAME);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocation loc  = new FSLocation();
                loc.locID       = results.getLong(i++); //[results intForColumn:@"location_id"];
                loc.locJobID    = results.getLong(i++); //[results intForColumn:@"location_jobid"];
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                //[arrLocList addObject:loc];
                arrLocList.add(loc);
            } while (results.moveToNext());
        }
        return arrLocList;
    }

    public boolean isExistSameLocation(long jobID, String locName) {
        String sql = "";
        sql = String.format("SELECT COUNT(*) AS samecount FROM tbl_location WHERE deleted = 0 and location_jobid  = %d and location_name = '%s'", jobID, locName);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int count       = results.getInt(0); //[results intForColumn:@"samecount"];
                if (count > 0)
                    return true;
                return false;
            } while (false);
        }
        return false;
    }

    public FSLocation getLocation(long jobID, String locName) {
        String sql = "";
        sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_jobid  = %d and location_name = '%s'", jobID, locName);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocation loc  = new FSLocation();
                loc.locID       = results.getLong(i++); //[results intForColumn:@"location_id"];
                loc.locJobID    = results.getLong(i++); //[results intForColumn:@"location_jobid"];
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                return loc;
            } while (results.moveToNext());
        }
        return null;
    }

    public FSLocation getLocationFromID(long locID) {
        FSLocation loc = new FSLocation();
        String sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_id = %d", locID);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;

                loc.locID       = results.getLong(i++); //[results intForColumn:@"location_id"];
                loc.locJobID    = results.getLong(i++); //[results intForColumn:@"location_jobid"];
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                return loc;
            } while (results.moveToNext());
        }
        return null;
    }

    public long addLocationToDatabase(FSLocation loc) {
        ContentValues values = new ContentValues();
        values.put("location_jobid", loc.locJobID);
        values.put("location_name", loc.locName);
        values.put("deleted", 0);
        long ret = _database.insert("tbl_location", null, values);
        if (ret < 0)
            return 0;
        return ret;
    }

    public void updateLocToDatabase(FSLocation loc) {
        String sql;
        sql = String.format("UPDATE tbl_location SET location_name = '%s' WHERE location_id = %d", loc.locName, loc.locID);
        _database.executeUpdate(sql);
    }

    public void deleteLocFromDatabase(FSLocation loc) {
        String sql;
        //sql = [NSString stringWithFormat:@"DELETE FROM tbl_location WHERE location_id = '%@'", loc.locID];
        sql = String.format("UPDATE tbl_location SET deleted = 1 WHERE location_id = %d", loc.locID);
        _database.executeUpdate(sql);
    }

    public FSLocation getDefaultLocationOfJob(long jobID) {
        FSLocation loc = new FSLocation();
        String sql = String.format("SELECT * FROM tbl_location WHERE deleted = 0 and location_jobid  = %d and location_name = '%s'", jobID, FMD_DEFAULT_LOCATIONNAME);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;

                loc.locID       = results.getLong(i++); //[results intForColumn:@"location_id"];
                loc.locJobID    = results.getLong(i++); //[results intForColumn:@"location_jobid"];
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                return loc;
            } while (results.moveToNext());
        }
        return null;
    }

    public ArrayList<FSLocation> getAllDistinctLocations() {
        ArrayList<FSLocation> arrLocList = new ArrayList<FSLocation>();
        String sql = String.format("SELECT DISTINCT(location_name) AS location_name FROM tbl_location WHERE deleted = 0 and location_name != '%s'", DataManager.FMD_DEFAULT_LOCATIONNAME);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocation loc  = new FSLocation();
                loc.locID       = 0;
                loc.locJobID    = 0;
                loc.locName     = results.getString(i++); //[results stringForColumn:@"location_name"];
                //[arrLocList addObject:loc];
                arrLocList.add(loc);
            } while (results.moveToNext());
        }
        return arrLocList;
    }

// product
    public ArrayList<FSProduct> getAllProducts() {
        ArrayList<FSProduct> arrProductList = new ArrayList<FSProduct>();
        Cursor results = _database.executeCommand("SELECT * FROM tbl_product WHERE deleted = 0");
        if (results.moveToFirst())
        {
            do  {

                int i = 0;
                FSProduct product  = new FSProduct();

                product.productID = results.getLong(i++); //[results intForColumn:@"product_id"];
                product.productName = results.getString(i++); //[results stringForColumn:@"product_name"];
                product.productType = results.getLong(i++); //[results intForColumn:@"product_type"];
                product.productDeleted = results.getLong(i++); //[results intForColumn:@"deleted"];

                arrProductList.add(product);
            } while (results.moveToNext());
        }
        return arrProductList;
    }

    public ArrayList<FSProduct> getProducts(String searchField) {
        ArrayList<FSProduct> arrProductList = new ArrayList<FSProduct>();
        if (searchField == null)
            searchField = "";
        Cursor results = _database.executeQuery("SELECT * FROM tbl_product WHERE deleted = 0 and product_name like %s%s%s", "'%", searchField, "%'");
        if (results.moveToFirst())
        {
            do  {

                int i = 0;
                FSProduct product  = new FSProduct();

                product.productID = results.getLong(i++); //[results intForColumn:@"product_id"];
                product.productName = results.getString(i++); //[results stringForColumn:@"product_name"];
                product.productType = results.getLong(i++); //[results intForColumn:@"product_type"];
                product.productDeleted = results.getLong(i++); //[results intForColumn:@"deleted"];

                arrProductList.add(product);
            } while (results.moveToNext());
        }
        return arrProductList;
    }

    public boolean isExistSameProduct(String productName, long productType) {
        String sql = "";
        sql = String.format("SELECT COUNT(*) AS samecount FROM tbl_product WHERE deleted = 0 and product_type = %d and product_name = '%s'", productType, productName);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int count       = results.getInt(0); //[results intForColumn:@"samecount"];
                if (count > 0)
                    return true;
                return false;
            } while (false);
        }
        return false;
    }

    public FSProduct getProductFromID(long procID) {
        FSProduct product = new FSProduct();
        Cursor results = _database.executeQuery("SELECT * FROM tbl_product WHERE deleted = 0 AND product_id = %d", procID);
        if (results.moveToFirst())
        {
            do {
                int i = 0;

                product.productID = results.getLong(i++); //[results intForColumn:@"product_id"];
                product.productName = results.getString(i++); //[results stringForColumn:@"product_name"];
                product.productType = results.getLong(i++); //[results intForColumn:@"product_type"];
                product.productDeleted = results.getLong(i++); //[results intForColumn:@"deleted"];
                return product;
            } while (false);
        }
        return null;
    }

    public long addProductToDatabase(FSProduct product) {
        ContentValues values = new ContentValues();
        values.put("product_name", product.productName);
        values.put("product_type", product.productType);
        values.put("deleted", 0);

        long ret = _database.insert("tbl_product", null, values);

        if (ret < 0)
            return 0;
        return ret;
    }

    public void updateProductToDatabase(FSProduct product) {
        String sql;
        sql = String.format("UPDATE tbl_product SET product_name = '%s', product_type = %d WHERE product_id = %d", product.productName, product.productType, product.productID);
        _database.executeUpdate(sql);
    }

    public void deleteProductFromDatabase(FSProduct product) {
        String sql;
        //sql = [NSString stringWithFormat:@"DELETE FROM tbl_product WHERE product_id = '%@'", product.productID];
        sql = String.format("UPDATE tbl_product SET deleted = 1 WHERE product_id = %d", product.productID);
        _database.executeUpdate(sql);
    }

    public FSProduct getProductWithLocProduct(FSLocProduct locProduct) {
        FSProduct product = new FSProduct();
        Cursor results = _database.executeQuery("SELECT * FROM tbl_product WHERE deleted = 0 AND product_name = '%s' and product_type = %d", locProduct.locProductName, locProduct.locProductType);
        if (results.moveToFirst())
        {
            do {
                int i = 0;

                product.productID = results.getLong(i++); //[results intForColumn:@"product_id"];
                product.productName = results.getString(i++); //[results stringForColumn:@"product_name"];
                product.productType = results.getLong(i++); //[results intForColumn:@"product_type"];
                product.productDeleted = results.getLong(i++); //[results intForColumn:@"deleted"];
                return product;
            } while (false);
        }
        return null;
    }

// products for specific location
    public ArrayList<FSLocProduct> getLocProducts(FSLocation loc, String searchField) {
        ArrayList<FSLocProduct> arrLocProductList = new ArrayList<FSLocProduct>();
        if (searchField == null)
            searchField = "";
        Cursor results = _database.executeQuery("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_locid = %d AND locproduct_productname like %s%s%s and locproduct_productname != '%s'", loc.locID, "'%", searchField, "%'", FMD_DEFAULT_PRODUCTNAME);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocProduct locProduct  = new FSLocProduct();

                locProduct.locProductID = results.getLong(i++); //[results intForColumn:@"locproduct_id"];
                locProduct.locProductLocID = results.getLong(i++); //[results intForColumn:@"locproduct_locid"];
                locProduct.locProductName = results.getString(i++); //[results stringForColumn:@"locproduct_productname"];
                locProduct.locProductType = results.getLong(i++); //[results intForColumn:@"locproduct_producttype"];
                locProduct.locProductCoverage = results.getDouble(i++); //[results doubleForColumn:@"locproduct_coverage"];

                arrLocProductList.add(locProduct);

            } while (results.moveToNext());
        }
        return arrLocProductList;
    }

    public ArrayList<FSLocProduct> getLocProducts(FSLocation loc, String searchField, boolean isContainDefault) {
        ArrayList<FSLocProduct> arrLocProductList = new ArrayList<FSLocProduct>();
        if (searchField == null)
            searchField = "";

        String sql = "";
        if (isContainDefault)
            sql = String.format("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_locid = %d AND locproduct_productname like %s%s%s", loc.locID, "'%", searchField, "%'");
        else
        sql = String.format("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_locid = %d AND locproduct_productname like %s%s%s and locproduct_productname != '%@'", loc.locID, "'%", searchField, "%'", FMD_DEFAULT_PRODUCTNAME);

        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocProduct locProduct  = new FSLocProduct();

                locProduct.locProductID = results.getLong(i++); //[results intForColumn:@"locproduct_id"];
                locProduct.locProductLocID = results.getLong(i++); //[results intForColumn:@"locproduct_locid"];
                locProduct.locProductName = results.getString(i++); //[results stringForColumn:@"locproduct_productname"];
                locProduct.locProductType = results.getLong(i++); //[results intForColumn:@"locproduct_producttype"];
                locProduct.locProductCoverage = results.getDouble(i++); //[results doubleForColumn:@"locproduct_coverage"];

                arrLocProductList.add(locProduct);

            } while (results.moveToNext());
        }
        return arrLocProductList;
    }

    public boolean isExistSameLocProduct(long locID, String locProductName, long locProductType) {
        String sql = "";
        sql = String.format("SELECT COUNT(*) AS samecount FROM tbl_locproduct WHERE deleted = 0 and locproduct_locid = %d and locproduct_producttype = %d and locproduct_productname = '%s'", locID, locProductType, locProductName);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                int count       = results.getInt(0); //[results intForColumn:@"samecount"];
                if (count > 0)
                    return true;
                return false;
            } while (false);
        }
        return false;
    }

    public FSLocProduct getLocProductWithID(long locProductID) {
        Cursor results = _database.executeQuery("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_id = %d", locProductID);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocProduct locProduct  = new FSLocProduct();

                locProduct.locProductID = results.getLong(i++); //[results intForColumn:@"locproduct_id"];
                locProduct.locProductLocID = results.getLong(i++); //[results intForColumn:@"locproduct_locid"];
                locProduct.locProductName = results.getString(i++); //[results stringForColumn:@"locproduct_productname"];
                locProduct.locProductType = results.getLong(i++); //[results intForColumn:@"locproduct_producttype"];
                locProduct.locProductCoverage = results.getDouble(i++); //[results doubleForColumn:@"locproduct_coverage"];

                return locProduct;

            } while (false);
        }
        return null;
    }

    public FSLocProduct getDefaultLocProductOfLocation(FSLocation loc) {
        Cursor results = _database.executeQuery("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_locid = %d AND locproduct_productname = '%s'", loc.locID, FMD_DEFAULT_PRODUCTNAME);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocProduct locProduct  = new FSLocProduct();

                locProduct.locProductID = results.getLong(i++); //[results intForColumn:@"locproduct_id"];
                locProduct.locProductLocID = results.getLong(i++); //[results intForColumn:@"locproduct_locid"];
                locProduct.locProductName = results.getString(i++); //[results stringForColumn:@"locproduct_productname"];
                locProduct.locProductType = results.getLong(i++); //[results intForColumn:@"locproduct_producttype"];
                locProduct.locProductCoverage = results.getDouble(i++); //[results doubleForColumn:@"locproduct_coverage"];

                return locProduct;

            } while (false);
        }
        return null;
    }

    public FSLocProduct getLocProductWithProduct(FSProduct product, long locID) {
        Cursor results = _database.executeQuery("SELECT * FROM tbl_locproduct WHERE deleted = 0 AND locproduct_locid = %d AND locproduct_productname = '%s' AND locproduct_producttype = %d", locID, product.productName, product.productType);
        if (results.moveToFirst())
        {
            do {
                int i = 0;
                FSLocProduct locProduct  = new FSLocProduct();

                locProduct.locProductID = results.getLong(i++); //[results intForColumn:@"locproduct_id"];
                locProduct.locProductLocID = results.getLong(i++); //[results intForColumn:@"locproduct_locid"];
                locProduct.locProductName = results.getString(i++); //[results stringForColumn:@"locproduct_productname"];
                locProduct.locProductType = results.getLong(i++); //[results intForColumn:@"locproduct_producttype"];
                locProduct.locProductCoverage = results.getDouble(i++); //[results doubleForColumn:@"locproduct_coverage"];

                return locProduct;

            } while (false);
        }
        return null;
    }

    public long addLocProductToDatabaseWithProduct(FSProduct product, long locID, double coverage) {
        ContentValues values = new ContentValues();
        values.put("locproduct_locid", locID);
        values.put("locproduct_productname", product.productName);
        values.put("locproduct_producttype", product.productType);
        values.put("locproduct_coverage", coverage);
        values.put("deleted", 0);
        long ret = _database.insert("tbl_locproduct", null, values);
        if (ret < 0)
            return 0;
        return ret;
        /*String sql;
        sql = String.format("insert into tbl_locproduct (locproduct_locid, locproduct_productname, locproduct_producttype, locproduct_coverage, deleted) values (%d, '%s', %d, %f, 0)", locID, product.productName, product.productType, coverage);
        boolean bSuccess = _database.executeUpdate(sql);
        if (bSuccess == false)
            return 0;
        return 1;*/
    }

    public long addLocProductToDatabase(FSLocProduct locProduct) {
        ContentValues values = new ContentValues();
        values.put("locproduct_locid", locProduct.locProductLocID);
        values.put("locproduct_productname", locProduct.locProductName);
        values.put("locproduct_producttype", locProduct.locProductType);
        values.put("locproduct_coverage", locProduct.locProductCoverage);
        values.put("deleted", 0);
        long ret = _database.insert("tbl_locproduct", null, values);
        if (ret < 0)
            return 0;
        return ret;
        /*String sql;
        sql = String.format("insert into tbl_locproduct (locproduct_locid, locproduct_productname, locproduct_producttype, locproduct_coverage, deleted) values (%d, '%s', %d, %f, 0)", locProduct.locProductLocID, locProduct.locProductName, locProduct.locProductType, locProduct.locProductCoverage);
        boolean bSuccess = _database.executeUpdate(sql);
        if (bSuccess == false)
            return 0;
        return 1;*/
    }

    public boolean updateLocProductToDatabase(FSLocProduct locProduct) {
        String sql;
        sql = String.format("UPDATE tbl_locproduct SET locproduct_locid = %d, locproduct_productname = '%s', locproduct_producttype = %d, locproduct_coverage = %f WHERE locproduct_id = %d", locProduct.locProductLocID, locProduct.locProductName, locProduct.locProductType, locProduct.locProductCoverage, locProduct.locProductID);
        return _database.executeUpdate(sql);
    }

    public boolean deleteLocProductFromDatabase(FSLocProduct locProduct) {
        String sql;
        sql = String.format("UPDATE tbl_locproduct SET deleted = 1 WHERE locproduct_id = %d", locProduct.locProductID);
        return _database.executeUpdate(sql);
    }

// readings
    public ArrayList<FSReading> getCurReadings(long locProductID) {
        /*
     read_locproductid
     read_date
     read_uuid
     read_rh
     read_convrh
     read_temp
     read_convtemp
     read_battery
     read_depth
     read_gravity
     read_material
     read_mc
     deleted
*/
        ArrayList<FSReading> arrReadingsList = new ArrayList<FSReading>();
        Date curDate = new Date();
        return this.getReadings(locProductID, curDate);
    }

    public ArrayList<Date> getAllReadingDates(long locProductID) {
        ArrayList<Date> arrDates = new ArrayList<Date>();
        String sql = String.format("SELECT DISTINCT(SUBSTR(read_date, 1, 10)) as read_date FROM tbl_reading WHERE read_locproductid = %d ORDER BY read_date ASC", locProductID);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                String strDateOnly = results.getString(0); //[[results stringForColumn:@"read_date"] substringToIndex:10];
                Date date = CommonMethods.str2date(strDateOnly, CommonDefs.DATE_FORMAT);
                arrDates.add(date);
            } while (results.moveToNext());
        }
        return arrDates;
    }

    public ArrayList<FSReading> getReadings(long locProductID, Date date) {
        ArrayList<FSReading> arrReadingsList = new ArrayList<FSReading>();
        String strDate = CommonMethods.date2str(date, CommonDefs.DATE_FORMAT);
        String sql = String.format("SELECT * FROM tbl_reading WHERE deleted = 0 AND  read_locproductid = %d AND SUBSTR(read_date, 1, 10) = '%s'", locProductID, strDate);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                FSReading reading = new FSReading();
                reading.readID = results.getLong(results.getColumnIndex("read_id"));
                reading.readLocProductID = results.getLong(results.getColumnIndex("read_locproductid"));
                reading.readTimestamp = CommonMethods.str2date(results.getString(results.getColumnIndex("read_date")), CommonDefs.DATETIME_FORMAT);
                reading.readUuid = results.getString(results.getColumnIndex("read_uuid"));
                reading.readRH = results.getLong(results.getColumnIndex("read_rh"));
                reading.readConvRH = results.getDouble(results.getColumnIndex("read_convrh"));
                reading.readTemp = results.getLong(results.getColumnIndex("read_temp"));
                reading.readConvTemp = results.getDouble(results.getColumnIndex("read_convtemp"));
                reading.readBattery = results.getLong(results.getColumnIndex("read_battery"));
                reading.readDepth = results.getLong(results.getColumnIndex("read_depth"));
                reading.readGravity = results.getLong(results.getColumnIndex("read_gravity"));
                reading.readMaterial = results.getLong(results.getColumnIndex("read_material"));
                reading.readMC = results.getLong(results.getColumnIndex("read_mc"));
                arrReadingsList.add(reading);
            } while (results.moveToNext());
        }
        return arrReadingsList;
    }

    public int getReadingsCount(long locProductID, Date date) {
        String strDate = CommonMethods.date2str(date, CommonDefs.DATE_FORMAT);
        String sql = String.format("SELECT count(*) AS allcount FROM tbl_reading WHERE deleted = 0 AND read_locproductid = %d AND SUBSTR(read_date, 1, 10) = '%s'", locProductID, strDate);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                return results.getInt(results.getColumnIndex("allcount"));
            } while (false);
        }
        return 0;
    }

    public int getReadingsCount(long locProductID) {
        String sql = String.format("SELECT count(*) AS allcount FROM tbl_reading WHERE deleted = 0 AND read_locproductid = %d", locProductID);
        Cursor results = _database.executeCommand(sql);
        if (results.moveToFirst())
        {
            do {
                return results.getInt(results.getColumnIndex("allcount"));
            } while (false);
        }
        return 0;
    }

    public long addReadingToDatabase(FSReading reading) {
        /*
     read_locproductid
     read_date
     read_uuid
     read_rh
     read_convrh
     read_temp
     read_convtemp
     read_battery
     read_depth
     read_gravity
     read_material
     read_mc
     deleted
     */
        ContentValues values = new ContentValues();
        values.put("read_locproductid", reading.readLocProductID);
        values.put("read_date", CommonMethods.date2str(reading.readTimestamp, CommonDefs.DATETIME_FORMAT));
        values.put("read_uuid", reading.readUuid);
        values.put("read_rh", reading.readRH);
        values.put("read_convrh", reading.readConvRH);
        values.put("read_temp", reading.readTemp);
        values.put("read_convtemp", reading.readConvTemp);
        values.put("read_battery", reading.readBattery);
        values.put("read_depth", reading.readDepth);
        values.put("read_gravity", reading.readGravity);
        values.put("read_material", reading.readMaterial);
        values.put("read_mc", reading.readMC);
        values.put("deleted", 0);

        long ret = _database.insert("tbl_reading", null, values);
        if (ret < 0)
            return 0;
        return ret;
    }

    public boolean updateReadingToDatabase(FSReading reading) {
        String sql;
        sql = String.format("UPDATE tbl_reading set read_locproductid = %d, read_date = '%s', read_uuid = '%s', read_rh = %d, read_convrh = %f, read_temp = %d, read_convtemp = %f, read_battery = %d, read_depth = %d, read_gravity = %d, read_material = %d, read_mc = %d",
                reading.readLocProductID,
                CommonMethods.date2str(reading.readTimestamp, CommonDefs.DATETIME_FORMAT),
                reading.readUuid,
                reading.readRH,
                reading.readConvRH,
                reading.readTemp,
                reading.readConvTemp,
                reading.readBattery,
                reading.readDepth,
                reading.readGravity,
                reading.readMaterial,
                reading.readMC);
        return _database.executeUpdate(sql);
    }

    public boolean deleteReadingFromDatabase(FSReading reading) {
        String sql;
        sql = String.format("UPDATE tbl_reading SET deleted = 1 WHERE read_id = %d", reading.readID);
        return _database.executeUpdate(sql);
    }
}
