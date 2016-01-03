package com.bagdi.test.todoTimer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bagdi.test.todoTimer.Task;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bagdi on 12/31/15.
 */
public class TaskDatabase {
    private String TAG = "DB";
    private SQLiteDatabase database;
    private TaskDBHelper taskDBHelper;

    public TaskDatabase(Context context){
        Log.d(TAG,"New TaskDB");
        taskDBHelper = TaskDBHelper.getInstance(context);

    }
    public void open() throws SQLException {
        Log.d(TAG,"open");
        database = taskDBHelper.getWritableDatabase();
    }

    public void close() {
        Log.d(TAG,"close");
        taskDBHelper.close();
    }

    public Task searchByID(int id) throws ParseException {
        Log.d(TAG,"searching "+id);
        Cursor cursor = database.query(TaskContract.TABLE, null, TaskContract.Columns._ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor==null){
            return null;
        }
        else {
            Task task = new Task();
            cursor.moveToFirst();
            task.setId(cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID)));
            task.setTask(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK)));
            task.setDateTimeString(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.DATETIME)));
            return task;
        }

    }
    public Task searchByString(String taskdDescription) throws ParseException {
        Log.d(TAG,"Searching "+taskdDescription);
        Cursor cursor = database.query(TaskContract.TABLE, null, TaskContract.Columns.TASK + "=?",
                new String[] { taskdDescription }, null, null, null, null);
        if(cursor==null){
            return null;
        }
        else {
            Task task = new Task();
            cursor.moveToFirst();
            task.setId(cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID)));
            task.setTask(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK)));
            task.setDateTimeString(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.DATETIME)));
            return task;
        }
    }

    //return inserted ID
    public int insert(String task, String dateTimeString){
        Log.d(TAG,"insertedd"+task);
        //inserting
        String insertQuery = "INSERT INTO " + TaskContract.TABLE + "(" + TaskContract.Columns.TASK + "," + TaskContract.Columns.DATETIME + ") VALUES('" + task + "', datetime('" + dateTimeString + " '))";
        Log.d("MainInsertQuery:", insertQuery);
        database.execSQL(insertQuery);
        //getID
        Cursor cursor = database.query(TaskContract.TABLE, null, TaskContract.Columns.TASK + "=?",
                new String[]{task}, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID));

    }

    public  Task[] getAllTaskObjects() throws ParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        Task task;
        Cursor cursor = getAll();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID)));
            task.setTask(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK)));
            task.setDateTimeString(cursor.getString(cursor.getColumnIndex(TaskContract.Columns.DATETIME)));
            tasks.add(task);
            cursor.moveToNext();

        }
        Task[] returnArray= new Task[tasks.size()];
        tasks.toArray(returnArray);
        return returnArray;
    }
    public Cursor getAll(){
        Log.d(TAG, "getall");
        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = database.query(TaskContract.TABLE, null,null, null, null, null,   "date("+TaskContract.Columns.DATETIME+")");

        Log.d(TAG, "count:" + cursor.getCount());
        return cursor;
    }

    public void delete(String task){
        Log.d(TAG,"deleted"+task);
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);
        database.execSQL(sql);
    }

    public void deleteById(int id) {
        Log.d(TAG, "deleted by ID" + id);
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns._ID,
                String.valueOf(id));
        database.execSQL(sql);
    }


}
