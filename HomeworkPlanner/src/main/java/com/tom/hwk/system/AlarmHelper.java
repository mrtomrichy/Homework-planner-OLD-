package com.tom.hwk.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmHelper {

    public void createAlarm(HomeworkItem hwk, ArrayList<HomeworkAlarm> alarms,
                            Context con) {
        for (HomeworkAlarm thisAlarm : alarms) {
            Calendar alarmDate = Calendar.getInstance();
            alarmDate.set(thisAlarm.year, thisAlarm.month, thisAlarm.day,
                    thisAlarm.hour, thisAlarm.minute, 0);
            Intent i = new Intent(con, AlarmReceiver.class);
            i.putExtra("hwk", hwk);
            i.putExtra("id", thisAlarm.id);
            PendingIntent pi = PendingIntent.getBroadcast(con, thisAlarm.id, i,
                    0);
            AlarmManager mAlarm = (AlarmManager) con
                    .getSystemService(Context.ALARM_SERVICE);
            mAlarm.set(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), pi);
        }
    }

    public void deleteAllAlarms(ArrayList<HomeworkAlarm> alarms, Context con) {
        Intent intent = new Intent(con, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) con
                .getSystemService(Context.ALARM_SERVICE);

        for (HomeworkAlarm alarm : alarms) {
            PendingIntent pi = PendingIntent.getBroadcast(
                    con, alarm.id, intent, 0);
            if (pi != null) {
                alarmManager.cancel(pi);
                pi.cancel();
            }
        }
    }

}
