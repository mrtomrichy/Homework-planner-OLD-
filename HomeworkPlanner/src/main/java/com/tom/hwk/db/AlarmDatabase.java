package com.tom.hwk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tom.hwk.utils.HomeworkAlarm;

import java.util.ArrayList;

/**
 * Created by tom on 24/08/2014.
 */
public class AlarmDatabase {

  private Context mContext;

  private SQLiteDatabase mDatabase = null;
  private AlarmDB mHelper;

  public static int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "alarmDB";
  public static final String DATABASE_ALARM_TABLE = "alarms";

  public static final String KEY_ALARM_ID = "_id";
  public static final String KEY_ALARM_HOMEWORK_ID = "homework_id";
  public static final String KEY_ALARM_DAY = "_day";
  public static final String KEY_ALARM_MONTH = "_month";
  public static final String KEY_ALARM_YEAR = "_year";
  public static final String KEY_ALARM_HOUR = "_hour";
  public static final String KEY_ALARM_MINUTE = "_minute";

  public AlarmDatabase(Context mContext){
    this.mContext = mContext;
  }

  // method to open the database
  public AlarmDatabase open() throws SQLException {
    mHelper = new AlarmDB(mContext);
    mDatabase = mHelper.getWritableDatabase();
    return this;
  }

  // method to close the database
  public void close() {
    mHelper.close();
  }

  public long addNewAlarm(HomeworkAlarm alarm) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_ALARM_HOMEWORK_ID, alarm.homeworkId);
    cv.put(KEY_ALARM_DAY, alarm.day);
    cv.put(KEY_ALARM_MONTH, alarm.month);
    cv.put(KEY_ALARM_YEAR, alarm.year);
    cv.put(KEY_ALARM_MINUTE, alarm.minute);
    cv.put(KEY_ALARM_HOUR, alarm.hour);
    this.open();
    long id = mDatabase.insert(DATABASE_ALARM_TABLE, null, cv);
    
    this.close();
    return id;
  }

  public void deleteAlarmsForHomework(int homeworkID) {
    this.open();
    mDatabase.delete(DATABASE_ALARM_TABLE, KEY_ALARM_HOMEWORK_ID + " = "
        + homeworkID, null);
    this.close();
  }

  public void deleteAlarm(int alarmID) {
    this.open();
    mDatabase.delete(DATABASE_ALARM_TABLE, KEY_ALARM_ID + " = " + alarmID, null);
    this.close();
  }

  public ArrayList<HomeworkAlarm> getAlarmsForHomework(int homeworkID) {
    ArrayList<HomeworkAlarm> alarms = getAllAlarms(KEY_ALARM_HOMEWORK_ID + " = " + homeworkID);
    return alarms;
  }

  public ArrayList<HomeworkAlarm> getAllAlarms(String whereString) {
    String[] columns = new String[]{KEY_ALARM_ID, KEY_ALARM_HOMEWORK_ID,
        KEY_ALARM_DAY, KEY_ALARM_MONTH, KEY_ALARM_YEAR,
        KEY_ALARM_MINUTE, KEY_ALARM_HOUR};
    this.open();
    Cursor c = mDatabase.query(DATABASE_ALARM_TABLE, columns, whereString, null,
        null, null, null);

    int id = c.getColumnIndex(KEY_ALARM_ID);
    int homeworkId = c.getColumnIndex(KEY_ALARM_HOMEWORK_ID);
    int iDay = c.getColumnIndex(KEY_ALARM_DAY);
    int iMonth = c.getColumnIndex(KEY_ALARM_MONTH);
    int iYear = c.getColumnIndex(KEY_ALARM_YEAR);
    int iHour = c.getColumnIndex(KEY_ALARM_HOUR);
    int iMinute = c.getColumnIndex(KEY_ALARM_MINUTE);

    ArrayList<HomeworkAlarm> alarms = new ArrayList<HomeworkAlarm>();

    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      HomeworkAlarm thisAlarm = new HomeworkAlarm(c.getInt(id), c.getInt(iDay),
          c.getInt(iMonth), c.getInt(iYear), c.getInt(iHour),
          c.getInt(iMinute), c.getInt(homeworkId));

      alarms.add(thisAlarm);
    }
    this.close();

    return alarms;
  }

  private class AlarmDB extends SQLiteOpenHelper {
    Context mContext;

    public AlarmDB(Context c) {
      super(c, DATABASE_NAME, null, DATABASE_VERSION);
      this.mContext = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + DATABASE_ALARM_TABLE + "("
          + KEY_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + KEY_ALARM_HOMEWORK_ID + " INTEGER NOT NULL, "
          + KEY_ALARM_DAY + " INTEGER NOT NULL, " + KEY_ALARM_MONTH
          + " INTEGER NOT NULL, " + KEY_ALARM_YEAR
          + " INTEGER NOT NULL, " + KEY_ALARM_MINUTE
          + " INTEGER NOT NULL, " + KEY_ALARM_HOUR
          + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }
}
