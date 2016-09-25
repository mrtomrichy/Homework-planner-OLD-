package com.tom.hwk.utils;

import android.content.Context;

import com.tom.hwk.db.AlarmDatabase;
import com.tom.hwk.db.HomeworkDatabase;
import com.tom.hwk.db.SubjectDatabase;
import com.tom.hwk.models.HomeworkAlarm;
import com.tom.hwk.models.HomeworkItem;

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

  private static List<HomeworkItem> homework = null;

  private DatabaseAccessor(Context context) {
    con = context;
    homeworkDB = new HomeworkDatabase(con);
    alarmDB = new AlarmDatabase(con);
    subjectDB = new SubjectDatabase(con);
  }

  public static DatabaseAccessor getDBAccessor(Context c)
  {
    if(sSharedInstance == null)
      sSharedInstance = new DatabaseAccessor(c);
    return sSharedInstance;
  }

  private synchronized void getHomeworkFromDatabase() {
    if (homework == null)
      homework = new ArrayList<>();
    homework.clear();

    for (HomeworkItem item : homeworkDB.getAllHomeworks())
      homework.add(item);
  }

  public synchronized List<HomeworkItem> getHomework() {
    if (homework == null)
      getHomeworkFromDatabase();

    return homework;
  }

  public synchronized HomeworkItem getHomeworkWithId(int id) {
    for (HomeworkItem i : getHomework())
      if (i.id == id) return i;

    return null;
  }

  public synchronized void addHomework(HomeworkItem hwk) {
    hwk.id = (int) homeworkDB.addNewHomework(hwk);
    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.homeworkId = hwk.id;
      alarm.id = (int) alarmDB.addNewAlarm(alarm);
    }
    AlarmUtils.createAlarmsFromList(hwk.alarms, con.getApplicationContext());
    subjectDB.addSubject(hwk.subject);
    getHomeworkFromDatabase();
  }

  public synchronized void updateHomework(HomeworkItem hwk) {
    List<HomeworkAlarm> oldAlarms = alarmDB.getAlarmsForHomework(hwk.id);
    homeworkDB.updateHomework(hwk);

    for (HomeworkAlarm alarm : oldAlarms) {
      alarmDB.deleteAlarm(alarm.id);
      AlarmUtils.deleteAlarm(alarm, con.getApplicationContext());
    }

    for (HomeworkAlarm alarm : hwk.alarms) {
      alarm.id = (int) alarmDB.addNewAlarm(alarm);
      AlarmUtils.createAlarm(alarm, con.getApplicationContext());
    }

    subjectDB.addSubject(hwk.subject);
    getHomeworkFromDatabase();
  }

  public synchronized void updateHomeworkStatus(HomeworkItem hwk) {
    homeworkDB.updateHomework(hwk);
  }

  public synchronized void deleteHomework(HomeworkItem hwk) {
    AlarmUtils.deleteAlarms(hwk.alarms, con.getApplicationContext());
    alarmDB.deleteAlarmsForHomework(hwk.id);
    homeworkDB.removeHomework(hwk.id);
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

  public synchronized List<String> getSubjects() {
    return subjectDB.getSubjects();
  }

  public synchronized void addSubject(String subject) {
    subjectDB.addSubject(subject);
  }

  public synchronized void deleteSubject(String subject) {
    subjectDB.deleteSubject(subject);
  }
}
