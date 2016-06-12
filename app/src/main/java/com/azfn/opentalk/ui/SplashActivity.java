package com.azfn.opentalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.widget.ImageView;
import android.widget.TextView;

import com.azfn.opentalk.R;
import com.azfn.opentalk.base.BaseActivity;
import com.azfn.opentalk.model.Version;
import com.azfn.opentalk.network.HttpRequset;
import com.azfn.opentalk.tools.WeakHandler;
import com.azfn.opentalk.tools.loadImageView.ImageLoadHelper;

/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 11:36
 * 闪屏页面
 */
public class SplashActivity extends BaseActivity implements HttpRequset.ILoadFinish {
    private final static int MSG_ANIM_START_0 = 0;
    private final static int MSG_ANIM_START_1 = 1;
    private final static int MSG_ANIM_START_2 = 2;
    private final static int MSG_ANIM_START_3 = 3;
    private final static int MSG_ANIM_ENDED = 4;

    private ImageView imAvatar;
    private TextView tvNickname;
    private TextView tvLoadingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        initDatas();
    }

    public void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.icon_single_avatar);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initViews(){
        imAvatar = (ImageView) findViewById(R.id.img_splash_activity_avatar);
        tvNickname = (TextView) findViewById(R.id.tv_splash_activity_name);
        tvLoadingText = (TextView) findViewById(R.id.tv_splash_activity_state);
    }

    private void initDatas(){
        ImageLoadHelper.getImageLoader().loadCircleImage(this, imAvatar, "net_url");
//        tvNickname.setText("");//load from local
        tvLoadingText.setText(R.string.auto_loading);
        weakHandler.sendEmptyMessage(MSG_ANIM_START_0);
        weakHandler.sendEmptyMessageDelayed(MSG_ANIM_ENDED, 1000);

//        HttpRequset.getInstance().checkVersion(AppUtils.getAppVersionCode(this)+"", "", this);
    }

    private WeakHandler weakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_ANIM_START_0:
                    tvLoadingText.setText(getString(R.string.auto_loading));
                    weakHandler.sendEmptyMessageDelayed(MSG_ANIM_START_1, 200);
                    break;
                case MSG_ANIM_START_1:
                    tvLoadingText.setText(getString(R.string.auto_loading)+".");
                    weakHandler.sendEmptyMessageDelayed(MSG_ANIM_START_2, 200);
                    break;
                case MSG_ANIM_START_2:
                    tvLoadingText.setText(getString(R.string.auto_loading)+"..");
                    weakHandler.sendEmptyMessageDelayed(MSG_ANIM_START_3, 200);
                    break;
                case MSG_ANIM_START_3:
                    tvLoadingText.setText(getString(R.string.auto_loading)+"...");
                    weakHandler.sendEmptyMessageDelayed(MSG_ANIM_START_0, 200);
                    break;
                case MSG_ANIM_ENDED:
                    weakHandler.removeCallbacksAndMessages(null);
                    loadingFinish();
                    break;
            }
            return false;
        }
    });


    private void loadingFinish(){
        Intent intent  = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void success(Object object) {
        Version version = (Version) object;
    }

    @Override
    public void fail(String errorMsg) {

    }
}
