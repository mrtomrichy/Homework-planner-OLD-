package com.tom.hwk.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class HomeworkDatabase {

    public static final String KEY_ROWID = "_id";
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
    public static final String DATABASE_ALARM_TABLE = "alarms";

    public static final String KEY_ALARM_ID = "_id";
    public static final String KEY_ALARM_HOMEWORK_ID = "homework_id";
    public static final String KEY_ALARM_DAY = "_day";
    public static final String KEY_ALARM_MONTH = "_month";
    public static final String KEY_ALARM_YEAR = "_year";
    public static final String KEY_ALARM_HOUR = "_hour";
    public static final String KEY_ALARM_MINUTE = "_minute";

    public static final int DATABASE_VERSION = 4;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    // method to initialise the database
    public HomeworkDatabase(Context c) {
        ourContext = c;
    }

    // method to open the database
    public HomeworkDatabase open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    // method to close the database
    public void close() {
        ourHelper.close();
    }

    public long addHomeworkToDatabase(HomeworkItem hwk) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, hwk.title);
        cv.put(KEY_SUBJECT, hwk.subject);
        cv.put(KEY_NOTES, hwk.notes);
        cv.put(KEY_DUE_DAY, hwk.day);
        cv.put(KEY_DUE_MONTH, hwk.month);
        cv.put(KEY_DUE_YEAR, hwk.year);
        cv.put(KEY_COLOR_CODE, hwk.color);
        cv.put(KEY_COMPLETE, hwk.getComplete());
        return ourDatabase.insert(DATABASE_HOMEWORK_TABLE, null, cv);
    }

    // method to get all homeworks
    public ArrayList<HomeworkItem> getHomeworks() {
        String[] columns = new String[]{KEY_ROWID, KEY_TITLE, KEY_SUBJECT,
                KEY_DUE_DAY, KEY_DUE_MONTH, KEY_DUE_YEAR, KEY_NOTES,
                KEY_COLOR_CODE, KEY_COMPLETE};
        Cursor c = ourDatabase.query(DATABASE_HOMEWORK_TABLE, columns, null,
                null, null, null, null);
        ArrayList<HomeworkItem> hwks = new ArrayList<HomeworkItem>();

        int id = c.getColumnIndex(KEY_ROWID);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iSub = c.getColumnIndex(KEY_SUBJECT);
        int iDay = c.getColumnIndex(KEY_DUE_DAY);
        int iMonth = c.getColumnIndex(KEY_DUE_MONTH);
        int iYear = c.getColumnIndex(KEY_DUE_YEAR);
        int iNotes = c.getColumnIndex(KEY_NOTES);
        int iColor = c.getColumnIndex(KEY_COLOR_CODE);
        int iComplete = c.getColumnIndex(KEY_COMPLETE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            HomeworkItem h = new HomeworkItem();
            h.addData(c.getInt(id), c.getString(iTitle), c.getString(iSub),
                    Integer.parseInt(c.getString(iDay)),
                    Integer.parseInt(c.getString(iMonth)),
                    Integer.parseInt(c.getString(iYear)),
                    c.getString(iNotes),
                    Integer.parseInt(c.getString(iColor)),
                    Integer.parseInt(c.getString(iComplete)) == 1);
            hwks.add(h);
        }

        Collections.sort(hwks);
        return hwks;
    }

    // method to update a homework
    public void updateEntry(HomeworkItem hwk) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, hwk.title);
        cv.put(KEY_SUBJECT, hwk.subject);
        cv.put(KEY_NOTES, hwk.notes);
        cv.put(KEY_DUE_DAY, hwk.day);
        cv.put(KEY_DUE_MONTH, hwk.month);
        cv.put(KEY_DUE_YEAR, hwk.year);
        cv.put(KEY_COLOR_CODE, hwk.color);
        cv.put(KEY_COMPLETE, hwk.getComplete());
        ourDatabase.update(DATABASE_HOMEWORK_TABLE, cv, KEY_ROWID + "=" + hwk.id,
                null);
    }

    // method to remove a homework
    public void removeEntry(int id) {
        ourDatabase.delete(DATABASE_HOMEWORK_TABLE, KEY_ROWID + "=" + id, null);
    }

    public long addAlarm(HomeworkAlarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ALARM_HOMEWORK_ID, alarm.homeworkId);
        cv.put(KEY_ALARM_DAY, alarm.day);
        cv.put(KEY_ALARM_MONTH, alarm.month);
        cv.put(KEY_ALARM_YEAR, alarm.year);
        cv.put(KEY_ALARM_MINUTE, alarm.minute);
        cv.put(KEY_ALARM_HOUR, alarm.hour);
        return ourDatabase.insert(DATABASE_ALARM_TABLE, null, cv);
    }

    public ArrayList<HomeworkAlarm> getAlarmsById(int homeworkID) {
        ArrayList<HomeworkAlarm> alarms = getAlarms();
        Iterator it = alarms.iterator();
        while (it.hasNext()) {
            if (((HomeworkAlarm) it.next()).homeworkId != homeworkID) {
                it.remove();
            }
        }
        return alarms;
    }

    public void deleteAlarms(int homeworkID) {
        ourDatabase.delete(DATABASE_ALARM_TABLE, KEY_ALARM_HOMEWORK_ID + " = "
                + homeworkID, null);
    }

    public void deleteAlarm(int id) {
        ourDatabase.delete(DATABASE_ALARM_TABLE, KEY_ALARM_ID + " = " + id, null);
    }

    public ArrayList<HomeworkAlarm> getAlarms() {
        String[] columns = new String[]{KEY_ALARM_ID, KEY_ALARM_HOMEWORK_ID,
                KEY_ALARM_DAY, KEY_ALARM_MONTH, KEY_ALARM_YEAR,
                KEY_ALARM_MINUTE, KEY_ALARM_HOUR};

        Cursor c = ourDatabase.query(DATABASE_ALARM_TABLE, columns, null, null,
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
            HomeworkAlarm thisAlarm = new HomeworkAlarm(c.getInt(iDay),
                    c.getInt(iMonth), c.getInt(iYear), c.getInt(iHour),
                    c.getInt(iMinute));
            thisAlarm.id = c.getInt(id);
            thisAlarm.homeworkId = c.getInt(homeworkId);

            alarms.add(thisAlarm);
        }

        return alarms;
    }

    // This class creates the database
    private static class DbHelper extends SQLiteOpenHelper {
        // constructor takes context
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        // creates the Database
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE " + DATABASE_HOMEWORK_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TITLE + " TEXT NOT NULL, " + KEY_SUBJECT
                    + " TEXT NOT NULL, " + KEY_DUE_DAY + " INTEGER NOT NULL, "
                    + KEY_DUE_MONTH + " INTEGER NOT NULL, " + KEY_DUE_YEAR
                    + " INTEGER NOT NULL, " + KEY_NOTES + " LONGTEXT NOT NULL, "
                    + KEY_COLOR_CODE + " INTEGER NOT NULL, "
                    + KEY_COMPLETE + " INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE " + DATABASE_ALARM_TABLE + "("
                    + KEY_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ALARM_HOMEWORK_ID + " INTEGER NOT NULL, "
                    + KEY_ALARM_DAY + " INTEGER NOT NULL, " + KEY_ALARM_MONTH
                    + " INTEGER NOT NULL, " + KEY_ALARM_YEAR
                    + " INTEGER NOT NULL, " + KEY_ALARM_MINUTE
                    + " INTEGER NOT NULL, " + KEY_ALARM_HOUR
                    + " INTEGER NOT NULL)");
        }

        // updates the database if needed
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
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
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ALARM_TABLE);
                onCreate(db);

            }
        }

    }

}// HomeworkDatabase
