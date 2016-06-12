package com.azfn.opentalk.ui;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.azfn.opentalk.R;
import com.azfn.opentalk.base.BaseActivity;
import com.azfn.opentalk.model.Group;
import com.azfn.opentalk.network.SocketService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 14:41
 * 主页面
 */

public class MainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private GroupAdapter mGroupAdapter;
    private LinearLayout mContainer;
    private View mHalfTalkOptions;//半屏通话控制栏
    private View mOptionsMore;//更多布局

    private ImageView mIvPtt;
    private ImageView mIvOpen;
    private ImageView mIvMore;
    private ImageView mIvMoreBack;

    private int mFullHeight;//全屏操作栏高度
    private int mHalfHeight;//半屏操作栏高度



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDatas();

//        startService(new Intent(this, GuardService.class));
        startService(new Intent(this, SocketService.class));
    }

    @Override
    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.icon_single_avatar);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mContainer = (LinearLayout) findViewById(R.id.ll_main_activity_container);
        mHalfTalkOptions = getLayoutInflater().inflate(R.layout.layout_talk_options_half, null);
        mIvPtt = (ImageView) mHalfTalkOptions.findViewById(R.id.iv_ptt);
        mIvPtt.setOnTouchListener(this);
        mIvOpen = (ImageView) mHalfTalkOptions.findViewById(R.id.iv_open);
        mIvOpen.setOnClickListener(this);
        mIvMore = (ImageView) mHalfTalkOptions.findViewById(R.id.iv_more_open);
        mIvMoreBack = (ImageView) mHalfTalkOptions.findViewById(R.id.iv_more_close);
        mIvMore.setOnClickListener(this);
        mIvMoreBack.setOnClickListener(this);
        mOptionsMore = mHalfTalkOptions.findViewById(R.id.rl_more_options);
    }

    private void initDatas(){
        List<Group> groupList = new ArrayList<>();

        for(int i = 0; i<20; i++) {
            Group group = new Group();
            group.avatar = "";
            group.code = new Random().nextLong();
            group.name = "西湖区队长－张"+i;
            groupList.add(group);
        }

        mGroupAdapter = new GroupAdapter(this, groupList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mGroupAdapter);


        mContainer.addView(mHalfTalkOptions);
        mHalfHeight = mContainer.getLayoutParams().height;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mFullHeight = wm.getDefaultDisplay().getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mIvPtt.setImageResource(R.drawable.img_half_ptt_press);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIvPtt.setImageResource(R.drawable.img_half_ptt_normal);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_open:
                ViewGroup.LayoutParams params = mContainer.getLayoutParams();
//                params.height = mFullHeight;
//                mContainer.setLayoutParams(params);
                if(params.height == mFullHeight){
                    startAnim(mContainer, mFullHeight, mHalfHeight);
                }else if(params.height == mHalfHeight){
                    startAnim(mContainer, params.height, mFullHeight);
                }
                break;

            case R.id.iv_more_open:
                mOptionsMore.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_more_close:
                mOptionsMore.setVisibility(View.GONE);
                break;

        }
    }



    public void startAnim(final View view, int from, int to){

        ValueAnimator objectAnimator = ObjectAnimator.ofInt(from, to);
        objectAnimator.addListener(mAnimationListener);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = (int)animation.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });

        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    private Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
//            isAnimationFinished = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
//            isAnimationFinished = true;
            if(mContainer.getLayoutParams().height == mFullHeight){
                mIvOpen.setImageResource(R.drawable.img_close);
            }else{
                mIvOpen.setImageResource(R.drawable.img_open);
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
//            isAnimationFinished = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
//            isAnimationFinished = true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
