package com.tom.hwk.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tom.hwk.R;
import com.tom.hwk.ui.fragments.ViewHomeworkFragment;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.models.HomeworkItem;

/**
 * Created by Tom on 01/03/2014.
 * An Activity which shows a homework's details. If we meet the dual pane
 * criteria, we end the activity and show the details with the list.
 */
public class ViewActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DatabaseAccessor dbAccessor = DatabaseAccessor.getDBAccessor(this);
    HomeworkItem hwk = null;

    Bundle b = getIntent().getExtras();
    if (b != null && b.containsKey(HomeworkItem.ID_TAG)) {
      hwk = dbAccessor.getHomeworkWithId(b.getInt(HomeworkItem.ID_TAG));
    } else {
      backToList(null);
    }

    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
      backToList(hwk);

    // If we get here, everything is fine. Build UI and show the homework.
    setContentView(R.layout.activity_view);

    android.support.v7.app.ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setTitle(hwk.subject);
    String complete = hwk.complete ? "Complete" : "Incomplete";
    ab.setSubtitle(complete);

    if (savedInstanceState == null) {
      Bundle args = new Bundle();
      args.putParcelable(ViewHomeworkFragment.ARG_HOMEWORK_KEY, hwk);
      ViewHomeworkFragment viewHomeworkFragment = new ViewHomeworkFragment();
      viewHomeworkFragment.setArguments(args);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.main_view_hwk, viewHomeworkFragment)
          .commit();
    }
  }

  public void onEditPressed(HomeworkItem homeworkToEdit) {
    Intent i = new Intent(this, EditActivity.class);
    i.putExtra(HomeworkItem.ID_TAG, homeworkToEdit.id);
    startActivity(i);
    finish();
  }

  /* Takes us back to the list view. We can forward the current homework
     to be shown next to the list.
   */
  public void backToList(HomeworkItem homeworkToForward) {
    Intent i = new Intent(this, ListActivity.class);
    if (homeworkToForward != null) {
      Bundle bu = new Bundle();
      bu.putInt(HomeworkItem.ID_TAG, homeworkToForward.id);
      i.putExtras(bu);
    }
    startActivity(i);
    finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // app icon in action bar clicked; go home
        backToList(null);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {
    backToList(null);
  }
}
