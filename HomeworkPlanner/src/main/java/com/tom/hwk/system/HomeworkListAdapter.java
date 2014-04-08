package com.tom.hwk.system;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tom on 30/12/2013.
 */
public class HomeworkListAdapter extends ArrayAdapter { // adapter for list
    TextView footer_text;
    private Context context;
    private Calendar today;
    private HomeworkDatabase db;

    public HomeworkListAdapter(Context context, int textViewResourceId, ArrayList<HomeworkItem> hwks, TextView footer_text) {
        super(context, textViewResourceId, hwks);
        this.context = context;
        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        this.footer_text = footer_text;
        db = new HomeworkDatabase(context);
        updateFooter();
    }

    public ArrayList<HomeworkAlarm> deleteHomework(int position){
        HomeworkItem h = (HomeworkItem)getItem(position);
        db.open();
        db.removeEntry(h.id);                                           // remove the homework from the database
        ArrayList<HomeworkAlarm> alarms = db.getAlarmsById(h.id);       // Get all it's alarms
        new AlarmHelper().deleteAllAlarms(alarms, context.getApplicationContext());
        db.deleteAlarms(h.id);
        db.close();

        remove(h);
        notifyDataSetChanged();
        return alarms;
    }

    public void insertDeletedHomework(HomeworkItem deletedItem, ArrayList<HomeworkAlarm> deletedAlarms, int position){
        db.open();
        deletedItem.id = (int) db.addHomeworkToDatabase(deletedItem);
        for(HomeworkAlarm alarm : deletedAlarms)
            db.addAlarm(alarm);
        db.close();

        new AlarmHelper().createAlarm(deletedItem, deletedAlarms, context.getApplicationContext());

        insert(deletedItem, position);
        notifyDataSetChanged();
    }

    public void updateFooter() {
        if (getCount() == 0)
            footer_text.setVisibility(View.VISIBLE);
        else
            footer_text.setVisibility(View.GONE);
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        updateFooter();
    };

    public View getView(int position, View convertView, ViewGroup parent) {
        final HomeworkItem hwk = (HomeworkItem)getItem(position);
        updateFooter();


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
        }else{
            holder = (ViewHolder) v.getTag();
        }




        Calendar time = Calendar.getInstance();
        time.setTime(hwk.dueDate);
        Date d = new Date(today.getTimeInMillis());
        Date d1 = new Date(time.getTimeInMillis() + 1000);

        int daysUntil = (int) ((d1.getTime() - d.getTime()) / (1000 * 60 * 60 * 24));

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
                HomeworkDatabase db = new HomeworkDatabase(getContext());
                db.open();
                db.updateEntry(hwk);
                db.close();
                setStatusText(holder, hwk);
            }
        });
        holder.complete.setChecked(hwk.complete);
        if(holder.dueDate != null) holder.dueDate.setText(date);

        setStatusText(holder, hwk);

        holder.color = (ImageView) v.findViewById(R.id.color_picker);
        holder.color.setBackgroundColor(hwk.color);
        return v;

    }

    public void setStatusText(ViewHolder holder, HomeworkItem hwk){
        if (hwk.isLate()) { // set if they are late or not
            if (hwk.isToday()) {
                holder.listLate.setTextColor(Color.rgb(255, 165, 0));
                holder.listLate.setText("Due Today!");
            } else {
                holder.listLate.setTextColor(Color.RED);
                holder.listLate.setText("Late");
            }
        } else {
            holder.listLate.setTextColor(Color.rgb(104, 220, 50));
            holder.listLate.setText("Ongoing");
        }

        if(hwk.complete){
            holder.listLate.setTextColor(Color.rgb(104, 220, 50));
            holder.listLate.setText("Complete");
        }
    }
    protected class ViewHolder {
        protected TextView title, subject, listLate, dueDate;
        protected CheckBox complete;
        protected ImageView color;
    }

}
