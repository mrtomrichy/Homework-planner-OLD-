package com.tom.hwk.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tom.hwk.R;
import com.tom.hwk.models.HomeworkItem;
import com.tom.hwk.ui.fragments.ListHomeworkFragment;
import com.tom.hwk.ui.fragments.ViewHomeworkFragment;

public class ListActivity extends AppCompatActivity implements ListHomeworkFragment.ListAttachedListener {

  private CharSequence reorderOptions[];

  private ListHomeworkFragment listFragment;

  private boolean mTwoPane = false;

  /* Override the onCreate method to set up all initial variables
     and show the fragments */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);

    // How the user wishes to sort the homework
    SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
    HomeworkItem.SORT_NUM = prefs.getInt("order", 0);

    // Sort methods
    reorderOptions = getResources().getStringArray(R.array.sort_by_options);

    listFragment = ((ListHomeworkFragment) getSupportFragmentManager()
        .findFragmentById(R.id.item_list));

    mTwoPane = findViewById(R.id.view_homework_content) != null;

    if(mTwoPane) {
      Bundle b = getIntent().getExtras();
      if (b != null && b.containsKey(HomeworkItem.ID_TAG)) {
        listFragment.setSelectedHomework(b.getInt(HomeworkItem.ID_TAG));
        getIntent().removeExtra(HomeworkItem.ID_TAG);
      }
    }

    FloatingActionButton addNewButton = (FloatingActionButton) findViewById(R.id.add_new_homework_fab);
    addNewButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createNewHomework();
      }
    });
  }

  /* Create the options menu */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.optionsmenu, menu);
    return true;
  }

  /* Show the Dialog which allows the user to select the order
     their homework is displayed in. */
  public void showReorderDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Reorder Homework");
    builder.setCancelable(true);

    builder.setSingleChoiceItems(reorderOptions, HomeworkItem.SORT_NUM, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface i, int selected) {
        HomeworkItem.SORT_NUM = selected;
        SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("order", selected);
        edit.apply();
        listFragment.reorderHomework();
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
      case R.id.settingsScreen:
        intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onHomeworkSelected(HomeworkItem hwk) {
    if (mTwoPane) {
      Bundle arguments = new Bundle();
      arguments.putParcelable(ViewHomeworkFragment.ARG_HOMEWORK_KEY, hwk);
      ViewHomeworkFragment fragment = new ViewHomeworkFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.view_homework_content, fragment)
          .commit();
    } else {
      Intent i = new Intent(this, ViewActivity.class);
      Bundle b = new Bundle();
      b.putInt(HomeworkItem.ID_TAG, hwk.id); // add the homework
      i.putExtras(b);
      startActivity(i);
      finish();
    }
  }

  public void createNewHomework(){
    Intent intent = new Intent(this, EditActivity.class);
    startActivity(intent);
    finish();
  }

  public void onEditPressed(HomeworkItem homeworkItem) {
    Intent i = new Intent(this, EditActivity.class);
    i.putExtra(HomeworkItem.ID_TAG, homeworkItem.id);
    startActivity(i);
    finish();
  }
}