package com.bagdi.test.todoTimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bagdi.test.todoTimer.db.TaskContract;
import com.bagdi.test.todoTimer.db.TaskDBHelper;
import com.bagdi.test.todoTimer.db.TaskDatabase;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bagdi on 12/31/15.
 */
public class NotificationService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final int RequestCode = intent.getIntExtra("UNIQUE_ID", -1);
        String origin = intent.getStringExtra("ORIGIN");
        Log.d("rqOrigin", origin);
        Log.d("MainActivity_rqCdoe", String.valueOf(RequestCode));
        TaskDBHelper sql = new TaskDBHelper(context);
        SQLiteDatabase db = sql.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TABLE, null, TaskContract.Columns._ID + "=?",
                new String[]{String.valueOf(RequestCode)}, null, null, null, null);
        cursor.moveToFirst();
        String task = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));


        if (origin.equals("ADDTASK")) {

            // Toast.makeText(context,"ADDTASKNOTIFCATION",Toast.LENGTH_LONG).show();
            Log.d("rqCodePutting", String.valueOf(RequestCode));
            Intent intentDone = new Intent(context, NotificationService.class);
            intentDone.putExtra("ORIGIN", "DONE");
            //  intentDone.set
            intentDone.putExtra("UNIQUE_ID", RequestCode);
            intentDone.setAction(Long.toString(System.currentTimeMillis()) + "DONE");

            Intent intentSnooze = new Intent(context, NotificationService.class);
            intentSnooze.putExtra("ORIGIN", "SNOOZE");
            intentSnooze.putExtra("UNIQUE_ID", RequestCode);
            intentSnooze.setAction(Long.toString(System.currentTimeMillis()) + "SNOOZE");
            PendingIntent pendingIntentDone = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intentDone, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentSnooze = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String strRingtonePreference = preferences.getString("ring_tone_pref", "DEFAULT_SOUND");
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] pattern = {500, 500, 500, 500};
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_menu_camera)
                            .setContentTitle("Todo Timer")
                            .setContentText(task)
                            .setSound(Uri.parse(strRingtonePreference))
                            .addAction(R.drawable.ic_done, "DONE", pendingIntentDone).addAction(R.drawable.ic_alarm_add, "SNOOZE", pendingIntentSnooze)
                            .setVibrate(pattern);

            NotificationManager notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.notify(RequestCode, mBuilder.build());
        } else if (origin.equals("DONE")) {
            // Toast.makeText(context,"DONETASKNOTIFICAION",Toast.LENGTH_LONG).show();

            TaskDatabase taskDatabase = new TaskDatabase(context);
            taskDatabase.open();
            taskDatabase.deleteById(RequestCode);
            taskDatabase.close();
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_menu_camera)
                            .setContentTitle("Deleted")
                            .setContentText(task);
            final NotificationManager notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.notify(RequestCode, mBuilder.build());
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    notifManager.cancel(RequestCode);
                }
            }, 2000);

        } else if (origin.equals("SNOOZE")) {
            Toast.makeText(context, "SNOOZEASKNOTIFCATION", Toast.LENGTH_LONG).show();


        }
    }

    private void deleteNotification(Context context, int id) {
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        Log.d("Main", "Pending Intent Deleted");
    }
}
