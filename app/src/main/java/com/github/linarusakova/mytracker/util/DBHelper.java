package com.github.linarusakova.mytracker.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    SQLiteDatabase DB;
    private final String LOG_TAG = "DB HELPER = ";
    public static final String DB_NAME = "weightDB";
    private static final int DB_VERSION = 1;
    int oldVersion, newVersion;

    public static final String DB_TABLE_NAME = "trackerTable1";
    private static final String date_column = "date";
    private static final String weight_column = "currentWeight";
    private static final String medical_column = "currentMedical";
    private static final String day_details_column = "currentDayDetails";
    List<OneDayRecord> recordList;
    private static DBHelper dbHelper;

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) dbHelper = new DBHelper(context);
        return dbHelper;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        oldVersion = 1;
        newVersion = 1;
        onCreateOrUpgradeDB(db, oldVersion, newVersion);
    }

    public void insertDBRecord(SQLiteDatabase db, OneDayRecord oneDayRecord) {
        OneDayRecord savedRecord = getOneRecordByDate(db, oneDayRecord.getDate());
        String date = oneDayRecord.getDate();
        ContentValues putValues = new ContentValues();
        putValues.put(date_column, date);
        putValues.put(weight_column, oneDayRecord.getWeight());
        putValues.put(medical_column, oneDayRecord.getMedical());
        putValues.put(day_details_column, oneDayRecord.getDayDetails());

        if (savedRecord.getWeight() == null) {
            db.insert(DB_TABLE_NAME, null, putValues);
            Log.d(LOG_TAG, "--- insert Record to DB:" + oneDayRecord.toString());
        } else {
            ContentValues updateValues = new ContentValues();
            updateValues.put(weight_column, oneDayRecord.getWeight());
            updateValues.put(medical_column, oneDayRecord.getMedical());
            updateValues.put(day_details_column, oneDayRecord.getDayDetails());
            db.update(DB_TABLE_NAME, putValues, "date = ?", new String[]{date});
            Log.d(LOG_TAG, "--- update Record into DB:" + oneDayRecord.toString());
        }
    }

    public void update(SQLiteDatabase db, String tableName, String locationName, String latitude, String longitude) {
        ContentValues locationValues = new ContentValues();
        locationValues.put(date_column, locationName);
        locationValues.put(weight_column, latitude);
        locationValues.put(day_details_column, longitude);
        db.update(tableName, locationValues, "_ID= ?", new String[]{Integer.toString(1)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreateOrUpgradeDB(db, oldVersion, newVersion);
        Log.d(LOG_TAG, "--- onUpgrade database ---");
    }

    public void onCreateOrUpgradeDB(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
        } else {
        }
    }

    OneDayRecord dayRecord;

    public void prepareAllDataBases(SQLiteDatabase db) {
        DB = db;
        DB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + " (" + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + date_column + " text, " + weight_column + " text, " + medical_column + " text, " + day_details_column + " text);");
    }

    public boolean tableExists(SQLiteDatabase db, String table) {
        boolean result = false;
        String sql = "select count(*) xcount from sqlite_master where type='table' and name='" + table + "'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0) result = true;
        cursor.close();
        return result;
    }

    public OneDayRecord getOneRecordByDate(SQLiteDatabase db, String selectedDate) {
        dayRecord = new OneDayRecord();
        String dateInTable = "'" + selectedDate + "'";
        String sql = "select * from " + DB_TABLE_NAME + " where " + date_column + "= " + dateInTable;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            dayRecord = new OneDayRecord(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            cursor.close();
        } else {
            dayRecord = new OneDayRecord();
            dayRecord.setDate(dateInTable.replaceAll("'", ""));
            return dayRecord;
        }
        return dayRecord;
    }

    public List<OneDayRecord> getAllRecordsByDate(SQLiteDatabase db, String date) {

        dayRecord = new OneDayRecord();

        DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateCurrent = timeFormat.format(new Date());
//        String dateCurrentInTable = "'" + dateCurrent + "'";
        String dateCurrentInTable = dateCurrent;
        String dateInTable = date;
        String sql = "select * from " + DB_TABLE_NAME + " where " + date_column + " between '" + dateInTable + "' and '" + dateCurrentInTable + "' order by " + date_column;

        Cursor cursor = null;
        cursor = db.rawQuery(sql, null);
        recordList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                recordList.add(new OneDayRecord(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }

            cursor.close();
        }
        return recordList;
    }

    public List<OneDayRecord> getListRecordsBetweenTwoDate(SQLiteDatabase db, String firstDateOnThisWeek, String currentDate) {
        OneDayRecord oneDayRecord = new OneDayRecord();
        List<OneDayRecord> oneDayRecordList = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + DB_TABLE_NAME + " WHERE (" + date_column + ") BETWEEN ('" + firstDateOnThisWeek + "') AND ('" + currentDate + "')";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        oneDayRecordList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                oneDayRecordList.add(new OneDayRecord(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
            cursor.close();
        }
        return oneDayRecordList;
    }

    public List<BarEntry> getChartEntryListBetweenTwoDate(SQLiteDatabase db, String firstDateOnThisWeek, String currentDate) throws ParseException {
        DateManager dateManager = new DateManager();
        List<BarEntry> chartEntryArrayList = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + DB_TABLE_NAME + " WHERE (" + date_column + ") BETWEEN ('" + firstDateOnThisWeek + "') AND ('" + currentDate + "') ORDER BY " + date_column
                + " ASC;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        Date date;
        Float x = 1f;
        Float weight;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(1);
                try {
                    date = dateManager.getDateFromDateString(s);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date d = formatter.parse(s);
                    Calendar myCal = new GregorianCalendar();
                    myCal.setTime(date);
                    x = (float) myCal.get(Calendar.DAY_OF_WEEK);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                weight = Float.parseFloat(String.valueOf(cursor.getString(2)));
                chartEntryArrayList.add(new BarEntry(x, weight, date));
            }
            cursor.close();
        }
        return chartEntryArrayList;
    }

    public List<ChartRecordFromDB> getChartListBetweenTwoDate(SQLiteDatabase db, String firstDateOnThisWeek, String currentDate) {
        List<ChartRecordFromDB> chartEntryArrayList = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + DB_TABLE_NAME + " WHERE (" + date_column + ") BETWEEN ('" + firstDateOnThisWeek + "') AND ('" + currentDate + "') ORDER BY " + date_column
                + " ASC;";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                chartEntryArrayList.add(new ChartRecordFromDB(cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();
        }
        return chartEntryArrayList;
    }
}
