package com.tom.hwk.system;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class HomeworkItem implements Parcelable, Comparable<HomeworkItem> {
    public int id;
    public String title;
    public String subject;
    public int day, month, year;
    public String notes;
    public Date dueDate;
    public static int SORT_NUM = 0;
    public static int SORT_DATE_ORDER = 0;
    public static int SORT_DATE_REVERSE = 1;
    public static int SORT_SUBJECTAZ = 2;
    public static int SORT_SUBJECTZA = 3;
    public static int SORT_TITLEAZ = 4;
    public static int SORT_TITLEZA = 5;
    public int color;
    public boolean complete;

    //constructor for building from Parcel
    public HomeworkItem(Parcel in) {
        int[] ints = in.createIntArray();
        String[] strings = in.createStringArray();

        id = ints[0];
        day = ints[1];
        month = ints[2];
        year = ints[3];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        dueDate = cal.getTime();

        title = strings[0];
        subject = strings[1];
        notes = strings[2];
        color = ints[4];
        complete = ints[5] == 1;
    }

    //standard constructor
    public HomeworkItem() {

    }
    public HomeworkItem(String title, String subject, int day,
                        int month, int year, String notes, int color, boolean complete) {
        this.title = title;
        this.subject = subject;
        this.day = day;
        this.month = month;
        this.year = year;
        this.notes = notes;
        this.color = color;
        this.complete = complete;
    }

    public boolean isLate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day, 0, 0);

        return c.after(c2);
    }

    public void addData(int id, String title, String subject, int day,
                        int month, int year, String notes, int color, boolean complete) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.day = day;
        this.month = month;
        this.year = year;
        this.notes = notes;
        this.color = color;
        this.complete = complete;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0, 0);
        dueDate = new Date(c.getTimeInMillis());
    }

    // check if due date is today
    public boolean isToday() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day, 0, 0);

        Date d = c.getTime();
        Date d2 = c2.getTime();

        if (d.getYear() == d2.getYear() && d.getMonth() == d2.getMonth()
                && d.getDay() == d2.getDay()) {
            return true;
        } else {
            return false;
        }

    }

    public int getComplete(){
        return complete ? 1 : 0;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    // write data to parcel
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        int[] ints = new int[6];
        ints[0] = id;
        ints[1] = day;
        ints[2] = month;
        ints[3] = year;
        ints[4] = color;
        ints[5] = getComplete();

        String[] strings = new String[3];
        strings[0] = title;
        strings[1] = subject;
        strings[2] = notes;

        dest.writeIntArray(ints);
        dest.writeStringArray(strings);

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
            case 0:
                if (other.dueDate.after(dueDate))
                    return -1;
                else if (other.dueDate.before(dueDate))
                    return 1;
                else
                    return 0;
            case 1:
                if (other.dueDate.after(dueDate))
                    return 1;
                else if (other.dueDate.before(dueDate))
                    return -1;
                else
                    return 0;
            case 2:
                return subject.compareTo(other.subject);
            case 3:
                return -subject.compareTo(other.subject);
            case 4:
                return title.compareTo(other.title);
            case 5:
                return -title.compareTo(other.title);

        }
        return 0;
    }
}
