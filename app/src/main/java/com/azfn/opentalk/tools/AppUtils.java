package com.azfn.opentalk.tools;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 16:17
 * 描述一下这个类吧
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by apple on 16/6/9.
 */
public class AppUtils {
    /**
     * 获取APP版本名称
     *
     * @param context
     *
     * @return
     *
     * @since V1.0
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取APP版本Code
     *
     * @param context
     *
     * @return
     *
     * @since V1.0
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }
}
