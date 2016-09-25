package com.tom.hwk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tom.hwk.models.HomeworkItem;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // RE-SET ALL ALARMS FROM THE DATABASE WHEN PHONE IS TURNED ON
        DatabaseAccessor db = DatabaseAccessor.getDBAccessor(context);
        AlarmUtils ma = new AlarmUtils();
        List<HomeworkItem> hwks = db.getHomework();
        // repopulate an alarm receiver

        for (HomeworkItem hwk : hwks) {
            ma.createAlarmsFromList(hwk.alarms, context);
        }
    }
}