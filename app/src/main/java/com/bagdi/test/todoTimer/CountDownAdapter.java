package com.bagdi.test.todoTimer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bagdi.test.todoTimer.db.TaskDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by bagdi on 12/31/15.
 */
class CountdownAdapter extends ArrayAdapter<Task> {

    private LayoutInflater lf;
    private Activity activity;
    private List<ViewHolder> taskHolder;
    private Handler mHandler = new Handler();
    private  List<Task> data;
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (taskHolder) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : taskHolder) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public CountdownAdapter(Context context, List<Task> objects,Activity activity) {
        super(context, 0, objects);
        lf = LayoutInflater.from(context);
        this.activity= activity;
        taskHolder = new ArrayList<>();
        startUpdateTimer();
        setNotifyOnChange(true);
        data=objects;
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = lf.inflate(R.layout.listview_item, parent, false);
            holder.taskName = (TextView) convertView.findViewById(R.id.taskTextView);
            holder.remainingTime = (TextView) convertView.findViewById(R.id.remainingTime);
            convertView.setTag(holder);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Main", "item click");
                    // //logic to open up a detailed activity view
                    Intent intent = new Intent(getContext(), TaskDetails.class);
                    TextView t = (TextView) v.findViewById(R.id.taskTextView);
                    Log.d("Main_itemClick", t.getText().toString());
                    intent.putExtra("ORIGIN", "ITEMCLICK");
                    intent.putExtra("TASKDESC", t.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);

                }
            });
            ImageButton button = (ImageButton) convertView.findViewById(R.id.doneButton);
            button.setTag(position);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout parent = (RelativeLayout) v.getParent();
                    final String taskDescription = ((TextView) parent.findViewById(R.id.taskTextView)).getText().toString();
                    //  Toast.makeText(getContext(), "Button clicked:" + taskDescription, Toast.LENGTH_LONG).show();
                    TaskDatabase taskDatabase = new TaskDatabase(getContext());
                    taskDatabase.open();
                    Task task = null;
                    try {
                        task = taskDatabase.searchByString(taskDescription);
                        taskDatabase.delete(taskDescription);
                    } catch (ParseException p) {
                        Log.d("Main", "Parse Exception");
                    }
                    Log.d("Main_indelete", String.valueOf(taskHolder.size()));
                    taskDatabase.close();
                    deleteNotification(task.getId());
                    ViewHolder taskToDelete = taskHolder.get(position);
                  //  taskHolder.remove(taskToDelete);

                    data.remove((int) v.getTag());
//                remove(data.get((Integer)v.getTag()));
                    Log.d("Main_indeletePOST", String.valueOf(taskHolder.size()));

                    notifyDataSetChanged();
                    Snackbar.make(activity.findViewById(R.id.cordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Main_SnackbarClick", taskDescription);
                                    Intent intent = new Intent(getContext(), TaskDetails.class);
                                    TextView t = (TextView) v.findViewById(R.id.taskTextView);
                                    intent.putExtra("ORIGIN", "SNACKBAR");
                                    intent.putExtra("TASKDESC", taskDescription);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getContext().startActivity(intent);
                                }
                            })
                            .show();

                }
            });
            holder.button=button;
            synchronized (taskHolder) {
                taskHolder.add(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(getItem(position));

        return convertView;
    }
    private void deleteNotification(int id) {
        Intent intent = new Intent(getContext(), NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        Log.d("Main","Pending Intent Deleted");
    }
}

class ViewHolder {
    TextView taskName;
    TextView remainingTime;
    Task task;
    ImageButton button;

    public void setData(Task task) {
        this.task = task;
        taskName.setText(task.getTask());
        updateTimeRemaining(System.currentTimeMillis());
    }

    public void updateTimeRemaining(long currentTime) {
        long timeDiff = task.getMilliseconds() - currentTime;
        if (timeDiff > 0) {
            if(timeDiff > TimeUnit.DAYS.toMillis(1)){
                int daysRemaining =(int) (timeDiff/TimeUnit.DAYS.toMillis(1));
                remainingTime.setText(String.valueOf(daysRemaining)+"days Remaining");
                return;
            }
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            remainingTime.setText(hours + " h " + minutes + "m");
        } else {
            remainingTime.setText("0h 0m");
        }
    }
}
