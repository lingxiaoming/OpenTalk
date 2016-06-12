package com.azfn.opentalk.ui;
/**
 * User: xiaoming
 * Date: 2016-06-05
 * Time: 11:43
 * 描述一下这个类吧
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.azfn.opentalk.R;
import com.azfn.opentalk.base.BaseActivity;

/**
 * Created by apple on 16/6/5.
 */
public class PersonalProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
    }

    @Override
    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.icon_single_avatar);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("个人资料");
    }
}
