package com.tom.hwk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tom.hwk.R;
import com.tom.hwk.ui.fragments.HomeworkListFragment;
import com.tom.hwk.ui.fragments.ViewHomeworkFragment;
import com.tom.hwk.utils.DatabaseAccessor;
import com.tom.hwk.utils.HomeworkItem;
import com.tom.hwk.utils.Utils;

public class ListActivity extends Activity implements ViewHomeworkFragment.ViewHomeworkAttachedListener, HomeworkListFragment.ListAttachedListener {

  private CharSequence items[];
  private HomeworkListFragment listFragment = null;
  private ViewHomeworkFragment viewFragment = null;

  private DatabaseAccessor dbAccessor;

  /* Override the onCreate method to set up all initial variables
     and show the fragments */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // How the user wishes to sort the homework
    SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
    HomeworkItem.SORT_NUM = prefs.getInt("order", 0);

    dbAccessor = new DatabaseAccessor(this);

    // Sort methods
    items = getResources().getStringArray(R.array.sort_by_options);

    FragmentManager fragmentManager = getFragmentManager();

    listFragment = HomeworkListFragment.getHomeworkListFragment();
    viewFragment = ViewHomeworkFragment.getViewHomeworkFragment();

    fragmentManager.beginTransaction().replace(R.id.homework_list_content, listFragment).commit();

    if (Utils.isDualPane(this)) {
      fragmentManager.beginTransaction().replace(R.id.view_homework_content, viewFragment).commit();
    }

    fragmentManager.executePendingTransactions();
  }

  @Override
  public void onResume(){
    super.onResume();
    if(Utils.isDualPane(this) && viewFragment.getHomework() != null) {
      for (HomeworkItem item : dbAccessor.getAllHomework())
        if (item.id == viewFragment.getHomework().id)
          return;
      viewFragment.updateDetails(null);
    }
  }

  /* Create the options menu */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    if (Utils.isDualPane(this)) inflater.inflate(R.menu.joint_menu, menu);
    else inflater.inflate(R.menu.optionsmenu, menu);

    return true;
  }

  /* Show the Dialog which allows the user to select the order
     their homework is displayed in. */
  public void showReorderDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Reorder Homework");
    builder.setCancelable(true);

    builder.setSingleChoiceItems(items, HomeworkItem.SORT_NUM, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface i, int selected) {
        HomeworkItem.SORT_NUM = selected;
        SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("order", selected);
        edit.apply();
        listFragment.reorderHomeworks();
        i.cancel();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  /* Deal with menu options */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Check which menu item was pressed
    Intent intent;

    switch (item.getItemId()) {
      case R.id.reorder:
        showReorderDialog();
        return true;
      case R.id.infoScreen:
        intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
        finish();
        return true;
      case R.id.addNew:
        intent = new Intent(this, EditActivity.class);
        startActivity(intent);
        finish();
        return true;
      case R.id.settingsScreen:
        intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
        return true;
      case R.id.editButton:
        if (viewFragment.getHomework() == null) return true;
        intent = new Intent(this, EditActivity.class);
        intent.putExtra(HomeworkItem.ID_TAG, viewFragment.getHomework().id);
        startActivity(intent);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onHomeworkSelected(HomeworkItem hwk) {
    if (Utils.isDualPane(this)) {
      viewFragment.updateDetails(hwk);
    } else {
      Intent i = new Intent(this, ViewActivity.class);
      Bundle b = new Bundle();
      b.putInt(HomeworkItem.ID_TAG, hwk.id); // add the homework
      i.putExtras(b);
      startActivity(i);
      finish();
    }
  }

  @Override
  public void onViewFragmentResumed() {
    if (Utils.isDualPane(this))
      if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(HomeworkItem.ID_TAG)) {
        listFragment.setSelectedHomework(dbAccessor.getHomeworkWithId(getIntent().getExtras().getInt(HomeworkItem.ID_TAG)));
        getIntent().removeExtra(HomeworkItem.ID_TAG);
      } else {
        viewFragment.updateDetails(listFragment.getSelectedHomework());
      }
  }
}