package com.azfn.opentalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.azfn.opentalk.R;
import com.azfn.opentalk.base.BaseActivity;
import com.azfn.opentalk.model.LoginUser;
import com.azfn.opentalk.network.HttpRequset;

/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 13:36
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpRequset.ILoadFinish {
    private EditText mEtNickname;
    private EditText mEtPassword;
    private TextView mTvFindPassword;
    private Button mBtnLogin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initDatas();
    }

    @Override
    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.icon_single_avatar);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initViews(){
        mEtNickname = (EditText) findViewById(R.id.et_login_activity_nickname);
        mEtPassword = (EditText) findViewById(R.id.et_login_activity_password);
        mTvFindPassword = (TextView) findViewById(R.id.tv_login_activity_findpassowrd);
        mBtnLogin = (Button) findViewById(R.id.btn_login_activity_login);

        mTvFindPassword.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    private void initDatas(){


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login_activity_findpassowrd:

                break;
            case R.id.btn_login_activity_login:
//                HttpRequset.getInstance().login(mEtNickname.getText().toString(), mEtPassword.getText().toString());
//                Observable.con
                break;
        }
    }


    private void loginSuccess(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void success(Object object) {
        LoginUser loginUser = (LoginUser) object;
        if(loginUser != null) loginSuccess();
    }

    @Override
    public void fail(String errorMsg) {

    }
}
