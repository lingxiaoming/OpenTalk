package com.azfn.opentalk.network;
/**
 * User: xiaoming
 * Date: 2016-06-09
 * Time: 08:46
 * 后台运行的socket服务类
 */

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.azfn.opentalk.tools.LogUtils;
import com.azfn.opentalk.tools.PrefsUtils;

import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by apple on 16/6/9.
 */
public class SocketService extends Service {
    private final String TAG = "SocketService";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private final IBinder mBinder = new SosWebSocketClientBinder();
    private PrefsUtils mPrefsUtils;
    private NotificationManager mNm;

    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        mPrefsUtils = PrefsUtils.getInstance(this);
        mNm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        initWebSocket();
        super.onCreate();
        LogUtils.d(TAG, "SocketService Create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        if(mTimer == null)
            mTimer = new Timer();
        mTimer.schedule(new MyTask(), 0, 5000);
        return START_STICKY;
    }

    class MyTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("I am a timer!");
//            mConnection.sendTextMessage("");
        }

    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        if (mConnection.isConnected()) {
            mConnection.disconnect();
        }
        super.onDestroy();
        LogUtils.d(TAG, "SocketService Destroy");
    }

    private void initWebSocket(){
        //注意连接和服务名称要一致
        final String wsuri = "ws://192.168.0.2：8080";
        LogUtils.d(TAG, "connect to "+wsuri);
        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    LogUtils.i(TAG, "WebSocket open");

                }

                @Override
                public void onTextMessage(String text) {
                    LogUtils.i(TAG, "onTextMessage:"+text);
                }

                @Override
                public void onClose(int code, String reason) {
                    LogUtils.i(TAG, "Connection lost:"+reason);/*alert("Connection lost.");*/

                }
            });
        } catch (WebSocketException e) {
            LogUtils.d(TAG, e.toString());
        }
    }




    public class SosWebSocketClientBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }

        public void sendXxx(String addr){
            if(mConnection.isConnected())
                mConnection.sendTextMessage("xxx");

        }
    }
}
