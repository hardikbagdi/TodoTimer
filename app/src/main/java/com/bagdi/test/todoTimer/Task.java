package com.bagdi.test.todoTimer;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by bagdi on 12/31/15.
 */
//model class
public class Task {
    private int id;
    private String task;
    private String dateTimeString;
    private long milliseconds;
    private Date dateTimeObj;
    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.dateTimeObj = sdf.parse(dateTimeString);
        this.milliseconds = dateTimeObj.getTime();
        this.dateTimeString = dateTimeString;
    }

    public Date getDateTimeObj() {
        return dateTimeObj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    @Override
    public String toString() {
        return "ID:"+id+"\nTask:"+task+"\nTime"+dateTimeString;
    }
}
