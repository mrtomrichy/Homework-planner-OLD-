package com.tom.hwk.utils;

import android.content.Context;
import android.util.Log;

import com.tom.hwk.db.AlarmDatabase;
import com.tom.hwk.db.HomeworkDatabase;
import com.tom.hwk.db.SubjectDatabase;
import com.tom.hwk.utils.AlarmUtils;
import com.tom.hwk.utils.HomeworkAlarm;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

/**
 * Created by Tom on 19/04/2014.
 */
public class DatabaseAccessor {
  private static Context con;
  private static HomeworkDatabase homeworkDB;
  private static AlarmDatabase alarmDB;
  private static SubjectDatabase subjectDB;
  private static AlarmUtils ma;

  private static List<HomeworkItem> homeworks = null;

  public DatabaseAccessor(Context context) {
    con = context;
    homeworkDB = new HomeworkDatabase(con);
    alarmDB = new AlarmDatabase(con);
    subjectDB = new SubjectDatabase(con);
    ma = new AlarmUtils();
  }

  public List<HomeworkItem> getAllHomework() {
    if (homeworks == null) {
      homeworks = new ArrayList<HomeworkItem>();
      homeworks.clear();

      for (HomeworkItem item : homeworkDB.getAllHomeworks())
        homeworks.add(item);
    }

    return homeworks;
  }

  public void addSubject(String subject){
    subjectDB.addSubject(subject);
  }

  public List<String> getAllSubjects(){
    List<String> subjects = subjectDB.getSubjects();

    return subjects;
  }

  public void deleteSubject(String subject){
    subjectDB.deleteSubject(subject);
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
    hwk.id = (int) homeworkDB.addNewHomework(hwk);
    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.homeworkId = hwk.id;
      alarm.id = (int) alarmDB.addNewAlarm(alarm);
    }
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());
    subjectDB.addSubject(hwk.subject);
    homeworks.add(hwk);
  }

  public void updateHomework(HomeworkItem hwk, ArrayList<HomeworkAlarm> oldAlarms) {
    homeworkDB.updateHomework(hwk);

    for (HomeworkAlarm alarm : oldAlarms) alarmDB.deleteAlarm(alarm.id);
    ma.deleteAllAlarms(oldAlarms, con.getApplicationContext());
    for (HomeworkAlarm alarm : hwk.alarms) alarm.id = (int) alarmDB.addNewAlarm(alarm);
    ma.createAlarmsFromList(hwk, hwk.alarms, con.getApplicationContext());
    subjectDB.addSubject(hwk.subject);
  }

  public void updateHomeworkStatus(HomeworkItem hwk) {
    homeworkDB.updateHomework(hwk);
  }

  public void deleteHomework(HomeworkItem hwk) {
    ma.deleteAllAlarms(hwk.alarms, con.getApplicationContext());
    homeworkDB.removeHomework(hwk.id);
    alarmDB.deleteAlarms(hwk.id);
  }

  public void addAlarm(HomeworkAlarm alarm){
    alarmDB.addNewAlarm(alarm);
  }
}
