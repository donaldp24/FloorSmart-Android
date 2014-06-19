package com.tim.FloorSmart.Database.FMDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.sql.SQLException;

/**
 * Created by donald on 5/8/14.
 */
public class FMDatabase {
    // Database fields
    private FMDBHelper dbHelper;
    private SQLiteDatabase db;


    private boolean logsErrors;
    private boolean crashOnErrors;
    private boolean inUse;
    private boolean inTransaction;
    private boolean traceExecution;
    private boolean checkedOut;
    private int busyRetryTimeout;
    private boolean shouldCacheStatements;
    //NSMutableDictionary *cachedStatements;
    //NSMutableSet *openResultSets;

    SQLiteCursor openCursor;

    public FMDatabase(Context ctx) {
        dbHelper = new FMDBHelper(ctx);
    }

    public boolean open() {
        try {
            db = dbHelper.getWritableDatabase();
            return true;
        } catch (Exception e) {
            CommonMethods.Log("error in open database : " + e.getMessage());
            return false;
        }
    }

    public boolean close() {
        try {
            dbHelper.close();
            return true;
        } catch (Exception e) {
           CommonMethods.Log("error in close database : " + e.getMessage());
            return false;
        }
    }

    public boolean goodConnection() {
        // have to re-implement
        return false;
    }

    public void clearCachedStatements() {
        //
    }

    public void closeCursor() {
        //
    }

    public String lastErrorMessage() {
        // have to re-implement
        return "";
    }

    public int lastErrorCode() {
        // have to re-implement.
        return 0;
    }

    public boolean hadError() {
        // have to re-implement.
        return false;
    }

    public Cursor executeQuery(String formatSql, Object ...arguments) {
        String sql = String.format(formatSql, arguments);
        return db.rawQuery(sql, null);
    }

    public Cursor executeCommand(String formattedSql)
    {
        return db.rawQuery(formattedSql, null);
    }

    public boolean executeUpdate(String formatSql, Object ...arguments) {
        String sql = String.format(formatSql, arguments);
        db.execSQL(sql);
        return true;
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return db.insert(table, nullColumnHack, values);
    }
}
