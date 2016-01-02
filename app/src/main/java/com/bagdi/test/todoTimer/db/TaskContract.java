package com.bagdi.test.todoTimer.db;

/**
 * Created by bagdi on 12/27/15.
 */
import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.example.TodoList.db.tasks";
    public static final int DB_VERSION = 9;
    public static final String TABLE = "mytasks";

    public class Columns {
        public static final String TASK = "task";
        public static final String _ID = BaseColumns._ID;
        public static final String DATETIME = "alarmTime";
    }
}