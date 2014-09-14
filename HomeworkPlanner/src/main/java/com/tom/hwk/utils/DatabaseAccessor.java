package com.tom.hwk.utils;

import android.content.Context;

import com.tom.hwk.db.AlarmDatabase;
import com.tom.hwk.db.HomeworkDatabase;
import com.tom.hwk.db.SubjectDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tom on 19/04/2014.
 */
public class DatabaseAccessor {
  private static DatabaseAccessor sSharedInstance = null;

  private static Context con;
  private static HomeworkDatabase homeworkDB;
  private static AlarmDatabase alarmDB;
  private static SubjectDatabase subjectDB;
  private static AlarmUtils ma;

  private static List<HomeworkItem> homeworks = null;

  private DatabaseAccessor(Context context) {
    con = context;
    homeworkDB = new HomeworkDatabase(con);
    alarmDB = new AlarmDatabase(con);
    subjectDB = new SubjectDatabase(con);
    ma = new AlarmUtils();
  }

  public static DatabaseAccessor getDBAccessor(Context c)
  {
    if(sSharedInstance == null)
      sSharedInstance = new DatabaseAccessor(c);
    return sSharedInstance;
  }

  public synchronized List<HomeworkItem> getAllHomework() {
    if (homeworks == null)
      getHomeworkFromDatabase();

    return homeworks;
  }

  private synchronized void getHomeworkFromDatabase() {
    if (homeworks == null)
      homeworks = new ArrayList<HomeworkItem>();
    homeworks.clear();

    for (HomeworkItem item : homeworkDB.getAllHomeworks())
      homeworks.add(item);
  }

  public synchronized void addSubject(String subject) {
    subjectDB.addSubject(subject);
  }

  public synchronized List<String> getAllSubjects() {
    List<String> subjects = subjectDB.getSubjects();

    return subjects;
  }

  public synchronized void deleteSubject(String subject) {
    subjectDB.deleteSubject(subject);
  }

  public synchronized HomeworkItem getHomeworkWithId(int id) {
    for (HomeworkItem i : getAllHomework())
      if (i.id == id) return i;

    return null;
  }

  public synchronized void saveHomework(HomeworkItem hwk) {
    hwk.id = (int) homeworkDB.addNewHomework(hwk);
    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.homeworkId = hwk.id;
      alarm.id = (int) alarmDB.addNewAlarm(alarm);
    }
    ma.createAlarmsFromList(hwk.alarms, con.getApplicationContext());
    subjectDB.addSubject(hwk.subject);
    getHomeworkFromDatabase();
  }

  public synchronized void updateHomework(HomeworkItem hwk, ArrayList<HomeworkAlarm> oldAlarms) {
    homeworkDB.updateHomework(hwk);

    for (HomeworkAlarm alarm : oldAlarms) alarmDB.deleteAlarm(alarm.id);
    ma.deleteAllAlarms(oldAlarms, con.getApplicationContext());
    for (HomeworkAlarm alarm : hwk.alarms) alarm.id = (int) alarmDB.addNewAlarm(alarm);
    ma.createAlarmsFromList(hwk.alarms, con.getApplicationContext());
    subjectDB.addSubject(hwk.subject);
    getHomeworkFromDatabase();
  }

  public synchronized void updateHomeworkStatus(HomeworkItem hwk) {
    homeworkDB.updateHomework(hwk);
  }

  public synchronized void deleteHomework(HomeworkItem hwk) {
    ma.deleteAllAlarms(hwk.alarms, con.getApplicationContext());
    homeworkDB.removeHomework(hwk.id);
    alarmDB.deleteAlarmsForHomework(hwk.id);
    getHomeworkFromDatabase();
  }

  public synchronized long addAlarm(HomeworkAlarm alarm) {
    return alarmDB.addNewAlarm(alarm);
  }

  public synchronized void deleteAlarm(int id, HomeworkItem hwk) {
    alarmDB.deleteAlarm(id);

    for (Iterator<HomeworkAlarm> it = hwk.alarms.iterator(); it.hasNext(); ) {
      HomeworkAlarm alarm = it.next();
      if (alarm.id == id)
        it.remove();
    }

  }
}
