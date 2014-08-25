package com.tom.hwk.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tom.hwk.R;
import com.tom.hwk.adapters.AlarmAdapter;
import com.tom.hwk.db.DatabaseAccessor;
import com.tom.hwk.db.HomeworkDatabase;
import com.tom.hwk.utils.HomeworkAlarm;
import com.tom.hwk.utils.HomeworkItem;
import com.tom.hwk.utils.colorpicker.ColorPickerDialog;
import com.tom.hwk.utils.colorpicker.ColorPickerSwatch;
import com.tom.hwk.utils.colorpicker.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class EditActivity extends Activity {

  private TextView editTitle, editSubject, editNotes, editDate;
  private HomeworkItem working_homework;
  private AlarmAdapter adapter;
  private ArrayList<HomeworkAlarm> alarms;
  private ImageButton color_button;

  private int currentDay, currentMonth, currentYear;
  private String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May",
      "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

  DatabaseAccessor db;

  private boolean edit;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.homework_input_screen);

    db = new DatabaseAccessor(this);
    Calendar c = Calendar.getInstance();
    currentDay = c.get(Calendar.DAY_OF_MONTH);
    currentMonth = c.get(Calendar.MONTH);
    currentYear = c.get(Calendar.YEAR);
    c.set(currentYear, currentMonth, currentDay, 0, 0);

    Bundle b = getIntent().getExtras();

    if (b != null && b.containsKey(HomeworkItem.ID_TAG)) {
      working_homework = db.getHomeworkWithId(b.getInt(HomeworkItem.ID_TAG));
      alarms = (ArrayList<HomeworkAlarm>) working_homework.alarms.clone();
      edit = true;
    } else {
      edit = false;
      alarms = new ArrayList<HomeworkAlarm>();

      Calendar init = Calendar.getInstance();
      init.set(currentYear, currentMonth, currentDay);
      init.roll(Calendar.DAY_OF_YEAR, 1);                // Initial date set for tomorrow (by request)

      working_homework = new HomeworkItem(-1, "", "", init.get(Calendar.DAY_OF_MONTH),
          init.get(Calendar.MONTH), init.get(Calendar.YEAR),
          "", Color.parseColor("#33b5e5"), false, alarms);

      alarms.add(new HomeworkAlarm(-1, currentDay, currentMonth, currentYear, 0, 0, working_homework.id));
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

    ListView homework_edit_list = (ListView) findViewById(R.id.homework_edit_list);
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
        Dialog date_dialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int thisYear, int monthOfYear, int dayOfMonth) {
            working_homework.year = thisYear;
            working_homework.month = monthOfYear;
            working_homework.day = dayOfMonth;
            updateDisplay(editDate, working_homework.day, working_homework.month, working_homework.year);
          }
        }, working_homework.year, working_homework.month,
            working_homework.day
        );
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
            Utils.isTablet(EditActivity.this) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

        colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
          @Override
          public void onColorSelected(int color) {
            EditActivity.this.working_homework.color = color;
            color_button.setBackgroundColor(color);
          }
        });
        colorcalendar.show(getFragmentManager(), "colors");
      }
    });


    Button addAlarm = (Button) findViewById(R.id.newAddAlarm);
    addAlarm.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {

        alarms.add(new HomeworkAlarm(-1, currentDay, currentMonth,
            currentYear, 0, 0, working_homework.id));
        adapter.notifyDataSetChanged();
      }
    });

    // Display all existing homework data
    editTitle.setText(working_homework.title);
    editSubject.setText(working_homework.subject);
    editNotes.setText(working_homework.notes);


    color_button.setBackgroundColor(working_homework.color);

    updateDisplay(editDate, working_homework.day, working_homework.month, working_homework.year);
  }

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
        ArrayList<HomeworkAlarm> oldAlarms = (ArrayList<HomeworkAlarm>) working_homework.alarms.clone();
        working_homework.alarms = alarms;


        if (edit) {
          db.updateHomework(working_homework, oldAlarms);
        } else {
          db.saveHomework(working_homework);
        }

      } catch (Exception e) {
        Log.e("SAVING ERROR", e.getMessage());
        e.printStackTrace();
        worked = false;
      } finally {
        if (worked) {
          Toast.makeText(this, "Changes saved!", Toast.LENGTH_LONG).show();
          backToView(true);
        } else {
          Toast.makeText(this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
        }

      }
    } else {

      String errors = "Oops! \nYou've forgotten: ";
      errors += checktitle.equals("") ? "\n -Title" : "";
      errors += checksubject.equals("") ? "\n -Subject" : "";
      errors += checknotes.equals("") ? "\n -Notes" : "";

      Toast.makeText(this, errors, Toast.LENGTH_LONG).show();
    }

  }


  @Override
  public void onBackPressed() {
    if (edit) backToView(true);
    else backToView(false);
  }

  public void backToView(boolean success) {
    Intent i;
    if (edit) i = new Intent(this, ViewActivity.class);
    else i = new Intent(this, ListActivity.class);
    if (success) {
      Bundle b = new Bundle();
      b.putInt(HomeworkItem.ID_TAG, working_homework.id);
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
        editEntry();
        return true;
      case android.R.id.home:
        if (edit) backToView(true);
        else backToView(false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


}
