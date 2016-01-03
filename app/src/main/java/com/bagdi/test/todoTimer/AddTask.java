package com.bagdi.test.todoTimer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bagdi.test.todoTimer.db.TaskDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AddTask extends AppCompatActivity {
    Button timePickerButton;
    Button datePickerButton;
    Button shortDelay, longDelay;
    EditText taskDescription;
    TextView remainingTimeTextView;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int shortDuration, longDuration;
    private SimpleDateFormat dtformatter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        c.setTime(new Date());
        mYear = c.get(Calendar.YEAR);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DATE);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        longDuration = Integer.parseInt(sharedPreferences.getString("longDuration", "-1"));
        shortDuration = Integer.parseInt(sharedPreferences.getString("shortDuration", "-1"));
        Log.d("sharedPref", String.valueOf(longDuration));
        Log.d("sharedPref", String.valueOf(shortDuration));
        taskDescription = (EditText) findViewById(R.id.taskname);
        remainingTimeTextView = (TextView) findViewById(R.id.remainingTimeTextView);
        timePickerButton = (Button) findViewById(R.id.timePickerButton);
        datePickerButton = (Button) findViewById(R.id.datePickerButton);
        shortDelay = (Button) findViewById(R.id.plusShort);
        longDelay = (Button) findViewById(R.id.plusLong);
        shortDelay.setText("+" + String.valueOf(shortDuration) + " min");
        longDelay.setText("+" + String.valueOf(longDuration) + " min");

        timePickerButton.setText(mHour + ":" + mMinute);
        datePickerButton.setText((mMonth + 1) + "/" + mDay);
        taskDescription.setHint(R.string.taskDescriptionHint);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(taskDescription, InputMethodManager.SHOW_IMPLICIT);

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
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        //  Toast.makeText(AddTask.this, dayOfMonth + "-"
//                                + (monthOfYear + 1) + "-" + year, Toast.LENGTH_LONG).show();
                        button.setText((mMonth + 1) + "/" + mDay);
                        toggleButton("date");
                        updateRemainingTime();
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
                        //  Toast.makeText(AddTask.this, hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
                        mHour = hourOfDay;
                        mMinute = minute;
                        button.setText(mHour + ":" + mMinute);
                        toggleButton("time");
                        updateRemainingTime();
                    }
                }, mHour, mMinute, false);
        tpd.show();

    }

    public void setShort(View view) {
//TODO implement
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, shortDuration);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        toggleButton("plusShort");
        updateRemainingTime();
    }

    public void setLong(View view) {
        //TODO implement
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, longDuration);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        toggleButton("plusLong");
        updateRemainingTime();
    }

    public void addTask(View view) {

        String taskDesc = taskDescription.getText().toString();
        if (taskDesc.equals("") || taskDesc.equals(getResources().getString(R.string.taskDescriptionHint))) {
            Log.d("MainActivity", "Empty field return");
            Toast.makeText(this, "Put some description", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("MainActivity", "Add a new task");
        String debugString = "task name:" + taskDesc + "\nTime:" + mHour + ":" + mMinute + ":" + mMonth + ":" + mDay + ":" + mYear;
        Log.d("MainActivity", debugString);
        addToDB(taskDesc);

    }


    public void addToDB(String task) {

        Log.d("MainActivity", "inserting intoDB");
        Log.d("MainActivity", task);
        //2014-10-23 15:21 format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth, mDay, mHour, mMinute);
        Date date = new Date(cal.getTimeInMillis());
        TaskDatabase database = new TaskDatabase(this);
        database.open();
        int id = database.insert(task, sdf.format(date));
        database.close();
        setUpNotification(cal.getTimeInMillis(), id);
        Toast.makeText(AddTask.this, "Task added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void setUpNotification(long time, int REQUEST_CODE) {

        //logic for notification
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra("UNIQUE_ID", REQUEST_CODE);
        intent.putExtra("ORIGIN", "ADDTASK");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        //
    }

    public void toggleButton(String which) {
        switch (which) {
            case "time":
                switchOn(timePickerButton);
                switchOff(shortDelay);
                switchOff(longDelay);

                break;
            case "date":
                switchOn(datePickerButton);
                switchOff(shortDelay);
                switchOff(longDelay);
                break;
            case "plusShort":
                switchOff(timePickerButton);
                switchOff(datePickerButton);
                switchOff(longDelay);
                switchOn(shortDelay);
                break;
            case "plusLong":
                switchOff(timePickerButton);
                switchOff(datePickerButton);
                switchOn(longDelay);
                switchOff(shortDelay);
                break;
        }
    }

    private void switchOn(Button button) {
//        button.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        button.getBackground().setColorFilter(new LightingColorFilter(0x00FF5722, 0xFFAA0000));
    }

    private void switchOff(Button button) {
        button.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0FF0));
    }


    private void updateRemainingTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth, mDay, mHour, mMinute);
        Date date = new Date(cal.getTimeInMillis());

        long timeDiff = date.getTime() - (new Date().getTime());
        if (timeDiff > 0) {
            if (timeDiff > TimeUnit.DAYS.toMillis(1)) {
                int daysRemaining = (int) (timeDiff / TimeUnit.DAYS.toMillis(1));
                remainingTimeTextView.setText(String.valueOf(daysRemaining) + "days Remaining");
                return;
            }
            //   int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            remainingTimeTextView.setText(hours + " h " + minutes + "m");
        } else {
            remainingTimeTextView.setText("Time Up!!");
        }
    }

}
