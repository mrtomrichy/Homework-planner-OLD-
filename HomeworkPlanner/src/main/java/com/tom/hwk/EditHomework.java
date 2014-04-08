package com.tom.hwk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tom.hwk.system.AlarmHelper;
import com.tom.hwk.system.DateVerifier;
import com.tom.hwk.system.HomeworkAlarm;
import com.tom.hwk.system.HomeworkDatabase;
import com.tom.hwk.system.HomeworkItem;
import com.tom.hwk.system.colorpicker.ColorPickerDialog;
import com.tom.hwk.system.colorpicker.ColorPickerSwatch;
import com.tom.hwk.system.colorpicker.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class EditHomework extends Activity {

    private TextView editTitle, editSubject, editNotes, editDate;
    private HomeworkItem working_homework;
    private AlarmAdapter adapter;
    private ListView homework_edit_list;
    private ArrayList<HomeworkAlarm> alarms;
    private ArrayList<HomeworkAlarm> oldAlarms;
    private ImageButton color_button;

    private DateVerifier d;
    private int currentDay, currentMonth, currentYear;
    private String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May",
            "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    boolean edit;

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_input_screen);


        d = new DateVerifier();
        Calendar c = Calendar.getInstance();
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        currentMonth = c.get(Calendar.MONTH);
        currentYear = c.get(Calendar.YEAR);
        c.set(currentYear, currentMonth, currentDay, 0, 0);

        Bundle b = getIntent().getExtras();

        try {
            working_homework = b.getParcelable("hwk");
            HomeworkDatabase db = new HomeworkDatabase(this);
            db.open();
            alarms = db.getAlarmsById(working_homework.id);
            oldAlarms = (ArrayList<HomeworkAlarm>) alarms.clone();
            db.close();
            edit = true;
        } catch (Exception e) {
            edit = false;
            alarms = new ArrayList<HomeworkAlarm>();
            alarms.add(new HomeworkAlarm(currentDay, currentMonth, currentYear, 0, 0));
            Calendar init = Calendar.getInstance();
            init.set(currentYear, currentMonth, currentDay);
            init.roll(Calendar.DAY_OF_YEAR, 1);                // Initial date set for tomorrow (by request)
            working_homework = new HomeworkItem("", "", init.get(Calendar.DAY_OF_MONTH), init.get(Calendar.MONTH), init.get(Calendar.YEAR), "", Color.parseColor("#33b5e5"), false);
        }

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        if (edit) {
            ab.setTitle("Edit Homework");
            ab.setSubtitle("Edit a current homework");
        } else {
            ab.setTitle("New Homework");
            ab.setSubtitle("Add a new homework");
        }

        homework_edit_list = (ListView) findViewById(R.id.homework_edit_list);
        homework_edit_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        View header = getLayoutInflater().inflate(R.layout.homework_input_main,
                null, false);
        View footer = getLayoutInflater().inflate(R.layout.homework_input_alarms,
                null, false);
        homework_edit_list.addHeaderView(header);
        homework_edit_list.addFooterView(footer);
        adapter = new AlarmAdapter(this, alarms); // add adapter
        homework_edit_list.setAdapter(adapter);
        homework_edit_list.setClickable(false);


        editTitle = (TextView) findViewById(R.id.newTitle);
        editSubject = (TextView) findViewById(R.id.newSubject);
        editNotes = (TextView) findViewById(R.id.newNotes);
        editDate = (TextView) findViewById(R.id.viewDate);
        editDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Dialog date_dialog = new DatePickerDialog(EditHomework.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int thisYear, int monthOfYear, int dayOfMonth) {
                        working_homework.year = thisYear;
                        working_homework.month = monthOfYear;
                        working_homework.day = dayOfMonth;
                        updateDisplay(editDate, working_homework.day, working_homework.month, working_homework.year);
                    }
                }, working_homework.year, working_homework.month,
                        working_homework.day);
                date_dialog.show();
            }
        });


        color_button = (ImageButton) findViewById(R.id.color_picker_button);
        final int[] mColor = Utils.ColorUtils.colorChoice(this);
        color_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title,
                        mColor, 0, 5,
                        Utils.isTablet(EditHomework.this) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

                colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        EditHomework.this.working_homework.color = color;
                        color_button.setBackgroundColor(color);
                    }
                });
                colorcalendar.show(getFragmentManager(), "colors");
            }
        });


        Button addAlarm = (Button) findViewById(R.id.newAddAlarm);
        addAlarm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                alarms.add(new HomeworkAlarm(currentDay, currentMonth,
                        currentYear, 0, 0));
                adapter.notifyDataSetChanged();
            }
        });

        // Display all existing homework data
        editTitle.setText(working_homework.title);
        editSubject.setText(working_homework.subject);
        editNotes.setText(working_homework.notes);


        color_button.setBackgroundColor(working_homework.color);

        updateDisplay(editDate, working_homework.day, working_homework.month, working_homework.year);
    }// onCreate

    public void updateDisplay(TextView dateView, int thisDay, int thisMonth,
                              int thisYear) {
        dateView.setText(thisDay + " / " + monthNames[thisMonth] + " / " + ""
                + thisYear);
        Calendar due = Calendar.getInstance();
        due.set(thisYear, thisMonth, thisDay);
        checkReminders(due);
    }

    public void checkReminders(Calendar due) {
        for (HomeworkAlarm thisAlarm : alarms) {
            Calendar alarm_cal = Calendar.getInstance();
            alarm_cal.set(thisAlarm.year, thisAlarm.month, thisAlarm.day);
            if (alarm_cal.after(due)) {
                thisAlarm.day = due.get(Calendar.DAY_OF_MONTH);
                thisAlarm.month = due.get(Calendar.MONTH);
                thisAlarm.year = due.get(Calendar.YEAR);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void editEntry() {
        boolean worked = true;

        String checktitle = editTitle.getText().toString();
        String checksubject = editSubject.getText().toString();
        String checknotes = editNotes.getText().toString();

        if (!checktitle.equals("") && !checksubject.equals("") && !checknotes.equals("")) {
            try {
                working_homework.title = checktitle;
                working_homework.subject = checksubject;
                working_homework.notes = checknotes;

                if (edit) deleteAllAlarms();

                HomeworkDatabase db = new HomeworkDatabase(this);
                db.open();
                if (edit) db.updateEntry(working_homework);
                else working_homework.id = (int) db.addHomeworkToDatabase(working_homework);
                if (edit) db.deleteAlarms(working_homework.id);
                for (HomeworkAlarm alarm : alarms) {
                    alarm.homeworkId = working_homework.id;
                    alarm.id = (int) db.addAlarm(alarm);
                }
                db.close();
                setAlarms(working_homework);
            } catch (Exception e) {
                worked = false;
            } finally {
                if (worked) {
                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_LONG).show();
                    backToView(true);
                } else {
                    Toast.makeText(this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
                    editTitle.setText("");
                    editSubject.setText("");
                    editNotes.setText("");
                }

            }
        } else {
// Fix this!!!
            String errors = "Oops! \nYou've forgotten: ";
            errors += working_homework.title.equals("") ? "\n -Title" : "";
            errors += working_homework.subject.equals("") ? "\n -Subject" : "";
            errors += working_homework.notes.equals("") ? "\n -Notes" : "";
            errors += !d.verifyDates(working_homework.year, working_homework.month, working_homework.day)
                    ? "\n -That date has already been!" : "";

            Toast.makeText(this, errors, Toast.LENGTH_LONG).show();
        }

    }

    public void setAlarms(HomeworkItem hwk) {
        AlarmHelper ma = new AlarmHelper();
        ma.createAlarm(hwk, alarms, getApplicationContext());
    }

    public void deleteAllAlarms() {
        AlarmHelper ma = new AlarmHelper();
        ma.deleteAllAlarms(oldAlarms, getApplicationContext());
    }

    public void deleteClick(View v) {
        int thisPosition = homework_edit_list.getPositionForView(v);
        int thisAlarmPosition = thisPosition - 1;
        alarms.remove(thisAlarmPosition);
        adapter.notifyDataSetChanged();
    }

    public void changeTime(View v) {
        int thisPosition = homework_edit_list.getPositionForView(v);
        final int thisAlarmPosition = thisPosition - 1;

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                alarms.get(thisAlarmPosition).hour = hour;
                alarms.get(thisAlarmPosition).minute = minute;
                adapter.notifyDataSetChanged();
            }
        }, alarms.get(thisAlarmPosition).hour, alarms.get(thisAlarmPosition).minute, true);
        timePicker.show();
    }

    public void changeDate(View v) {
        final int thisPosition = homework_edit_list.getPositionForView(v);
        final int thisAlarmPosition = thisPosition - 1;

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selected_year, int selected_month, int selected_day) {
                alarms.get(thisAlarmPosition).day = selected_day;
                alarms.get(thisAlarmPosition).month = selected_month;
                alarms.get(thisAlarmPosition).year = selected_year;
                adapter.notifyDataSetChanged();
            }
        }, alarms.get(thisAlarmPosition).year, alarms.get(thisAlarmPosition).month, alarms.get(thisAlarmPosition).day);

        Calendar to = Calendar.getInstance();
        to.set(working_homework.year, working_homework.month, working_homework.day);
        datePicker.getDatePicker().setMaxDate(to.getTimeInMillis());
        datePicker.show();
    }

    @Override
    public void onBackPressed() {
        if (edit) backToView(true);
        else backToView(false);
    }

    public void backToView(boolean success) {
        Intent i;
        if (edit) i = new Intent(this, ViewActivity.class);
        else i = new Intent(this, Main.class);
        if (success) {
            Bundle b = new Bundle();
            b.putParcelable("hwk", working_homework);
            i.putExtras(b);
        }
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edithomeworkmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveEditButton:
                // app icon in action bar clicked; go home
                editEntry();
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                if (edit) backToView(true);
                else backToView(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public class AlarmAdapter extends ArrayAdapter { // adapter for list

        public AlarmAdapter(Context context, ArrayList<HomeworkAlarm> list) {
            super(context, 0, list);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            HomeworkAlarm alarm = (HomeworkAlarm) getItem(position);
            View v = convertView; // inflate the list
            TextView date, time;
            ImageButton deleteAlarm;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.alarmlistitem, null);
            }
            date = (TextView) v.findViewById(R.id.dateAlarm);
            time = (TextView) v.findViewById(R.id.timeAlarm);

            deleteAlarm = (ImageButton) v.findViewById(R.id.deleteAlarmButton);
            date.setText(alarm.getDate());
            time.setText(alarm.getTime());
            time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeTime(view);
                }
            });
            date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeDate(view);
                }
            });
            deleteAlarm.setFocusable(false);
            deleteAlarm.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    deleteClick(view);
                }
            });

            return v;
        }
    }
}
