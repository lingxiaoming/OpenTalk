package com.azfn.opentalk.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.azfn.opentalk.R;
import com.azfn.opentalk.ui.PersonalProfileActivity;
import com.azfn.opentalk.ui.SettingActivity;

/**
 * User: xiaoming
 * Date: 2016-06-04
 * Time: 16:59
 * 首页用PopupMenu
 */
public class MyActionProvider extends ActionProvider implements View.OnClickListener {
    /** Context wrapper. */
    private ContextWrapper mContextWrapper;

    public MyActionProvider(Context context) {
        super(context);
        mContextWrapper = (ContextWrapper)context;
    }

    @Override
    public View onCreateActionView() {
        // Inflate the action view to be shown on the action bar.
        LayoutInflater layoutInflater = LayoutInflater.from(mContextWrapper);
        View view = layoutInflater.inflate(R.layout.provider_actionbar, null);
        ImageView popupView = (ImageView)view.findViewById(R.id.popup_view);
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopup(v);
                showPopup(v);
            }
        });

        return view;
    }

    private PopupWindow popupWindow;
    private int showYLocation;
    private View avatar,setting,cancel,exit;
    private void initPopup(View v){
        if(popupWindow != null) return;//放在这里初始化是因为放在前面拿不到view的高度

        WindowManager wm = (WindowManager) mContextWrapper.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getWidth();

        View contentView = LayoutInflater.from(mContextWrapper).inflate(
                R.layout.layout_popwindow, null);
        avatar = contentView.findViewById(R.id.include_popup_avatar);
        setting = contentView.findViewById(R.id.tv_popup_setting);
        cancel = contentView.findViewById(R.id.tv_popup_cancel);
        exit = contentView.findViewById(R.id.tv_popup_exit);
        avatar.setOnClickListener(this);
        setting.setOnClickListener(this);
        cancel.setOnClickListener(this);
        exit.setOnClickListener(this);

        popupWindow = new PopupWindow(contentView, wm.getDefaultDisplay().getWidth() * 4/7, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(mContextWrapper.getResources().getDrawable(R.drawable.bg_popupwindow));
        TypedArray actionbarSizeTypedArray = mContextWrapper.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        float h = actionbarSizeTypedArray.getDimension(0, 0);
        // 设置好参数之后再show
        showYLocation = (int)(h - v.getHeight())/2;

    }

    private void showPopup(View view){
        popupWindow.showAsDropDown(view, 0, showYLocation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.include_popup_avatar:
                Intent PersonalIntent = new Intent(mContextWrapper, PersonalProfileActivity.class);
                mContextWrapper.startActivity(PersonalIntent);
                break;
            case R.id.tv_popup_setting:
                Intent settingIntent = new Intent(mContextWrapper, SettingActivity.class);
                mContextWrapper.startActivity(settingIntent);
                break;
            case R.id.tv_popup_cancel:

                break;
            case R.id.tv_popup_exit:

                break;
        }
    }
}
