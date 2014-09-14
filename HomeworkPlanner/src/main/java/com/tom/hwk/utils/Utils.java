package com.tom.hwk.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by tom on 07/08/2014.
 */
public class Utils {
    public static boolean isDualPane(Context c) {
      int config = c.getResources().getConfiguration().screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        return c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                || (config != Configuration.SCREENLAYOUT_SIZE_NORMAL && config != Configuration.SCREENLAYOUT_SIZE_SMALL);
    }

  public static String getVersionNumber(Context c){
    try {
      return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
    }catch(Exception e){
      return "";
    }
  }
}
