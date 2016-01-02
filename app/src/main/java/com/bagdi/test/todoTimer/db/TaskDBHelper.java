package com.bagdi.test.todoTimer.db;

/**
 * Created by bagdi on 12/27/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDBHelper extends SQLiteOpenHelper {
    private static TaskDBHelper sInstance;

    public static synchronized TaskDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TaskDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    public TaskDBHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        Log.d("MainDB","oncreate for SQLopenHelperCalled");
        String sqlQuery =
                String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s DATETIME );", TaskContract.TABLE,
                        TaskContract.Columns.TASK, TaskContract.Columns.DATETIME);

        Log.d("TaskDBHelper", "Query to form table: " + sqlQuery);
        sqlDB.execSQL(sqlQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        Log.d("MainDB","onUpgrade for SQLopenHelperCalled");
        sqlDB.execSQL("DROP TABLE IF EXISTS " + TaskContract.TABLE);
        onCreate(sqlDB);
    }

}
