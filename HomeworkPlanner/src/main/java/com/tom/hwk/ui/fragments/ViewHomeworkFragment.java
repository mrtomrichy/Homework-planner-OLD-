package com.tom.hwk.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.models.HomeworkAlarm;
import com.tom.hwk.models.HomeworkItem;

import java.util.Calendar;
import java.util.List;

public class ViewHomeworkFragment extends Fragment {

  public static final String ARG_HOMEWORK_KEY = "hwk";

  private List<HomeworkAlarm> alarms;

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

  private ScrollView details;
  private RelativeLayout none;


  public ViewHomeworkFragment(){

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null && getArguments().containsKey(ARG_HOMEWORK_KEY)) {
      // Load the dummy content specified by the fragment
      // arguments. In a real-world scenario, use a Loader
      // to load content from a content provider.
      hwk = getArguments().getParcelable(ARG_HOMEWORK_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_view, group, false);

    details = (ScrollView) v.findViewById(R.id.view_homework_details);
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

    updateDetails(hwk);
  }

  @Override
  public void onDetach() {
    super.onDetach();
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
