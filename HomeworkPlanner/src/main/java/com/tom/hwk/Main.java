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
    boolean dualPane;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        dualPane = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                || (getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_LARGE;

        FragmentManager fragmentManager = getFragmentManager();

        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                HomeworkItem h = b.getParcelable("hwk");
                listFragment = new HomeworkListFragment(h);
            } else {
                listFragment = new HomeworkListFragment();
            }

            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.homework_list_content, listFragment);
            ft.commit();
        }else{
            listFragment = (HomeworkListFragment)fragmentManager.findFragmentById(R.id.homework_list_content);
        }

        SharedPreferences prefs = getSharedPreferences("sortPrefs", MODE_PRIVATE);
        HomeworkItem.SORT_NUM = prefs.getInt("order", 0);

        // Sort methods
        items = new String[6];
        items[0] = "Date (Upcoming)";
        items[1] = "Date (Reverse)";
        items[2] = "Subject (a-z)";
        items[3] = "Subject (z-a)";
        items[4] = "Title (a-z)";
        items[5] = "Title (z-a)";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (dualPane) inflater.inflate(R.menu.joint_menu, menu);
        else inflater.inflate(R.menu.optionsmenu, menu);

        return true;
    }

    // Deal with menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reorder:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Reorder Homeworks");
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
            //Can only be called if dual pane
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