package com.tom.hwk.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tom.hwk.R;
import com.tom.hwk.models.HomeworkAlarm;

import java.util.List;

/**
 * Created by tom on 25/08/2014.
 */
public class AlarmAdapter extends ArrayAdapter<HomeworkAlarm> { // adapter for list
  private Context mContext;

  public AlarmAdapter(Context context, List<HomeworkAlarm> list) {
    super(context, 0, list);
    this.mContext = context;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    final HomeworkAlarm alarm = getItem(position);
    TextView date, time;
    ImageButton deleteAlarm;

    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.alarm_list_cell, null);
      date = (TextView) convertView.findViewById(R.id.dateAlarm);
      time = (TextView) convertView.findViewById(R.id.timeAlarm);
      deleteAlarm = (ImageButton) convertView.findViewById(R.id.deleteAlarmButton);

      convertView.setTag(new AlarmViewHolder(date, time, deleteAlarm));
    } else {
      AlarmViewHolder holder = (AlarmViewHolder) convertView.getTag();
      date = holder.date;
      time = holder.time;
      deleteAlarm = holder.deleteAlarm;
    }

    date.setText(alarm.getDate());
    time.setText(alarm.getTime());
    time.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        changeTime(alarm);
      }
    });
    date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        changeDate(alarm);
      }
    });
    deleteAlarm.setFocusable(false);
    deleteAlarm.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        deleteClick(alarm);
      }
    });

    return convertView;
  }

  public void changeDate(final HomeworkAlarm alarm) {

    DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker datePicker, int selected_year, int selected_month, int selected_day) {
        alarm.day = selected_day;
        alarm.month = selected_month;
        alarm.year = selected_year;
        notifyDataSetChanged();
      }
    }, alarm.year, alarm.month, alarm.day);

    datePicker.show();
  }

  public void deleteClick(HomeworkAlarm alarm) {
    remove(alarm);
    notifyDataSetChanged();
  }

  public void changeTime(final HomeworkAlarm alarm) {
    TimePickerDialog timePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        alarm.hour = hour;
        alarm.minute = minute;
        notifyDataSetChanged();
      }
    }, alarm.hour, alarm.minute, true);
    timePicker.show();
  }

  private class AlarmViewHolder {
    public TextView date;
    public TextView time;
    public ImageButton deleteAlarm;

    public AlarmViewHolder(TextView date, TextView time, ImageButton deleteAlarm) {
      this.date = date;
      this.time = time;
      this.deleteAlarm = deleteAlarm;
    }
  }
}