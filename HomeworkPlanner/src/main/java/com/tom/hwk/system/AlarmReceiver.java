package com.tom.hwk.system;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.tom.hwk.R;
import com.tom.hwk.ViewActivity;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    Bundle b = intent.getExtras();

    if (b != null) {
      HomeworkItem h = b.getParcelable("hwk");
      int id = b.getInt("id");

      HomeworkDatabase db = new HomeworkDatabase(context);
      db.open();
      db.deleteAlarm(id);
      db.close();

      NotificationCompat.Builder mBuilder =
              new NotificationCompat.Builder(context)
                      .setSmallIcon(R.drawable.notification_icon)
                      .setContentTitle("Homework Reminder")
                      .setContentText(h.title + " - " + h.subject)
                      .setAutoCancel(true);

      Intent resultIntent = new Intent(context, ViewActivity.class);
      resultIntent.putExtra("hwk", h);
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
