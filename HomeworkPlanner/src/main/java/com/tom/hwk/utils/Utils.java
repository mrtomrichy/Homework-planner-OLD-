package com.tom.hwk.utils;

import android.content.Context;

/**
 * Created by tom on 07/08/2014.
 */
public class Utils {
  public static String getVersionNumber(Context c){
    try {
      return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
    }catch(Exception e){
      return "";
    }
  }
}
