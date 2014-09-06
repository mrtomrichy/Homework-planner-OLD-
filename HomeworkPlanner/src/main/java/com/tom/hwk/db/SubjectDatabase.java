package com.tom.hwk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 28/08/2014.
 */
public class SubjectDatabase {

  private final Context mContext;
  private SubjectDB mHelper;
  private SQLiteDatabase mDatabase;

  private static final String DATABASE_NAME = "SubjectDB";
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_SUBJECT_TABLE = "subjects";
  private static final String KEY_ROW_SUBJECT = "subject_name";


  public SubjectDatabase(Context context) {
    this.mContext = context;
  }

  // method to open the database
  public SubjectDatabase open() throws SQLException {
    mHelper = new SubjectDB(mContext);
    mDatabase = mHelper.getWritableDatabase();
    return this;
  }

  // method to close the database
  public void close() {
    mHelper.close();
  }

  public long addSubject(String subject) {
    ContentValues cv = new ContentValues();
    cv.put(KEY_ROW_SUBJECT, subject);
    this.open();
    long id = -1;
    try {
      id = mDatabase.insert(DATABASE_SUBJECT_TABLE, null, cv);
    } catch (Exception e) {
      id = -1;
    } finally {
      this.close();
    }
    return id;
  }

  public List<String> getSubjects() {
    List<String> subjects = new ArrayList<String>();
    String[] columns = {KEY_ROW_SUBJECT};
    this.open();
    Cursor c = mDatabase.query(DATABASE_SUBJECT_TABLE, columns, null, null, null, null, null);

    while (c.moveToNext())
      subjects.add(c.getString(c.getColumnIndex(KEY_ROW_SUBJECT)));

    this.close();
    return subjects;
  }

  public void deleteSubject(String subject) {
    this.open();
    mDatabase.delete(DATABASE_SUBJECT_TABLE, KEY_ROW_SUBJECT + "='" + subject + "'", null);
  }

  private class SubjectDB extends SQLiteOpenHelper {
    // constructor takes context
    public SubjectDB(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the Database
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + DATABASE_SUBJECT_TABLE + "("
          + KEY_ROW_SUBJECT + " TEXT UNIQUE ON CONFLICT IGNORE)");
      DatabaseAccessor dbAccessor = new DatabaseAccessor(mContext);
      List<HomeworkItem> hwks = dbAccessor.getAllHomework();

      for (HomeworkItem h : hwks) {
        db.execSQL("INSERT INTO " + DATABASE_SUBJECT_TABLE + " VALUES('" + h.subject + "')");
      }
    }

    // updates the database if needed
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      onCreate(db);
    }
  }
}
