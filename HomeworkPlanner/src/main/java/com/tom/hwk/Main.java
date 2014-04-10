package com.tom.hwk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tom.hwk.system.HomeworkItem;

public class Main extends Activity {

  CharSequence items[];
  HomeworkListFragment listFragment;
  static boolean dualPane;

  /* Override the onCreate method to set up all initial variables
     and show the ListFragment. The ListFragment will handle whether
     or not we show the view pane or not. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Get whether we have enough room to show the list
    // and a homework's details at the same time
    dualPane = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
            || (getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK)
            == Configuration.SCREENLAYOUT_SIZE_LARGE;

    // How the user wishes to sort the homework
    SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
    HomeworkItem.SORT_NUM = prefs.getInt("order", 0);

    // Sort methods
    items = getResources().getStringArray(R.array.sort_by_options);

    FragmentManager fragmentManager = getFragmentManager();

    // Some components might be initialised (if we have come from pause)
    if (savedInstanceState == null) {
      Bundle b = getIntent().getExtras();
      if (b != null) {                        // Check to see if we're supposed to display a homework or not
        HomeworkItem h = b.getParcelable("hwk");
        listFragment = new HomeworkListFragment(h);
      } else {
        listFragment = new HomeworkListFragment();
      }

      FragmentTransaction ft = fragmentManager.beginTransaction();
      ft.replace(R.id.homework_list_content, listFragment);
      ft.commit();
    } else {
      listFragment = (HomeworkListFragment) fragmentManager.findFragmentById(R.id.homework_list_content);
    }
  }

  /* Create the options menu */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    if (dualPane) inflater.inflate(R.menu.joint_menu, menu);
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
        edit.commit();
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
    switch (item.getItemId()) {
      case R.id.reorder:
        showReorderDialog();
        return true;
      case R.id.infoScreen:
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);
        finish();
        return true;
      case R.id.addNew:
        Intent newIntent = new Intent(this, EditHomework.class);
        startActivity(newIntent);
        finish();
        return true;

      // Can only be called if dual pane
      case R.id.editButton:
        Intent i = new Intent(this, EditHomework.class);
        i.putExtra("hwk", listFragment.getCurrentViewingHomework());
        startActivity(i);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}