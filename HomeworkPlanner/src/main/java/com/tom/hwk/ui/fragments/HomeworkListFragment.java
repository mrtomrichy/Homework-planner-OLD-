package com.tom.hwk.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.adapters.HomeworkListAdapter;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.utils.HomeworkItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by Tom on 12/02/2014.
 * This is the fragment which contains the ListView that displays all the homework
 */
public class HomeworkListFragment extends Fragment {

  private HomeworkListAdapter arrayAdapter;
  private List<HomeworkItem> hwks;

  private DatabaseAccessor databaseAccessor;

  private HomeworkItem lastSelected = null;

  private static HomeworkListFragment sharedInstance = null;

  public static HomeworkListFragment getHomeworkListFragment() {
    if (sharedInstance == null) sharedInstance = new HomeworkListFragment();
    return sharedInstance;
  }

  public interface ListAttachedListener {
    public abstract void onListFragmentAttached();

    public abstract void onHomeworkSelected(HomeworkItem hwk);

    public abstract void onHomeworkDeleted(int position, HomeworkItem item);
  }

  public HomeworkListFragment() {

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    databaseAccessor = new DatabaseAccessor(getActivity());
    hwks = databaseAccessor.getAllHomework();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.list_fragment, group, false);

    final EnhancedListView list = (EnhancedListView) v.findViewById(R.id.list); // get the list

    // Create the footer which is shown when there are no homeworks in the list
    TextView footer = (TextView) v.findViewById(R.id.homework_list_footer);
    list.setEmptyView(footer);
    list.setFooterDividersEnabled(true);
    list.setHeaderDividersEnabled(true);

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,
                              int position, long arg3) {
        if (position >= 0) {
          HomeworkItem h = lastSelected = hwks.get(position);
          ((ListAttachedListener) getActivity()).onHomeworkSelected(h);
        }
      }
    });

    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      public boolean onItemLongClick(AdapterView<?> parent, View v,
                                     int position, long arg3) {
        final int p = position;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
                HomeworkItem deleted = hwks.get(p);
                databaseAccessor.deleteHomework(deleted);
                arrayAdapter.notifyDataSetChanged();
                ((ListAttachedListener) getActivity()).onHomeworkDeleted(p, deleted);
                dialog.cancel();
                break;

              case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                dialog.cancel();
                break;
            }
          }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this homework?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();
        return true;
      }
    });

    list.setDismissCallback(new EnhancedListView.OnDismissCallback() {
      @Override
      public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int position) {
        final HomeworkItem deletedItem = hwks.remove(position);

        arrayAdapter.notifyDataSetChanged();
        ((ListAttachedListener) getActivity()).onHomeworkDeleted(position, deletedItem);

        return new EnhancedListView.Undoable() {
          @Override
          public void undo() {
            hwks.add(position, deletedItem);
            arrayAdapter.notifyDataSetChanged();
          }

          @Override
          public void discard() {
            databaseAccessor.deleteHomework(deletedItem);
          }
        };
      }
    });

    list.enableSwipeToDismiss();
    list.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
    list.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
    list.setUndoHideDelay(5000);
    list.addFooterView(new View(getActivity()));

    // Create the Array Adapter
    arrayAdapter = new HomeworkListAdapter(getActivity(), 0, hwks);
    list.setAdapter(arrayAdapter);

    return v;
  }

  @Override
  public void onResume() {
    super.onResume();

    hwks = databaseAccessor.getAllHomework();
    if (lastSelected != null)
      for (HomeworkItem h : hwks)
        if (h.id == lastSelected.id)
          lastSelected = h;
    arrayAdapter.notifyDataSetChanged();

    if (getActivity() instanceof ListAttachedListener)
      ((ListAttachedListener) getActivity()).onListFragmentAttached();
    else
      throw new RuntimeException("Activity must implement ListAttachedListener");
  }

  /* Reorder the homework by the specified order */
  public void reorderHomeworks() {
    Collections.sort(hwks);
    arrayAdapter.notifyDataSetChanged();
  }

  public HomeworkItem getSelectedHomework() {
    if (lastSelected == null && hwks.size() > 0) lastSelected = hwks.get(0);
    return lastSelected;
  }

}
