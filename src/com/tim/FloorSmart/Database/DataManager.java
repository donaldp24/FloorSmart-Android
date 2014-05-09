package com.tim.FloorSmart.Database;

import android.database.Cursor;
import com.tim.FloorSmart.Database.FMDB.FMDatabase;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by donald on 5/8/14.
 */
public class DataManager {

    // singleton
    private static DataManager _sharedInstance;

    // properties
    private FMDatabase _database;


    private DataManager() {
        //
    }

    public static DataManager sharedInstance() {
        if (_sharedInstance == null) {
            _sharedInstance = new DataManager();
        }
        return _sharedInstance;
    }

    // job
    public ArrayList<FSJob> getAllJobs() {
        ArrayList<FSJob> arrJobList = new ArrayList<FSJob>();
        Cursor results = _database.executeQuery("SELECT * FROM tbl_job WHERE deleted = 0");
        while (results != null && results.moveToNext()) {
            int i = 0;
            FSJob job  = new FSJob();
            job.jobID       = results.getLong(i++); //intForColumn:@"job_id"];
            job.jobName     = results.getString(i++); //[results stringForColumn:@"job_name"];
            job.jobArchived = results.getInt(i++); //[results intForColumn:@"job_archived"];

            arrJobList.add(job);
        }
        return arrJobList;
    }

    public ArrayList<FSJob> getJobs(long archiveFlag, String searchField) {
        //
    }

    public boolean isExistSameJob(String jobName) {
        //
    }

    public FSJob getJobFromID(long jobID) {
        //
    }

    public int addJobToDatabase(FSJob job) {
        //
    }

    public void updateJobToDatabase(FSJob job) {
        //
    }

    public void deleteJobFromDatabase(FSJob job) {
        //
    }

// location
    public ArrayList<FSLocation> getLocations(long jobID) {
        //
    }

    public ArrayList<FSLocation> getLocations(long jobID, boolean isContainDefault) {
        //
    }

    public boolean isExistSameLocation(long jobID, String locName) {
        //
    }

    public FSLocation getLocation(long jobID, String locName) {
        //
    }

    public FSLocation getLocationFromID(long locID) {
        //
    }

    public int addLocationToDatabase(FSLocation loc) {
        //
    }

    public void updateLocToDatabase(FSLocation loc) {
        //
    }

    public void deleteLocFromDatabase(FSLocation loc) {
        //
    }

    public FSLocation getDefaultLocationOfJob(long jobID) {
        //
    }

    public ArrayList<FSLocation> getAllDistinctLocations() {
        //
    }

// product
    public ArrayList<FSProduct> getAllProducts() {
        //
    }

    public ArrayList<FSProduct> getProducts(String searchField) {
        //
    }

    public boolean isExistSameProduct(String productName, long productType) {
        //
    }

    public FSProduct getProductFromID(long procID) {
        //
    }

    public int addProductToDatabase(FSProduct product) {
        //
    }

    public void updateProductToDatabase(FSProduct product) {
        //
    }

    public void deleteProductFromDatabase(FSProduct product) {
        //
    }

    public FSProduct getProductWithLocProduct(FSLocProduct locProduct) {
        //
    }

// products for specific location
    public ArrayList<FSLocProduct> getLocProducts(FSLocation loc, String searchField) {
        //
    }

    public ArrayList<FSLocProduct> getLocProducts(FSLocation loc, String searchField, boolean isContainDefault) {
        //
    }

    public boolean isExistSameLocProduct(long locID, String locProductName, long locProductType) {
        //
    }

    public FSLocProduct getLocProductWithID(long locProductID) {
        //
    }

    public FSLocProduct getDefaultLocProductOfLocation(FSLocation loc) {
        //
    }

    public FSLocProduct getLocProductWithProduct(FSProduct product, long locID) {
        //
    }

    public int addLocProductToDatabaseWithProduct(FSProduct product, long locID, double coverage) {
        //
    }

    public int addLocProductToDatabase(FSLocProduct locProduct) {
        //
    }

    public boolean updateLocProductToDatabase(FSLocProduct locProduct) {
        //
    }

    public boolean deleteLocProductFromDatabase(FSLocProduct locProduct) {
        //
    }

// readings
    public ArrayList<FSReading> getCurReadings(long locProductID) {
        //
    }

    public ArrayList<FSReading> getAllReadingDates(long locProductID) {
        //
    }

    public ArrayList<FSReading> getReadings(long locProductID, Date date) {
        //
    }

    public int getReadingsCount(long locProductID, Date date) {
        //
    }

    public int getReadingsCount(long locProductID) {
        //
    }

    public int addReadingToDatabase(FSReading reading) {
        //
    }

    public boolean updateReadingToDatabase(FSReading reading) {
        //
    }

    public boolean deleteReadingFromDatabase(FSReading reading) {
        //
    }
}
