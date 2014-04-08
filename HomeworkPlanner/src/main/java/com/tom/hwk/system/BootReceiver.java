package com.tom.hwk.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // RE-SET ALL ALARMS FROM THE DATABASE WHEN PHONE IS TURNED ON
        HomeworkDatabase db = new HomeworkDatabase(context);
        AlarmHelper ma = new AlarmHelper();
        db.open();
        ArrayList<HomeworkItem> hwks = db.getHomeworks();
        // repopulate an alarm receiver

        for (HomeworkItem hwk : hwks) {
            ArrayList<HomeworkAlarm> alarms = db.getAlarmsById(hwk.id);
            ma.createAlarm(hwk, alarms, context);
        }
        db.close();
    }
}