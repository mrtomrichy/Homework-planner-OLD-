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

    private Button rateButton;

    private ActionBar ab;

    private TextView whatInfoText, reminderInfoText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Information");
        whatInfoText = (TextView) findViewById(R.id.viewInfoText2);
        reminderInfoText = (TextView) findViewById(R.id.viewInfoText4);
        rateButton = (Button) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.tom.hwk"));
                startActivity(intent);
            }
        });

        whatInfoText.setText(R.string.info_desc);
        reminderInfoText.setText(R.string.info_me);
    }

    public void onBackPressed() {
        backToList();
    }

    public void backToList() {
        Intent i = new Intent(this, Main.class);
        startActivity(i);
        finish();
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
