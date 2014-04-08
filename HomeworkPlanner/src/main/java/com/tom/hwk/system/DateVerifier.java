package com.tom.hwk.system;

import java.util.Calendar;

public class DateVerifier {

    public boolean verifyDates(int year, int month, int day) {
        Calendar c = Calendar.getInstance();

        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day);

        return c2.after(c);
    }

}
