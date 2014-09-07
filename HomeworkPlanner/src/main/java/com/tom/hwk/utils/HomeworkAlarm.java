package com.tom.hwk.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeworkAlarm implements Parcelable {
  public static final String ID_TAG = "alarm_id";

  public int id, homeworkId;
  public int day, month, year, hour, minute;

  private static AlarmUtils helper = null;

  public HomeworkAlarm(){
    if(helper == null) helper = new AlarmUtils();
  }

  public HomeworkAlarm(int id, int day, int month, int year, int hour, int minute, int homeworkId) {
    this();
    this.id = id;
    this.homeworkId = homeworkId;
    this.day = day;
    this.month = month;
    this.year = year;
    this.hour = hour;
    this.minute = minute;
  }

  public HomeworkAlarm(Parcel parcel) {
    this();
    int[] ints = parcel.createIntArray();
    this.id = ints[0];
    this.homeworkId = ints[1];
    this.day = ints[2];
    this.month = ints[3];
    this.year = ints[4];
    this.hour = ints[5];
    this.minute = ints[6];
  }

  public int describeContents() {
    return 0;
  }

  // write data to parcel
  public void writeToParcel(Parcel dest, int flags) {
    int[] ints = new int[7];
    ints[0] = id;
    ints[1] = homeworkId;
    ints[2] = day;
    ints[3] = month;
    ints[4] = year;
    ints[5] = hour;
    ints[6] = minute;

    dest.writeIntArray(ints);

  }

  public static final Creator<HomeworkAlarm> CREATOR = new Creator<HomeworkAlarm>() {
    public HomeworkAlarm createFromParcel(Parcel in) {
      return new HomeworkAlarm(in);
    }

    public HomeworkAlarm[] newArray(int size) {
      return new HomeworkAlarm[size];
    }
  };

  public String getDate() {
    return String.format("%02d", day) + "/" + String.format("%02d", month + 1) + "/" + String.format("%04d", year);
  }

  public String getTime() {
    return String.format("%02d", hour) + ":" + String.format("%02d", minute);
  }
}
