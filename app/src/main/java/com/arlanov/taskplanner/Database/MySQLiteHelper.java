package com.arlanov.taskplanner.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arlanov.taskplanner.Database.TaskContract.TaskEntry;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Tasks.db";
    public static final int DB_VERSION = 1;

    public MySQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_TEXT + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_VALUE + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}
