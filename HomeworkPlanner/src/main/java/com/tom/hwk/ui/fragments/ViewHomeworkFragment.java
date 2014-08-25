package com.tom.hwk.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.utils.HomeworkAlarm;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHomeworkFragment extends Fragment {

  public interface ViewHomeworkAttachedListener {
    public abstract void onViewFragmentAttached();
  }

  private ArrayList<HomeworkAlarm> alarms;

  private final String[] monthNames = new String[]
      {"Jan", "Feb", "Mar", "Apr", "May",
          "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
  private final String[] dayNames = new String[]
      {"Sun", "Mon", "Tue", "Wed",
          "Thu", "Fri", "Sat"};

  private TextView viewTitle;
  private TextView viewDate;
  private TextView viewNotes;
  private TextView viewReminder;
  private ImageView viewColorCode;

  private HomeworkItem hwk = null;

  private RelativeLayout details, none;

  private static ViewHomeworkFragment sharedInstance = null;

  public static ViewHomeworkFragment getViewHomeworkFragment()
  {
    if(sharedInstance == null)
      sharedInstance = new ViewHomeworkFragment();
    return sharedInstance;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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

    return v;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (getActivity() instanceof ViewHomeworkAttachedListener)
      ((ViewHomeworkAttachedListener) getActivity()).onViewFragmentAttached();
    else
      throw new RuntimeException("Activity must implement ViewHomeworkAttachedListener");
  }

  /* Returns the homework that is currently being viewed */
  public HomeworkItem getHomework() {
    return hwk;
  }

  /* Updates the view pane with new details */
  public void updateDetails(HomeworkItem hwk) {
    this.hwk = hwk;

    // Can hide details by passing null argument
    if (hwk == null) {
      hideDetails();
      return;
    }

    alarms = hwk.alarms;
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


}
