package com.tom.hwk;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tom.hwk.system.HomeworkAlarm;
import com.tom.hwk.system.HomeworkDatabase;
import com.tom.hwk.system.HomeworkItem;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHomeworkFragment extends Fragment {

  private ArrayList<HomeworkAlarm> alarms;

  private final String[] monthNames = new String[]
          {"Jan", "Feb", "Mar", "Apr", "May",
                  "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
  private final String[] dayNames = new String[]
          {"Sunday", "Monday", "Tuesday", "Wednesday",
                  "Thursday", "Friday", "Saturday"};
  private HomeworkDatabase db;

  TextView viewTitle;
  TextView viewDate;
  TextView viewNotes;
  TextView viewReminder;
  ImageView viewColorCode;

  HomeworkItem hwk = null;

  RelativeLayout details, none;

  /* Empty constructor for when we don't have an initial homework to show */
  public ViewHomeworkFragment() {}

  /* Constructor which sets an initial homework to show */
  public ViewHomeworkFragment(HomeworkItem hwk) {
    this.hwk = hwk;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    db = new HomeworkDatabase(getActivity());
    if ((getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK)
            == Configuration.SCREENLAYOUT_SIZE_LARGE) setRetainInstance(true);
    else setRetainInstance(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.viewhomework, group, false);

    details = (RelativeLayout) v.findViewById(R.id.view_homework_details);
    none = (RelativeLayout) v.findViewById(R.id.view_homework_hidden);

    viewTitle = (TextView) v.findViewById(R.id.viewTitle);
    viewDate = (TextView) v.findViewById(R.id.viewDate);
    viewNotes = (TextView) v.findViewById(R.id.viewNotes);
    viewReminder = (TextView) v.findViewById(R.id.viewReminderAmount);
    viewColorCode = (ImageView) v.findViewById(R.id.viewColorCode);

    if (hwk != null) updateDetails(hwk);

    return v;
  }

  /* Returns the homework that is currently being viewed */
  public HomeworkItem getHomework() {
    return hwk;
  }

  /* Updates the view pane with new details */
  public void updateDetails(HomeworkItem hwk) {
    // Can hide details by passing null argument
    if (hwk == null) {
      hideDetails();
      return;
    }
    this.hwk = hwk;
    db.open();
    alarms = db.getAlarmsById(hwk.id);
    db.close();
    viewTitle.setText(hwk.title);
    viewDate.setText(hwk.day + " / " + monthNames[hwk.month] + " / " + hwk.year);
    viewNotes.setText(hwk.notes);
    String alarmText = alarms.size() == 0 ? "None" : (Integer) (alarms.size()) + "";
    viewReminder.setText(alarmText);
    viewColorCode.setBackgroundColor(hwk.color);

    // Create a Dialog which displays all alarms for this homework
    if (alarms.size() > 0) {
      viewReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          CharSequence alarmString[] = new String[alarms.size()];
          Calendar cal = Calendar.getInstance();
          for (HomeworkAlarm a : alarms) {
            cal.set(a.year, a.month, a.day, a.hour, a.minute, 0);
            alarmString[alarms.indexOf(a)] = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
                    + " " + monthNames[a.month] + " " + a.day +
                    ", " + a.year + " at " + a.getTime();
          }

          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setTitle("Alarms");
          builder.setItems(alarmString, null);
          builder.setCancelable(true);
          AlertDialog dialog = builder.create();
          dialog.show();
        }
      });
    }
    // Show the details pane
    details.setVisibility(View.VISIBLE);
    none.setVisibility(View.GONE);
  }

  /* Hides the details pane, and shows a different view which states
     no homework has been selected */
  private void hideDetails() {
    details.setVisibility(View.GONE);
    none.setVisibility(View.VISIBLE);
  }

  /* Takes the currently viewed homework to the edit activity */
  private void edit() {
    Intent i = new Intent(getActivity(), EditHomework.class);
    Bundle b = new Bundle();
    b.putParcelable("hwk", hwk);
    i.putExtras(b);
    startActivity(i);
    getActivity().finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.editButton:
        edit();
        return true;
      case android.R.id.home:
        Intent intent = new Intent(getActivity(), Main.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


}
