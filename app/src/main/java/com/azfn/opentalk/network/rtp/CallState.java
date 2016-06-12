package com.azfn.opentalk.network.rtp;
/**
 * User: xiaoming
 * Date: 2016-06-10
 * Time: 21:42
 * 描述一下这个类吧
 */

/**
 * Created by apple on 16/6/10.
 */
public class CallState {
    private static CallState mCallState;

    private CallState(){}

    public static CallState getInstance(){
        if(mCallState == null){
            mCallState = new CallState();
        }
        return mCallState;
    }

    public static final int UA_STATE_IDLE = 0;
    public static final int UA_STATE_INCOMING_CALL = 1;
    public static final int UA_STATE_OUTGOING_CALL = 2;
    public static final int UA_STATE_INCALL = 3;
    public static final int UA_STATE_HOLD = 4;

    public static int call_state;
}
