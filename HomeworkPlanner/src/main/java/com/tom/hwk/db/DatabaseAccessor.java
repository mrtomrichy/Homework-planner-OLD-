package com.tom.hwk.db;

import android.content.Context;
import android.util.Log;

import com.tom.hwk.utils.AlarmUtils;
import com.tom.hwk.utils.HomeworkAlarm;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 19/04/2014.
 */
public class DatabaseAccessor {
  private static Context con;
  private static HomeworkDatabase homeworkDB;
  private static AlarmDatabase alarmDB;
  private static AlarmUtils ma;

  private static List<HomeworkItem> homeworks = null;

  public DatabaseAccessor(Context context) {
    con = context;
    homeworkDB = new HomeworkDatabase(con);
    alarmDB = new AlarmDatabase(con);
    ma = new AlarmUtils();
  }

  public List<HomeworkItem> getAllHomework() {
    if (homeworks == null) {
      homeworks = new ArrayList<HomeworkItem>();
      homeworks.clear();
      homeworkDB.open();

      for (HomeworkItem item : homeworkDB.getAllHomeworks())
        homeworks.add(item);

      homeworkDB.close();
    }

    return homeworks;
  }

  public HomeworkItem getHomeworkWithId(int id) {
    for (HomeworkItem i : getAllHomework())
      if (i.id == id) return i;

    return null;
  }

  public HomeworkItem getHomeworkAtPosition(int position){
    if(position >= getAllHomework().size() || getAllHomework().size() == 0)
      return null;
    return getAllHomework().get(position);
  }

  public void saveHomework(HomeworkItem hwk) {
    homeworkDB.open();
    alarmDB.open();

    hwk.id = (int) homeworkDB.addNewHomework(hwk);
    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.homeworkId = hwk.id;
      alarm.id = (int) alarmDB.addNewAlarm(alarm);
    }
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());

    homeworks.add(hwk);

    homeworkDB.close();
    alarmDB.close();
  }

  public void updateHomework(HomeworkItem hwk, ArrayList<HomeworkAlarm> oldAlarms) {
    homeworkDB.open();
    alarmDB.open();
    homeworkDB.updateHomework(hwk);

    for (HomeworkAlarm alarm : oldAlarms) alarmDB.deleteAlarm(alarm.id);
    ma.deleteAllAlarms(oldAlarms, con.getApplicationContext());
    for (HomeworkAlarm alarm : hwk.alarms) alarm.id = (int) alarmDB.addNewAlarm(alarm);
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());

    homeworkDB.close();
    alarmDB.close();
  }

  public void updateHomeworkStatus(HomeworkItem hwk) {
    homeworkDB.open();
    homeworkDB.updateHomework(hwk);
    homeworkDB.close();
  }

  public void deleteHomework(HomeworkItem hwk) {
    homeworkDB.open();
    alarmDB.open();

    ma.deleteAllAlarms(hwk.alarms, con.getApplicationContext());
    homeworkDB.removeHomework(hwk.id);
    alarmDB.deleteAlarms(hwk.id);

    homeworkDB.close();
    alarmDB.close();
  }
}
