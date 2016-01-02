package com.bagdi.test.todoTimer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.bagdi.test.todoTimer.db.TaskContract;
import com.bagdi.test.todoTimer.db.TaskDBHelper;

/**
 * Created by bagdi on 12/31/15.
 */
public class NotificationService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int RequestCode = intent.getIntExtra("UNIQUE_ID",-1);
         Log.d("MainActivity_rqCdoe", String.valueOf(RequestCode));
        TaskDBHelper sql = new TaskDBHelper(context);
        SQLiteDatabase db = sql.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TABLE, null, TaskContract.Columns._ID + "=?",
                new String[]{String.valueOf(RequestCode)}, null, null, null, null);
        cursor.moveToFirst();
        String task = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500,500,500,500};
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_menu_camera)
                        .setContentTitle("My notification")
                        .setContentText(task)
                        .setSound(uri)
                         .setVibrate(pattern);;

        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(1, mBuilder.build());

    }
}
