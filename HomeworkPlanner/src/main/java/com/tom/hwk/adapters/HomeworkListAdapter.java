package com.tom.hwk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.models.HomeworkItem;

import java.util.List;

/**
 * Created by Tom on 30/12/2013.
 */
public class HomeworkListAdapter extends RecyclerView.Adapter<HomeworkListAdapter.ViewHolder> {

  private Context mContext;
  private List<HomeworkItem> mHomework;
  private HomeworkClickedListener mListener;


  public interface HomeworkClickedListener {
    void homeworkClick(int postition);
    void homeworkLongClick(int postition);
    void homeworkStatusChanged(int position, boolean status);
  }

  public HomeworkListAdapter(Context context, List<HomeworkItem> hwks, HomeworkClickedListener callbacks) {
    super();
    this.mHomework = hwks;
    this.mContext = context;
    this.mListener = callbacks;

    setHasStableIds(true);
  }

  @Override
  public long getItemId(int position){
    return this.mHomework.get(position).id;
  }

  private void setStatusText(TextView view, HomeworkItem hwk) {
    if (hwk.isComplete()) {
      view.setTextColor(Color.rgb(104, 220, 50));
      view.setText("Complete");
    } else {
      if (hwk.isToday()) {
        view.setTextColor(Color.rgb(255, 165, 0));
        view.setText("Due Today!");
      } else if (hwk.isLate()) {
        view.setTextColor(Color.RED);
        view.setText("Late");
      } else {
        view.setTextColor(Color.rgb(104, 220, 50));
        view.setText("Ongoing");
      }
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.homework_list_view, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    final HomeworkItem hwk = mHomework.get(position);
    int daysUntil = hwk.daysUntilDue();

    holder.title.setText(hwk.title);
    holder.subject.setText(hwk.subject);
    String days = (daysUntil == 1 || daysUntil == -1) ? "(" + daysUntil + " day)" : "(" + daysUntil + " days)";
    String date = hwk.day + "/"
        + (hwk.month + 1) + "/"
        + hwk.year + " " + days;

    holder.complete.setChecked(hwk.complete);
    if (holder.dueDate != null) holder.dueDate.setText(date);

    setStatusText(holder.listLate, hwk);

    holder.color.setBackgroundColor(hwk.color);
  }

  @Override
  public int getItemCount() {
    return this.mHomework.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView title, subject, listLate, dueDate;
    private CheckBox complete;
    private ImageView color;

    public ViewHolder(View itemView) {
      super(itemView);

      this.title = (TextView) itemView.findViewById(R.id.listTitle);
      this.subject = (TextView) itemView.findViewById(R.id.listSubject);
      this.dueDate = (TextView) itemView.findViewById(R.id.listDueDate);
      this.listLate = (TextView) itemView.findViewById(R.id.listLate);
      this.complete = (CheckBox) itemView.findViewById(R.id.homeworkComplete);
      this.color = (ImageView) itemView.findViewById(R.id.color_display);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mListener.homeworkClick(ViewHolder.this.getAdapterPosition());
          notifyDataSetChanged();
        }
      });

      itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
          mListener.homeworkLongClick(ViewHolder.this.getAdapterPosition());
          notifyDataSetChanged();
          return false;
        }
      });

      this.complete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View compoundButton) {
          mListener.homeworkStatusChanged(getAdapterPosition(), ((CompoundButton) compoundButton).isChecked());
          notifyDataSetChanged();
        }
      });
    }
  }

}
