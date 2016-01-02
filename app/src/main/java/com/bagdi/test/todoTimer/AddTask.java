package com.bagdi.test.todoTimer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bagdi.test.todoTimer.db.TaskDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private SimpleDateFormat dtformatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        c.setTime(new Date());
        mYear = c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DATE);
        mHour=c.get(Calendar.HOUR);
        mMinute=c.get(Calendar.MINUTE);
      //  Toast.makeText(this,c.getTime().toString(),Toast.LENGTH_LONG).show();
    }

    public void setDate(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        final Button button = (Button) findViewById(view.getId());

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        mYear=year;
                        mMonth=monthOfYear;
                        mDay=dayOfMonth;
                        Toast.makeText(AddTask.this, dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year, Toast.LENGTH_LONG).show();
                        button.setText(mMonth + "/" + mDay + "/" + mYear);
                    }
                }, mYear, mMonth, mDay);

        dpd.show();
    }

    public void setTime(View view) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final Button button = (Button) findViewById(view.getId());
        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view2, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        Toast.makeText(AddTask.this, hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
                        mHour=hourOfDay;
                        mMinute=minute;
                        button.setText(mHour + ":" + mMinute);
                    }
                }, mHour, mMinute, false);
        tpd.show();

    }

    public void addTask(View view) {
        TextView taskname = (TextView) findViewById(R.id.taskname);
        String taskDescription = taskname.getText().toString();
        if (taskDescription.equals("")) {
            Log.d("MainActivity", "Empty field return");
            Toast.makeText(this, "Put some description", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("MainActivity", "Add a new task");
        String debugString = "task name:" + taskDescription + "\nTime:" + mHour + ":" + mMinute + ":" + mMonth + ":" + mDay + ":" + mYear;
        Log.d("MainActivity", debugString);
        addToDB(taskDescription);

    }


    public void addToDB(String task) {

        Log.d("MainActivity", "inserting intoDB");
        Log.d("MainActivity", task);
        //2014-10-23 15:21 format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal  = Calendar.getInstance();
        cal.set(mYear, mMonth, mDay, mHour, mMinute);
        Date date = new Date(cal.getTimeInMillis());
        TaskDatabase database = new TaskDatabase(this);
        database.open();
        int id = database.insert(task, sdf.format(date));
        database.close();
        setUpNotification(cal.getTimeInMillis(), id);
        Toast.makeText(AddTask.this, "Task added", Toast.LENGTH_LONG).show();
        finish();
    }

    public void setUpNotification(long time, int REQUEST_CODE){

        //logic for notification
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra("UNIQUE_ID",REQUEST_CODE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        //
    }

}
