package org.cowboycoders.ant.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Utilities specific to android
 * @author will
 *
 */
public class AndroidUtils {
  
  private AndroidUtils() {};
  
  /** 
   * @see android.content.pm.PackageManager#getPackageInfo
   * @param context
   * @param packageName
   * @param flag
   * @return
   */
  public static boolean isPackageInstalled(Context context, 
      String packageName, int flag) {
    try{
      context.getPackageManager().
          getPackageInfo(packageName, flag);
      return true;
  } catch( PackageManager.NameNotFoundException e ){
      return false;
  }
  }
  
  public static boolean isAntRadioServiceInstalled(Context context) {
    return isPackageInstalled(context, "com.dsi.ant.service.socket",
        PackageManager.GET_SERVICES);
  }

}
