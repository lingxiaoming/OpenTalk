package com.azfn.opentalk.tools;
/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 14:56
 * 描述一下这个类吧
 */

import android.content.Context;
import android.widget.Toast;

/**
 * Created by apple on 16/6/4.
 */
public class ToastUtils {
    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
