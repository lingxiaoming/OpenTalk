package com.azfn.opentalk.tools;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 10:16
 * 描述一下这个类吧
 */

import android.util.Log;

/**
 * Created by apple on 16/6/9.
 */
public class LogUtils {

    public static void i(String tag, String msg){
        Log.i(tag, msg);
    }

    public static void d(String tag, String msg){
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg){
        Log.e(tag, msg);
    }
}
