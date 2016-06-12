package com.azfn.opentalk.base;
/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 12:32
 * activity基类
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by apple on 16/6/4.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    public abstract void initActionBar();
}
