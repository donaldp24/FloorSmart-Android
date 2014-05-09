package com.tim.FloorSmart.Database.FMDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by donald on 5/8/14.
 */
public class FMDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "floorsmart.db";
    public static final int DATABASE_VERSION = 1;

    public FMDBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql_job = "CREATE TABLE tbl_job(job_id INTEGER PRIMARY KEY AUTOINCREMENT, job_name TEXT, job_archived INTEGER, deleted INTEGER);";
            String sql_location = "CREATE TABLE tbl_location(location_id INTEGER PRIMARY KEY AUTOINCREMENT, location_jobid INTEGER, location_name TEXT, deleted INTEGER);";
            String sql_product = "CREATE TABLE tbl_product(product_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, product_type INTEGER, deleted INTEGER);";
            String sql_locproduct = "CREATE TABLE tbl_locproduct(locproduct_id INTEGER PRIMARY KEY AUTOINCREMENT, locproduct_locid INTEGER, locproduct_productname TEXT, locproduct_producttype INTEGER, locproduct_coverage DOUBLE, deleted INTEGER);";
            String sql_reading = "CREATE TABLE tbl_reading(read_id INTEGER PRIMARY KEY AUTOINCREMENT, read_locproductid INTEGER, read_date TEXT, read_uuid TEXT, read_rh INTEGER, read_convrh DOUBLE, read_temp INTEGER, read_convtemp DOUBLE, read_battery INTEGER, read_depth INTEGER, read_gravity INTEGER, read_material INTEGER, read_mc INTEGER, deleted INTEGER);";

            db.execSQL(sql_job);
            db.execSQL(sql_location);
            db.execSQL(sql_product);
            db.execSQL(sql_locproduct);
            db.execSQL(sql_reading);

        } catch (Exception e) {
            Log.v("FMDBHelper", "exception in db creating : " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if (oldVersion == 1 && newVersion == 1.1) {
        //    db.execSQL("DROP TABLE IF EXISTS tbl_job");
        //    db.execSQL("DROP TABLE IF EXISTS tbl_location");
        //    db.execSQL("DROP TABLE IF EXISTS tbl_product");
        //    db.execSQL("DROP TABLE IF EXISTS tbl_locproduct");
        //    db.execSQL("DROP TABLE IF EXISTS tbl_reading");
        //    onCreate(db);
        //}
    }
}
