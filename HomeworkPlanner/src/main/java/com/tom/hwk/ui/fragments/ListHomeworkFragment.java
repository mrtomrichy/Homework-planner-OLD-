package com.tom.hwk.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tom.hwk.R;
import com.tom.hwk.adapters.HomeworkListAdapter;
import com.tom.hwk.models.HomeworkItem;
import com.tom.hwk.utils.DatabaseAccessor;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tom on 12/02/2014.
 * This is the fragment which contains the ListView that displays all the homework
 */
public class ListHomeworkFragment extends Fragment {

  private List<HomeworkItem> mHomework;
  private DatabaseAccessor mDatabaseAccessor;
  private HomeworkItem mSelected;
  private ListAttachedListener mListener = sDummyListener;

  // Advanced RecyclerView
  private RecyclerView mRecyclerView;
  private HomeworkListAdapter mAdapter;
  private LinearLayoutManager mLayoutManager;

  private static ListAttachedListener sDummyListener = new ListAttachedListener() {
    @Override
    public void onHomeworkSelected(HomeworkItem hwk) {
    }
  };

  public interface ListAttachedListener {
    void onHomeworkSelected(HomeworkItem hwk);
  }

  public ListHomeworkFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDatabaseAccessor = DatabaseAccessor.getDBAccessor(getActivity());
    mHomework = mDatabaseAccessor.getHomework();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_list, group, false);

    mRecyclerView = (RecyclerView) v.findViewById(R.id.list);

    mAdapter = new HomeworkListAdapter(getActivity(), this.mHomework,
        new HomeworkListAdapter.HomeworkClickedListener() {
          @Override
          public void homeworkClick(int position) {
            selectHomework(mHomework.get(position));
          }

          @Override
          public void homeworkLongClick(int position) {
            showDeleteDialog(mHomework.get(position));
          }

          @Override
          public void homeworkStatusChanged(int position, boolean status) {
            HomeworkItem homework = mHomework.get(position);
            homework.complete = status;
            mDatabaseAccessor.updateHomework(homework);
          }
        });

    mLayoutManager = new LinearLayoutManager(getActivity());

    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);

    return v;
  }

  @Override
  public void onDestroyView() {
    if (mRecyclerView != null) {
      mRecyclerView.setItemAnimator(null);
      mRecyclerView.setAdapter(null);
      mRecyclerView = null;
    }

    mAdapter = null;
    mLayoutManager = null;

    super.onDestroyView();
  }

  @Override
  public void onResume() {
    super.onResume();
    mHomework = mDatabaseAccessor.getHomework();
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void onAttach(Context activity) {
    super.onAttach(activity);

    // Activities containing this fragment must implement its callbacks.
    if (!(activity instanceof ListAttachedListener)) {
      throw new IllegalStateException("Activity must implement fragment's callbacks.");
    }

    mListener = (ListAttachedListener) activity;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = sDummyListener;
  }

  private void showDeleteDialog(final HomeworkItem homework) {
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            //Yes button clicked
            deleteHomework(homework);
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
        .setNegativeButton("No", dialogClickListener);


    AlertDialog dialog = builder.create();
    dialog.show();

    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
  }

  private void deleteHomework(HomeworkItem homework) {
    mDatabaseAccessor.deleteHomework(homework);
  }

  private void selectHomework(HomeworkItem hwk) {
    mSelected = hwk;
    mListener.onHomeworkSelected(mSelected);
  }

  public void setSelectedHomework(int id) {
    for (HomeworkItem hwk : mHomework) {
      if (hwk.id == id) {
        selectHomework(hwk);
        break;
      }
    }
  }

  /* Reorder the homework by the specified order */
  public void reorderHomework() {
    Collections.sort(mHomework);
    mAdapter.notifyDataSetChanged();
  }

}
