package com.azfn.opentalk.tools;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 23:07
 * 描述一下这个类吧
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by apple on 16/6/9.
 */
public class GuardService extends Service {
    private static final String TAG = "GuardService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        return START_STICKY;
    }
}
