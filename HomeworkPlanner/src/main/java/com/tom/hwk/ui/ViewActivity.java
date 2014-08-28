package com.tom.hwk.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tom.hwk.R;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.ui.fragments.ViewHomeworkFragment;
import com.tom.hwk.utils.HomeworkItem;
import com.tom.hwk.utils.Utils;

/**
 * Created by Tom on 01/03/2014.
 * An Activity which shows a homework's details. If we meet the dual pane
 * criteria, we end the activity and show the details with the list.
 */
public class ViewActivity extends Activity implements ViewHomeworkFragment.ViewHomeworkAttachedListener {
  private HomeworkItem hwk;
  private ViewHomeworkFragment viewHomeworkFragment;

  /* Checks if we meet dual pane criteria, if we do then we end and show it with list
     else we build the UI and show the homework.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DatabaseAccessor dbAccessor = new DatabaseAccessor(this);

    try {
      Bundle b = getIntent().getExtras();
      this.hwk = dbAccessor.getHomeworkWithId(b.getInt(HomeworkItem.ID_TAG));
    } catch (NullPointerException ex) {
      backToList(false);
    }

    if (Utils.isDualPane(this))
      backToList(true);


    // If we get here, everything is fine. Build UI and show the homework.
    setContentView(R.layout.main_view_hwk);

    ActionBar ab = getActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setTitle(hwk.subject);
    String complete = hwk.complete ? "Complete" : "Incomplete";
    ab.setSubtitle(complete);

    if(viewHomeworkFragment == null)
      viewHomeworkFragment = new ViewHomeworkFragment();
    FragmentManager fm = getFragmentManager();
    fm.beginTransaction().replace(R.id.main_view_hwk, viewHomeworkFragment).commit();
  }

  /* Takes us back to the list view. We can forward the current homework
     to be shown next to the list.
   */
  public void backToList(boolean forwardHomework) {
    Intent i = new Intent(this, ListActivity.class);
    if (forwardHomework) {
      Bundle bu = new Bundle();
      bu.putInt(HomeworkItem.ID_TAG, hwk.id);
      i.putExtras(bu);
    }
    startActivity(i);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.viewhomeworkmenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.editButton:
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra(HomeworkItem.ID_TAG, hwk.id);
        startActivity(i);
        finish();
        return true;
      case android.R.id.home:
        // app icon in action bar clicked; go home
        backToList(false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {
    backToList(false);
  }

  @Override
  public void onViewFragmentAttached() {
    viewHomeworkFragment.updateDetails(hwk);
  }
}
