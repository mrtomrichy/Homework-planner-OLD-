package com.tom.hwk.system;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeworkItem implements Parcelable, Comparable<HomeworkItem> {
  public int id;
  public String title;
  public String subject;
  public int day, month, year;
  public String notes;
  public int color;
  public boolean complete;
  public ArrayList<HomeworkAlarm> alarms;

  public static int SORT_NUM = 0;
  private static final int SORT_DATE_ORDER = 0;
  private static final int SORT_DATE_REVERSE = 1;
  private static final int SORT_SUBJECTAZ = 2;
  private static final int SORT_SUBJECTZA = 3;
  private static final int SORT_TITLEAZ = 4;
  private static final int SORT_TITLEZA = 5;

  private static Calendar today = null;
  private Calendar due;

  //standard constructor
  public HomeworkItem() {
    if (today == null) {
      today = Calendar.getInstance();
      today.set(Calendar.HOUR_OF_DAY, 0);
      today.set(Calendar.SECOND, 0);
      today.set(Calendar.MINUTE, 0);
      today.set(Calendar.MILLISECOND, 0);
    }

    this.alarms = new ArrayList<HomeworkAlarm>();
  }

  //constructor for building from Parcel
  public HomeworkItem(Parcel in) {
    this();
    int[] ints = in.createIntArray();
    String[] strings = in.createStringArray();

    id = ints[0];
    day = ints[1];
    month = ints[2];
    year = ints[3];
    color = ints[4];
    complete = ints[5] == 1;

    title = strings[0];
    subject = strings[1];
    notes = strings[2];

    in.readTypedList(alarms, HomeworkAlarm.CREATOR);

    due = Calendar.getInstance();
    due.set(year, month, day, 0, 0, 0);
    due.set(Calendar.MILLISECOND, 0);
  }

  public HomeworkItem(int id, String title, String subject, int day,
                      int month, int year, String notes, int color, boolean complete,
                      ArrayList<HomeworkAlarm> alarms) {
    this();
    this.id = id;
    this.title = title;
    this.subject = subject;
    this.day = day;
    this.month = month;
    this.year = year;
    this.notes = notes;
    this.color = color;
    this.complete = complete;
    this.alarms = alarms;

    due = Calendar.getInstance();
    due.set(year, month, day, 0, 0, 0);
    due.set(Calendar.MILLISECOND, 0);
  }

  public int daysUntilDue() {
    return (int) ((due.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24));
  }

  public boolean isLate() {
    return today.after(due);
  }

  // check if due date is today
  public boolean isToday() {
    return (due.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            && due.get(Calendar.YEAR) == today.get(Calendar.YEAR));
  }

  public boolean isComplete() {
    return complete;
  }

  public int getCompleteAsInt() {
    return complete ? 1 : 0;
  }

  public int describeContents() {
    return 0;
  }

  // write data to parcel
  public void writeToParcel(Parcel dest, int flags) {
    int[] ints = new int[6];
    ints[0] = id;
    ints[1] = day;
    ints[2] = month;
    ints[3] = year;
    ints[4] = color;
    ints[5] = getCompleteAsInt();

    String[] strings = new String[3];
    strings[0] = title;
    strings[1] = subject;
    strings[2] = notes;

    dest.writeIntArray(ints);
    dest.writeStringArray(strings);
    dest.writeTypedList(alarms);
  }

  public static final Creator<HomeworkItem> CREATOR = new Creator<HomeworkItem>() {
    public HomeworkItem createFromParcel(Parcel in) {
      return new HomeworkItem(in);
    }

    public HomeworkItem[] newArray(int size) {
      return new HomeworkItem[size];
    }
  };

  public int compareTo(HomeworkItem other) {
    switch (SORT_NUM) {
      case SORT_DATE_ORDER:
        if (other.due.after(due))
          return -1;
        else if (other.due.before(due))
          return 1;
        else
          return 0;
      case SORT_DATE_REVERSE:
        if (other.due.after(due))
          return 1;
        else if (other.due.before(due))
          return -1;
        else
          return 0;
      case SORT_SUBJECTAZ:
        return subject.compareTo(other.subject);
      case SORT_SUBJECTZA:
        return -subject.compareTo(other.subject);
      case SORT_TITLEAZ:
        return title.compareTo(other.title);
      case SORT_TITLEZA:
        return -title.compareTo(other.title);

    }
    return 0;
  }
}
