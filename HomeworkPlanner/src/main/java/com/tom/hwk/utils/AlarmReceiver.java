package com.tom.hwk.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.tom.hwk.R;
import com.tom.hwk.db.AlarmDatabase;
import com.tom.hwk.ui.ViewActivity;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    Bundle b = intent.getExtras();
    DatabaseAccessor dbAccessor = new DatabaseAccessor(context);
    if (b != null) {
      HomeworkItem h = dbAccessor.getHomeworkWithId(b.getInt(HomeworkItem.ID_TAG));
      int id = b.getInt(HomeworkAlarm.ID_TAG);

      DatabaseAccessor db = new DatabaseAccessor(context);
      db.deleteAlarm(id, h);


      NotificationCompat.Builder mBuilder =
              new NotificationCompat.Builder(context)
                      .setSmallIcon(R.drawable.notification_icon)
                      .setContentTitle("Homework Reminder")
                      .setContentText(h.title + " - " + h.subject)
                      .setAutoCancel(true);

      Intent resultIntent = new Intent(context, ViewActivity.class);
      resultIntent.putExtra(HomeworkItem.ID_TAG, h.id);
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      stackBuilder.addParentStack(ViewActivity.class);
      stackBuilder.addNextIntent(resultIntent);
      PendingIntent resultPendingIntent =
              stackBuilder.getPendingIntent(
                      0,
                      PendingIntent.FLAG_UPDATE_CURRENT
              );
      mBuilder.setContentIntent(resultPendingIntent);
      NotificationManager mNotificationManager =
              (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      mNotificationManager.notify(id, mBuilder.build());

    }
  }

}
