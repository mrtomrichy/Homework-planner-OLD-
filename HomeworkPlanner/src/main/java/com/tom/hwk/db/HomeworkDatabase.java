package com.tom.hwk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tom.hwk.utils.HomeworkAlarm;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.Collections;

public class HomeworkDatabase {

  public static final String KEY_ROW_ID = "_id";
  public static final String KEY_TITLE = "homework_title";
  public static final String KEY_SUBJECT = "homework_subject";
  public static final String KEY_DUE_DAY = "homework_due_day";
  public static final String KEY_DUE_MONTH = "homework_due_month";
  public static final String KEY_DUE_YEAR = "homework_due_year";
  public static final String KEY_NOTES = "homework_notes";
  public static final String KEY_COLOR_CODE = "homework_color_code";
  public static final String KEY_COMPLETE = "homework_complete";

  public static final String DATABASE_NAME = "homeworkDB";
  public static final String DATABASE_HOMEWORK_TABLE = "homeworks";

  public static final int DATABASE_VERSION = 4;
  private final Context mContext;
  private HomeworkDB mHelper;
  private SQLiteDatabase mDatabase;

  // method to initialise the database
  public HomeworkDatabase(Context c) {
    mContext = c;
  }

  // method to open the database
  public HomeworkDatabase open() throws SQLException {
    mHelper = new HomeworkDB(mContext);
    mDatabase = mHelper.getWritableDatabase();
    return this;
  }

  // method to close the database
  public void close() {
    mHelper.close();
  }

  // method to get all homeworks
  public ArrayList<HomeworkItem> getAllHomeworks() {
    String[] columns = new String[]{KEY_ROW_ID, KEY_TITLE, KEY_SUBJECT,
            KEY_DUE_DAY, KEY_DUE_MONTH, KEY_DUE_YEAR, KEY_NOTES,
            KEY_COLOR_CODE, KEY_COMPLETE};
    Cursor c = mDatabase.query(DATABASE_HOMEWORK_TABLE, columns, null,
            null, null, null, null);
    ArrayList<HomeworkItem> hwks = new ArrayList<HomeworkItem>();

    int id = c.getColumnIndex(KEY_ROW_ID);
    int iTitle = c.getColumnIndex(KEY_TITLE);
    int iSub = c.getColumnIndex(KEY_SUBJECT);
    int iDay = c.getColumnIndex(KEY_DUE_DAY);
    int iMonth = c.getColumnIndex(KEY_DUE_MONTH);
    int iYear = c.getColumnIndex(KEY_DUE_YEAR);
    int iNotes = c.getColumnIndex(KEY_NOTES);
    int iColor = c.getColumnIndex(KEY_COLOR_CODE);
    int iComplete = c.getColumnIndex(KEY_COMPLETE);

    AlarmDatabase alarmDB = new AlarmDatabase(mContext);
    alarmDB.open();

    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      HomeworkItem h = new HomeworkItem(c.getInt(id), c.getString(iTitle), c.getString(iSub),
              Integer.parseInt(c.getString(iDay)),
              Integer.parseInt(c.getString(iMonth)),
              Integer.parseInt(c.getString(iYear)),
              c.getString(iNotes),
              Integer.parseInt(c.getString(iColor)),
              Integer.parseInt(c.getString(iComplete)) == 1,
              alarmDB.getAlarmsForHomework(c.getInt(id)));
      hwks.add(h);
    }
    alarmDB.close();

    Collections.sort(hwks);
    return hwks;
  }

  public long addNewHomework(HomeworkItem hwk) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_TITLE, hwk.title);
    cv.put(KEY_SUBJECT, hwk.subject);
    cv.put(KEY_NOTES, hwk.notes);
    cv.put(KEY_DUE_DAY, hwk.day);
    cv.put(KEY_DUE_MONTH, hwk.month);
    cv.put(KEY_DUE_YEAR, hwk.year);
    cv.put(KEY_COLOR_CODE, hwk.color);
    cv.put(KEY_COMPLETE, hwk.getCompleteAsInt());
    return mDatabase.insert(DATABASE_HOMEWORK_TABLE, null, cv);
  }

  // method to update a homework
  public void updateHomework(HomeworkItem hwk) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_TITLE, hwk.title);
    cv.put(KEY_SUBJECT, hwk.subject);
    cv.put(KEY_NOTES, hwk.notes);
    cv.put(KEY_DUE_DAY, hwk.day);
    cv.put(KEY_DUE_MONTH, hwk.month);
    cv.put(KEY_DUE_YEAR, hwk.year);
    cv.put(KEY_COLOR_CODE, hwk.color);
    cv.put(KEY_COMPLETE, hwk.getCompleteAsInt());
    mDatabase.update(DATABASE_HOMEWORK_TABLE, cv, KEY_ROW_ID + "=" + hwk.id,
        null);
  }

  // method to remove a homework
  public void removeHomework(int id) {
    mDatabase.delete(DATABASE_HOMEWORK_TABLE, KEY_ROW_ID + "=" + id, null);
  }



  // This class creates the database
  private static class HomeworkDB extends SQLiteOpenHelper {
    // constructor takes context
    public HomeworkDB(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the Database
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + DATABASE_HOMEWORK_TABLE + "("
              + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
              + KEY_TITLE + " TEXT NOT NULL, " + KEY_SUBJECT
              + " TEXT NOT NULL, " + KEY_DUE_DAY + " INTEGER NOT NULL, "
              + KEY_DUE_MONTH + " INTEGER NOT NULL, " + KEY_DUE_YEAR
              + " INTEGER NOT NULL, " + KEY_NOTES + " LONGTEXT NOT NULL, "
              + KEY_COLOR_CODE + " INTEGER NOT NULL, "
              + KEY_COMPLETE + " INTEGER NOT NULL)");

    }

    // updates the database if needed
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if (oldVersion == 2) {
        db.execSQL("ALTER TABLE " + DATABASE_HOMEWORK_TABLE + " ADD COLUMN "
                + KEY_COLOR_CODE + " INTEGER NOT NULL DEFAULT -13388315;");
        db.execSQL("ALTER TABLE " + DATABASE_HOMEWORK_TABLE + " ADD COLUMN "
                + KEY_COMPLETE + " INTEGER NOT NULL DEFAULT 0;");
      } else if (oldVersion == 3) {
        db.execSQL("ALTER TABLE " + DATABASE_HOMEWORK_TABLE + " ADD COLUMN "
                + KEY_COMPLETE + " INTEGER NOT NULL DEFAULT 0;");
      } else {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_HOMEWORK_TABLE);
        onCreate(db);
      }
    }

  }

}// HomeworkDatabase
