package com.tom.hwk;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tom.hwk.system.HomeworkAlarm;
import com.tom.hwk.system.HomeworkDatabase;
import com.tom.hwk.system.HomeworkItem;
import com.tom.hwk.system.HomeworkListAdapter;

import java.util.ArrayList;
import java.util.Collections;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by Tom on 12/02/2014.
 */
public class HomeworkListFragment extends Fragment {
    private EnhancedListView list;                      // ListView displays homeworks
    private HomeworkDatabase db;                // Database Object
    private HomeworkListAdapter arrayAdapter;   // ListView adapter
    private ArrayList<HomeworkItem> hwks;       // Store all homeworks
    private ActionBar ab;
    boolean dualPane;
    ViewHomeworkFragment view;
    HomeworkItem forward = null;

    public HomeworkListFragment() {
        setRetainInstance(true);
    }

    public HomeworkListFragment(HomeworkItem h) {
        this();
        this.forward = h;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new HomeworkDatabase(getActivity());    // new database object
        db.open();                          // open the db
        hwks = db.getHomeworks();           // get the homeworks;
        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, group, false);

        list = (EnhancedListView) v.findViewById(R.id.list); // get the list

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long arg3) {
                if (position >= 0) {
                    HomeworkItem h = hwks.get(position);
                    if (dualPane) {
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
                                arrayAdapter.deleteHomework(p);
                                updateViewPane();
                                updateActionBar();
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
                final ArrayList<HomeworkAlarm> deletedAlarms;

                deletedAlarms = arrayAdapter.deleteHomework(position);

                updateViewPane();
                updateActionBar();

                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        arrayAdapter.insertDeletedHomework(deletedItem, deletedAlarms, position);
                        updateViewPane();
                        updateActionBar();
                    }
                };
            }
        });

        list.enableSwipeToDismiss();
        list.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
        list.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
        list.setUndoHideDelay(5000);


        View footer = inflater.inflate(R.layout.list_footer,
                null, false);
        TextView footer_text = (TextView) footer.findViewById(R.id.homework_list_footer);
        list.addFooterView(footer, null, false); // To have divider on last item

        arrayAdapter = new HomeworkListAdapter(getActivity(), 0, hwks, footer_text);
        list.setAdapter(arrayAdapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ab == null) ab = getActivity().getActionBar();
        ab.setTitle("Planner");
        updateActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();

        dualPane = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                || (getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_LARGE;

        if (dualPane) {
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
                    view.hideDetails();
                else {
                    f = view.getHomework();
                    if (f == null) f = hwks.get(0);
                    view.updateDetails(f);
                }
            }
        }
    }

    public void reorderHomeworks() {
        Collections.sort(hwks);
        arrayAdapter.notifyDataSetChanged();
    }

    public HomeworkItem getCurrentViewingHomework() {
        return view.getHomework();
    }

    private void updateActionBar(){
        String subtitle = hwks.size() == 1 ? " current homework" : " current homeworks";
        ab.setSubtitle(hwks.size() + subtitle);
    }

    private void updateViewPane(){
        if (dualPane) {
            if(hwks.contains(getCurrentViewingHomework())) return;
            if (hwks.size() == 0) {
                view.hideDetails();
            } else {
                view.updateDetails(hwks.get(0));
            }
        }
    }

}
