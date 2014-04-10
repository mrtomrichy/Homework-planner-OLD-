package com.tom.hwk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Information extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.information);

    ActionBar ab = getActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setTitle("Information");

    TextView whatInfoText = (TextView) findViewById(R.id.viewInfoText2);
    TextView reminderInfoText = (TextView) findViewById(R.id.viewInfoText4);
    Button rateButton = (Button) findViewById(R.id.rateButton);

    rateButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.tom.hwk"));
        startActivity(intent);
      }
    });

    whatInfoText.setText(R.string.info_desc);
    reminderInfoText.setText(R.string.info_me);
  }

  public void backToList() {
    Intent i = new Intent(this, Main.class);
    startActivity(i);
    finish();
  }

  @Override
  public void onBackPressed() {
    backToList();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // app icon in action bar clicked; go home
        backToList();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
