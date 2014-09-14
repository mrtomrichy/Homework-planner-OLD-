package com.tom.hwk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.utils.HomeworkItem;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.db.HomeworkDatabase;

import java.util.List;

/**
 * Created by Tom on 30/12/2013.
 */
public class HomeworkListAdapter extends ArrayAdapter<HomeworkItem> { // adapter for list

  private Context context;

  public HomeworkListAdapter(Context context, int textViewResourceId, List<HomeworkItem> hwks) {
    super(context, textViewResourceId, hwks);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final HomeworkItem hwk = getItem(position);

    View v = convertView; // inflate the list
    final ViewHolder holder;

    if (v == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(R.layout.list, null);
      holder = new ViewHolder();
      holder.title = (TextView) v.findViewById(R.id.listTitle);
      holder.subject = (TextView) v.findViewById(R.id.listSubject);
      holder.dueDate = (TextView) v.findViewById(R.id.listDueDate);
      holder.listLate = (TextView) v.findViewById(R.id.listLate);
      holder.complete = (CheckBox) v.findViewById(R.id.homeworkComplete);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    int daysUntil = hwk.daysUntilDue();

    holder.title.setText(hwk.title);
    holder.subject.setText(hwk.subject);
    String days = (daysUntil == 1 || daysUntil == -1) ? "(" + daysUntil + " day)" : "(" + daysUntil + " days)";
    String date = hwk.day + "/"
        + (hwk.month + 1) + "/"
        + hwk.year + " " + days;


    holder.complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        hwk.complete = b;
        DatabaseAccessor db = DatabaseAccessor.getDBAccessor(context);
        db.updateHomeworkStatus(hwk);
        setStatusText(holder, hwk);
      }
    });
    holder.complete.setChecked(hwk.complete);
    if (holder.dueDate != null) holder.dueDate.setText(date);

    setStatusText(holder, hwk);

    holder.color = (ImageView) v.findViewById(R.id.color_picker);
    holder.color.setBackgroundColor(hwk.color);
    return v;

  }

  public void setStatusText(ViewHolder holder, HomeworkItem hwk) {
    if (hwk.isComplete()) {
      holder.listLate.setTextColor(Color.rgb(104, 220, 50));
      holder.listLate.setText("Complete");
    } else {
      if (hwk.isToday()) {
        holder.listLate.setTextColor(Color.rgb(255, 165, 0));
        holder.listLate.setText("Due Today!");
      } else if (hwk.isLate()) {
        holder.listLate.setTextColor(Color.RED);
        holder.listLate.setText("Late");
      } else {
        holder.listLate.setTextColor(Color.rgb(104, 220, 50));
        holder.listLate.setText("Ongoing");
      }
    }
  }

  protected class ViewHolder {
    protected TextView title, subject, listLate, dueDate;
    protected CheckBox complete;
    protected ImageView color;
  }

}
