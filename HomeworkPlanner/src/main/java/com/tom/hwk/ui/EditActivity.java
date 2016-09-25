package com.tom.hwk.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tom.hwk.R;
import com.tom.hwk.adapters.AlarmAdapter;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.models.HomeworkAlarm;
import com.tom.hwk.models.HomeworkItem;
import com.tom.hwk.utils.colorpicker.ColorPickerDialog;
import com.tom.hwk.utils.colorpicker.ColorPickerSwatch;
import com.tom.hwk.utils.colorpicker.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditActivity extends AppCompatActivity {

  private TextView mTitleInput, mNotesInput, mDateInput;
  private AutoCompleteTextView mSubjectInput;
  private HomeworkItem mCurrentHomework;
  private AlarmAdapter mAlarmAdapter;
  private ImageButton mColorButton;

  private static final String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May",
      "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

  private DatabaseAccessor db;

  private boolean edit;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);

    db = DatabaseAccessor.getDBAccessor(this);

    Bundle b = getIntent().getExtras();

    // Get the initial data from the bundle - if we're editing or not
    if (b != null && b.containsKey(HomeworkItem.ID_TAG)) {
      mCurrentHomework = db.getHomeworkWithId(b.getInt(HomeworkItem.ID_TAG));
      edit = true;
    } else {
      edit = false;

      Calendar initialHomeworkDate = Calendar.getInstance();
      initialHomeworkDate.roll(Calendar.DAY_OF_YEAR, 1);                // Initial date set for tomorrow (by request)

      List<HomeworkAlarm> newAlarms = new ArrayList<>();


      mCurrentHomework = new HomeworkItem(-1, "", "", initialHomeworkDate.get(Calendar.DAY_OF_MONTH),
          initialHomeworkDate.get(Calendar.MONTH), initialHomeworkDate.get(Calendar.YEAR),
          "", Color.parseColor("#33b5e5"), false, newAlarms);
    }

    // Set the action bar text
    android.support.v7.app.ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    if (edit) {
      ab.setTitle("Edit Homework");
      ab.setSubtitle("Edit a current homework");
    } else {
      ab.setTitle("New Homework");
      ab.setSubtitle("Add a new homework");
    }

    // Use a list for the entire screen due to the fact we need to add
    // alarms to the bottom, and we want the entire screen to scroll
    ListView homeworkEditList = (ListView) findViewById(R.id.homework_edit_list);
    homeworkEditList.setChoiceMode(ListView.CHOICE_MODE_NONE);

    // Ignore warning of null parent as it is for list
    @SuppressLint("InflateParams")
    View header = getLayoutInflater().inflate(R.layout.homework_input_main,
        null, false);
    @SuppressLint("InflateParams")
    View footer = getLayoutInflater().inflate(R.layout.homework_input_alarms,
        null, false);

    homeworkEditList.addHeaderView(header);
    homeworkEditList.addFooterView(footer);
    homeworkEditList.setClickable(false);

    mAlarmAdapter = new AlarmAdapter(this, mCurrentHomework.alarms); // add mAlarmAdapter
    homeworkEditList.setAdapter(mAlarmAdapter);

    // Get all the views for outputting data
    mTitleInput = (TextView) findViewById(R.id.newTitle);
    mSubjectInput = (AutoCompleteTextView) findViewById(R.id.newSubject);
    mSubjectInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, db.getSubjects()));
    mNotesInput = (TextView) findViewById(R.id.newNotes);
    mDateInput = (TextView) findViewById(R.id.viewDate);
    mDateInput.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Dialog date_dialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int thisYear, int monthOfYear, int dayOfMonth) {
            mCurrentHomework.year = thisYear;
            mCurrentHomework.month = monthOfYear;
            mCurrentHomework.day = dayOfMonth;
            updateDateOutput(mCurrentHomework.day, mCurrentHomework.month, mCurrentHomework.year);
          }
        }, mCurrentHomework.year, mCurrentHomework.month,
            mCurrentHomework.day
        );
        date_dialog.show();
      }
    });

    mColorButton = (ImageButton) findViewById(R.id.color_picker_button);
    mColorButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        ColorPickerDialog colorPicker = ColorPickerDialog.newInstance(
            R.string.color_picker_default_title,
            Utils.ColorUtils.colorChoice(EditActivity.this), 0, 5,
            Utils.isTablet(EditActivity.this) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

        colorPicker.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
          @Override
          public void onColorSelected(int color) {
            EditActivity.this.mCurrentHomework.color = color;
            mColorButton.setBackgroundColor(color);
          }
        });
        colorPicker.show(getFragmentManager(), "colors");
      }
    });


    Button addAlarm = (Button) findViewById(R.id.newAddAlarm);
    addAlarm.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calendar today = Calendar.getInstance();
        mCurrentHomework.alarms.add(new HomeworkAlarm(-1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH),
            today.get(Calendar.YEAR), 0, 0, mCurrentHomework.id));
        mAlarmAdapter.notifyDataSetChanged();
      }
    });

    // Display all existing homework data
    mTitleInput.setText(mCurrentHomework.title);
    mSubjectInput.setText(mCurrentHomework.subject);
    mNotesInput.setText(mCurrentHomework.notes);

    mColorButton.setBackgroundColor(mCurrentHomework.color);

    updateDateOutput(mCurrentHomework.day, mCurrentHomework.month, mCurrentHomework.year);
  }

  public void updateDateOutput(int thisDay, int thisMonth,
                               int thisYear) {
    mDateInput.setText(thisDay + " / " + monthNames[thisMonth] + " / " + ""
        + thisYear);
    Calendar due = Calendar.getInstance();
    due.set(thisYear, thisMonth, thisDay);
    checkReminders(due);
  }

  public void checkReminders(Calendar due) {
    for (HomeworkAlarm thisAlarm : mCurrentHomework.alarms) {
      Calendar alarm_cal = Calendar.getInstance();
      alarm_cal.set(thisAlarm.year, thisAlarm.month, thisAlarm.day);
      if (alarm_cal.after(due)) {
        thisAlarm.day = due.get(Calendar.DAY_OF_MONTH);
        thisAlarm.month = due.get(Calendar.MONTH);
        thisAlarm.year = due.get(Calendar.YEAR);
      }
    }
    mAlarmAdapter.notifyDataSetChanged();
  }

  public void editEntry() {
    boolean success = true;

    mCurrentHomework.title = mTitleInput.getText().toString();
    mCurrentHomework.subject = mSubjectInput.getText().toString();
    mCurrentHomework.notes = mNotesInput.getText().toString();

    if (!mCurrentHomework.title.equals("") && !mCurrentHomework.subject.equals("")) {
      try {
        if (edit) {
          db.updateHomework(mCurrentHomework);
        } else {
          db.addHomework(mCurrentHomework);
        }

      } catch (Exception e) {
        Log.e("SAVING ERROR", e.getMessage());
        e.printStackTrace();
        success = false;
      } finally {
        if (success) {
          Toast.makeText(this, "Changes saved!", Toast.LENGTH_LONG).show();
          back(true);
        } else {
          Toast.makeText(this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
        }
      }
    } else {
      Toast.makeText(this, "This homework needs a title and subject", Toast.LENGTH_LONG).show();
    }

  }


  @Override
  public void onBackPressed() {
    back(edit);
  }

  public void back(boolean forwardHomework) {
    Intent i;
    if (edit) i = new Intent(this, ViewActivity.class);
    else i = new Intent(this, ListActivity.class);
    if (forwardHomework) {
      Bundle b = new Bundle();
      b.putInt(HomeworkItem.ID_TAG, mCurrentHomework.id);
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
        back(edit);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


}
