package com.tom.hwk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tom.hwk.R;
import com.tom.hwk.utils.DatabaseAccessor;

import java.util.List;

/**
 * Created by tom on 28/08/2014.
 */
public class PreferencesActivity extends ActionBarActivity {

  DatabaseAccessor dbAccessor;
  ListView subjectList;
  List<String> subjects;
  SubjectAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preferences);

    dbAccessor = DatabaseAccessor.getDBAccessor(this);

    android.support.v7.app.ActionBar ab = getSupportActionBar();
    ab.setElevation(0);
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setTitle("Manage Subjects");
    ab.setSubtitle("Add or delete subjects");

    subjects = dbAccessor.getAllSubjects();

    subjectList = (ListView) findViewById(R.id.preference_listview);
    subjectList.addFooterView(new View(this));
    mAdapter = new SubjectAdapter(this, 0, subjects);
    subjectList.setAdapter(mAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.preferences_menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        backToList();
        return true;
      case R.id.preferences_add_subject:
        createNewSubjectDialog();
        return true;
      default:
        return true;
    }
  }

  private void createNewSubjectDialog() {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    final EditText input = new EditText(this);
    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    dialog.setView(input);
    dialog.setTitle("Add new subject");
    dialog.setPositiveButton("Add", new Dialog.OnClickListener() {
      @Override
      public void onClick(DialogInterface d, int which) {
        String newSubject = input.getText().toString();
        dbAccessor.addSubject(newSubject);
        if (!subjects.contains(newSubject)) subjects.add(newSubject);
        mAdapter.notifyDataSetChanged();
        d.cancel();
      }
    });
    dialog.setNegativeButton("Cancel", new Dialog.OnClickListener() {
      @Override
      public void onClick(DialogInterface d, int which) {
        d.cancel();
      }
    });
    dialog.setCancelable(false);
    dialog.show();
  }

  @Override
  public void onBackPressed(){
    backToList();
  }

  public void backToList(){
    Intent intent = new Intent(this, ListActivity.class);
    startActivity(intent);
    finish();
  }

  private class SubjectAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> subjects;

    public SubjectAdapter(Context c, int resource, List<String> subjects) {
      super(c, resource, subjects);
      this.context = c;
      this.subjects = subjects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      TextView subject;
      ImageView delete;
      if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.subject_row, null);
        subject = (TextView) convertView.findViewById(R.id.preference_subject_name_textview);
        delete = (ImageView) convertView.findViewById(R.id.preference_delete_icon);
        convertView.setTag(new SubjectPrefHolder(subject, delete));
      } else {
        SubjectPrefHolder holder = (SubjectPrefHolder) convertView.getTag();
        subject = holder.subject;
        delete = holder.delete;
      }

      subject.setText(getItem(position));
      delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          dbAccessor.deleteSubject(subjects.remove(position));
          notifyDataSetChanged();
        }
      });

      return convertView;
    }

    private class SubjectPrefHolder {
      public TextView subject;
      public ImageView delete;

      public SubjectPrefHolder(TextView subject, ImageView delete) {
        this.subject = subject;
        this.delete = delete;
      }
    }
  }
}
