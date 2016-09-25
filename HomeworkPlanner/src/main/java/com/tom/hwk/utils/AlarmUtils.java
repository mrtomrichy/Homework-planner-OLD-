package com.tom.hwk.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tom.hwk.models.HomeworkAlarm;
import com.tom.hwk.models.HomeworkItem;

import java.util.Calendar;
import java.util.List;

public class AlarmUtils {

  public static void createAlarmsFromList(List<HomeworkAlarm> alarms, Context con) {
    for (HomeworkAlarm thisAlarm : alarms)
      createAlarm(thisAlarm, con);
  }

  public static void createAlarm(HomeworkAlarm thisAlarm, Context con) {
    Calendar alarmDate = Calendar.getInstance();
    alarmDate.set(thisAlarm.year, thisAlarm.month, thisAlarm.day,
        thisAlarm.hour, thisAlarm.minute, 0);
    Intent i = new Intent(con, AlarmReceiver.class);
    i.putExtra(HomeworkItem.ID_TAG, thisAlarm.homeworkId);
    i.putExtra(HomeworkAlarm.ID_TAG, thisAlarm.id);
    PendingIntent pi = PendingIntent.getBroadcast(con, thisAlarm.id, i,
        0);
    AlarmManager mAlarm = (AlarmManager) con
        .getSystemService(Context.ALARM_SERVICE);
    mAlarm.set(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), pi);
  }

  public static void deleteAlarms(List<HomeworkAlarm> alarms, Context con) {
    for (HomeworkAlarm alarm : alarms) {
      deleteAlarm(alarm, con);
    }
  }

  public static void deleteAlarm(HomeworkAlarm alarm, Context con) {
    Intent intent = new Intent(con, AlarmReceiver.class);
    AlarmManager alarmManager = (AlarmManager) con
        .getSystemService(Context.ALARM_SERVICE);
    PendingIntent pi = PendingIntent.getBroadcast(
        con, alarm.id, intent, 0);
    if (pi != null) {
      alarmManager.cancel(pi);
      pi.cancel();
    }
  }

}
