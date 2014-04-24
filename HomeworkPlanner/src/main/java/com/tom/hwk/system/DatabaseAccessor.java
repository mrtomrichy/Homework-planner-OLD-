package com.tom.hwk.system;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Tom on 19/04/2014.
 */
public class DatabaseAccessor {
  private Context con;
  private HomeworkDatabase db;
  private AlarmHelper ma;

  public DatabaseAccessor(Context con) {
    this.con = con;
    this.db = new HomeworkDatabase(con);
    this.ma = new AlarmHelper();
  }

  public ArrayList<HomeworkItem> getHomework(){
    db.open();
    ArrayList<HomeworkItem> hwks = db.getAllHomeworks();
    db.close();
    return hwks;
  }

  public void addNewHomework(HomeworkItem hwk) {
    db.open();
    hwk.id = (int)db.addNewHomework(hwk);
    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.homeworkId = hwk.id;
      alarm.id = (int)db.addNewAlarm(alarm);
    }
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());
    db.close();
  }

  public void updateHomework(HomeworkItem hwk, ArrayList<HomeworkAlarm> oldAlarms) {
    db.open();
    db.updateHomework(hwk);
    for (HomeworkAlarm alarm : oldAlarms) db.deleteAlarm(alarm.id);
    ma.deleteAllAlarms(oldAlarms, con.getApplicationContext());
    for (HomeworkAlarm alarm : hwk.alarms) alarm.id = (int) db.addNewAlarm(alarm);
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());
    db.close();
  }

  public void updateHomeworkStatus(HomeworkItem hwk){
    db.open();
    db.updateHomework(hwk);
    db.close();
  }

  public void deleteHomework(HomeworkItem hwk){
    db.open();
    ma.deleteAllAlarms(hwk.alarms, con.getApplicationContext());
    db.removeHomework(hwk.id);
    db.deleteAlarms(hwk.id);
    db.close();
  }
}
