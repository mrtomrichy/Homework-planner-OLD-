package com.tom.hwk.system;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tom.hwk.Main;
import com.tom.hwk.R;

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

            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification mNotification = new Notification(
                    R.drawable.notification_icon,
                    context.getString(R.string.app_name),
                    System.currentTimeMillis());
            PendingIntent pi = PendingIntent.getActivity(context, 0,
                    new Intent(context, Main.class), 0);
            mNotification.setLatestEventInfo(context, "Homework Reminder",
                    h.subject + ", " + h.title, pi);
            mNotification.flags |= Notification.FLAG_AUTO_CANCEL
                    | Notification.DEFAULT_SOUND;
            mNotificationManager.notify(1, mNotification);

        }
    }

}
