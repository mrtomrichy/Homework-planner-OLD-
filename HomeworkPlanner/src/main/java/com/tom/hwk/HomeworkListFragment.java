package com.tom.hwk;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tom.hwk.system.DatabaseAccessor;
import com.tom.hwk.system.HomeworkItem;
import com.tom.hwk.system.HomeworkListAdapter;

import java.util.ArrayList;
import java.util.Collections;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by Tom on 12/02/2014.
 * This is the fragment which contains the ListView that displays all the homework
 * It also manages whether to show the view pane or not depending on screen
 * size and orientation.
 */
public class HomeworkListFragment extends Fragment {

  private HomeworkListAdapter arrayAdapter;
  private ArrayList<HomeworkItem> hwks;
  private ActionBar ab;
  ViewHomeworkFragment view;
  HomeworkItem forward = null;

  DatabaseAccessor db;

  /* An empty constructor to set initial settings */
  public HomeworkListFragment() {
    setRetainInstance(true);
  }

  /* A constructor to allow us to view a certain homework */
  public HomeworkListFragment(HomeworkItem h) {
    this();
    this.forward = h;
  }

  /* On create we initialise variables and get the homework */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    db = new DatabaseAccessor(getActivity());
    hwks = db.getHomework();
  }

  /* On create view, we create the list that holds the homeworks, and add the various
     listeners we need. We also add it's adapter and footer.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.list_fragment, group, false);

    final EnhancedListView list = (EnhancedListView) v.findViewById(R.id.list); // get the list

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,
                              int position, long arg3) {
        if (position >= 0) {
          HomeworkItem h = hwks.get(position);
          if (Main.dualPane) {
            view.updateDetails(h);
          } else {
            Intent i = new Intent(getActivity(), ViewActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("hwk", h); // add the homework
            i.putExtras(b);
            startActivity(i);
            getActivity().finish();
          }
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
                list.delete(p);
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
        final HomeworkItem deletedItem = hwks.get(position);

        arrayAdapter.remove(deletedItem);

        updateViewPane();
        updateActionBar();

        return new EnhancedListView.Undoable() {
          @Override
          public void undo() {
            arrayAdapter.insert(deletedItem, position);
            updateViewPane();
            updateActionBar();
          }

          @Override
          public void discard() {
            db.deleteHomework(deletedItem);
          }
        };
      }
    });

    list.enableSwipeToDismiss();
    list.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
    list.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
    list.setUndoHideDelay(5000);

    // Create the footer which is shown when there are no homeworks in the list
    View footer = inflater.inflate(R.layout.list_footer,
            list, false);
    TextView footer_text = (TextView) footer.findViewById(R.id.homework_list_footer);
    list.addFooterView(footer, null, false);

    // Create the Array Adapter
    arrayAdapter = new HomeworkListAdapter(getActivity(), 0, hwks, footer_text);
    list.setAdapter(arrayAdapter);

    return v;
  }

  /* On Resume we check if we need to show the view pane. If we do then we check if it
     already exists. If it does, we make it show a homework. If it doesn't exist, we
     create it and give it a homework to show. The forward variable holds the homework
     that the view pane should show.
   */
  @Override
  public void onResume() {
    super.onResume();

    ab = getActivity().getActionBar();
    ab.setTitle("Planner");
    updateActionBar();

    if (Main.dualPane) {
      view = null;
      try {
        view = (ViewHomeworkFragment) getActivity().getFragmentManager().findFragmentById(R.id.view_homework_content);
      } catch (Exception e) {
      }
      if (view == null) {
        if (hwks.size() > 0) {
          if (forward == null) forward = hwks.get(0);
          view = new ViewHomeworkFragment(forward);
        } else {
          view = new ViewHomeworkFragment();
        }
        getActivity().getFragmentManager().beginTransaction().replace(R.id.view_homework_content, view).commit();
      } else {
        HomeworkItem f;
        if (hwks.size() == 0 && view.getHomework() == null)
          view.updateDetails(null);
        else {
          f = view.getHomework();
          if (f == null) f = hwks.get(0);
          view.updateDetails(f);
        }
      }
    }
  }

  /* Reorder the homework by the specified order */
  public void reorderHomeworks() {
    Collections.sort(hwks);
    arrayAdapter.notifyDataSetChanged();
  }

  /* Gets the homework currently being viewed on the view pane */
  public HomeworkItem getCurrentViewingHomework() {
    return view.getHomework();
  }

  /* Updates the action bar to show the amount of current Homework items */
  public void updateActionBar() {
    String subtitle = hwks.size() == 1 ? " current homework" : " current homeworks";
    ab.setSubtitle(hwks.size() + subtitle);
  }

  /* Updates the Viewing Pane either with the first homework in the list, or
     the homework that they are viewing at the moment. Does nothing if no view pane. */
  private void updateViewPane() {
    if (Main.dualPane) {
      if (hwks.contains(getCurrentViewingHomework())) return;
      if (hwks.size() == 0) {
        view.updateDetails(null);
      } else {
        view.updateDetails(hwks.get(0));
      }
    }
  }

}
