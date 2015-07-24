package com.ssp.testapp.wcmap.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabaseHelper {

    private static final String TAG = SQLDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wcmap.db";

    private static final String TABLE_MARKERS = "markers_table";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";

    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;

    public SQLDatabaseHelper(Context context) {
        openHelper = new DatabaseOpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public boolean checkIfExist(){
        String buildSQL = "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + TABLE_MARKERS + "'";
        Cursor cursor = database.rawQuery(buildSQL, null);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void insertMarker(String title, String desc, double latitude, double longitude) {
//        if(!database.isOpen()){
//            database = openHelper.getWritableDatabase();
//        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESC, desc);
        contentValues.put(COLUMN_LAT, latitude);
        contentValues.put(COLUMN_LONG, longitude);
        database.insert(TABLE_MARKERS, null, contentValues);
    }

    public void deleteMarkers()
    {
        database.delete(TABLE_MARKERS, null, null);
//        database.close();
    }

    public Cursor getMarkers () {
        String buildSQL = "SELECT * FROM " + TABLE_MARKERS;
        Log.d(TAG, "getMarkers SQL: " + buildSQL);
        return database.rawQuery(buildSQL, null);
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String buildMarkersSQL = "CREATE TABLE " + TABLE_MARKERS + "( " + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " + COLUMN_DESC + " TEXT, " + COLUMN_LAT + " REAL, " +
                    COLUMN_LONG + " REAL )";

            Log.d(TAG, "onCreate SQL: " + buildMarkersSQL);

            sqLiteDatabase.execSQL(buildMarkersSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

            String buildMarkersSQL = "DROP TABLE IF EXISTS " + TABLE_MARKERS;

            Log.d(TAG, "onUpgrade SQL: " + buildMarkersSQL);

            sqLiteDatabase.execSQL(buildMarkersSQL);

            onCreate(sqLiteDatabase);
        }
    }
}
