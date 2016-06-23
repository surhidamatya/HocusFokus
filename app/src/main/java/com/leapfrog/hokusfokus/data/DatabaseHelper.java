package com.leapfrog.hokusfokus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * creates the database and database table.
 *
 * @author rajesh.
 *         Created by root on 3/27/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    private static final int DB_VERSION = 2;
    public static final String DB_NAME = "Focus";

    public static final String TABLE_FOCUS = "table_focus";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_STATUS = "taskStatus";
    public static final String COLUMN_TIME_STAMP = "timeStamp";
    public static final String COLUMN_TASK_ADDED_MONTH = "month";
    public static final String COLUMN_TASK_MONTH_AND_DATE = "monthDate";
    public static final String COLUMN_TASK_ORDER = "priority";


    private static final String CREATE_TABLE_FOCUS = "CREATE TABLE IF NOT EXISTS " + TABLE_FOCUS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TASK + " VARCHAR(255)," +
            COLUMN_STATUS + " VARCHAR(255)," +
            COLUMN_TASK_ADDED_MONTH + " VARCHAR(255)," +
            COLUMN_TASK_MONTH_AND_DATE + " VARCHAR(255)," +
            COLUMN_TASK_ORDER + " INTEGER DEFAULT 0," +
            COLUMN_TIME_STAMP + " VARCHAR(255))";

    private static final String DROP_TABLE_FOCUS = "DROP TABLE IF EXISTS " + TABLE_FOCUS;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FOCUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_FOCUS);
        onCreate(db);
    }
}

