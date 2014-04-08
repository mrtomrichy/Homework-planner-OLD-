package com.tom.hwk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tom.hwk.system.HomeworkItem;

/**
 * Created by Tom on 01/03/2014.
 */
public class ViewActivity extends Activity {
    HomeworkItem hwk;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        HomeworkItem hwk = b.getParcelable("hwk");
        this.hwk = hwk;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                || (getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            backToList(true);
        }
        setContentView(R.layout.main_view_hwk);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Your " + hwk.subject + " Homework");
        String complete = hwk.complete ? "Complete" : "Incomplete";
        ab.setSubtitle(complete);

        ViewHomeworkFragment view = new ViewHomeworkFragment(hwk);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.main_view_hwk, view).commit();
    }

    public void backToList(boolean forwardHomework) {
        Intent i = new Intent(this, Main.class);
        if (forwardHomework) {
            Bundle bu = new Bundle();
            bu.putParcelable("hwk", hwk);
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

    // Deal with menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editButton:
                Intent i = new Intent(this, EditHomework.class);
                i.putExtra("hwk", hwk);
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
}
