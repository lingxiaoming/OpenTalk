package com.azfn.opentalk;
/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 13:31
 * 描述一下这个类吧
 */

import android.app.Application;

import com.azfn.opentalk.network.rtp.CallState;
import com.azfn.opentalk.network.rtp.Codecs.Codecs;
import com.azfn.opentalk.network.rtp.media.JAudioLauncher;
import com.azfn.opentalk.tools.LocationUtils;

/**
 * Created by apple on 16/6/4.
 */
public class TalkApplication extends Application {
    private static TalkApplication talkApplication;
    private LocationUtils locationUtils;
    private JAudioLauncher jAudioLauncher;
    @Override
    public void onCreate() {
        super.onCreate();
        talkApplication = this;

        locationUtils = new LocationUtils(this);
//        locationUtils.startLocation();//开始定位

        jAudioLauncher = new JAudioLauncher(8181,
                "192.168.0.101", 8181, -1, new Codecs(), 0);//-1接收,0全部,1发送
        CallState.call_state = CallState.UA_STATE_OUTGOING_CALL;
//        jAudioLauncher.startMedia();
    }

    public static TalkApplication getInstance(){
        return talkApplication;
    }
}
