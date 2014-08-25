package com.tom.hwk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tom.hwk.db.HomeworkDatabase;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // RE-SET ALL ALARMS FROM THE DATABASE WHEN PHONE IS TURNED ON
        HomeworkDatabase db = new HomeworkDatabase(context);
        AlarmUtils ma = new AlarmUtils();
        db.open();
        ArrayList<HomeworkItem> hwks = db.getAllHomeworks();
        // repopulate an alarm receiver

        for (HomeworkItem hwk : hwks) {
            ma.createAlarmsFromList(hwk, hwk.alarms, context);
        }
        db.close();
    }
}