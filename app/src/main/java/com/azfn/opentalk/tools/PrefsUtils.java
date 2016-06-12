package com.azfn.opentalk.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 作者 : lxm on 2015/12/28
 * 描述 : sharedprefrence操作封装
 */
public class PrefsUtils {
    /**
     * 系统配置的配置文件
     */
    public final static String PREFERENCE_FILE_STRING = "open_talk";

    public final static String UID = "uid";
    public final static String NICKNAME = "nickname";
    public final static String AVATAR = "avatar";
    public final static String TOKEN = "token";

    private static PrefsUtils mPrefsUtils;
    private SharedPreferences preference;

    public static PrefsUtils getInstance(Context context) {
        if (null == mPrefsUtils) {
            mPrefsUtils = new PrefsUtils(context);
        }
        return mPrefsUtils;
    }

    private PrefsUtils(Context context) {
        preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
    }


    public void saveAvatar(String avatar) {
        Editor edit = preference.edit();
        edit.putString(AVATAR, avatar);
        edit.commit();
    }

    public String getAvatar() {
        return preference.getString(AVATAR, "");
    }

    public void saveNickname(String nickname) {
        Editor edit = preference.edit();
        edit.putString(NICKNAME, nickname);
        edit.commit();
    }

    public String getNickname() {
        return preference.getString(NICKNAME, "");
    }

    public void saveToken(String token) {
        Editor edit = preference.edit();
        edit.putString(TOKEN, token);
        edit.commit();
    }

    public String getToken() {
        return preference.getString(TOKEN, "");
    }

    public void saveStringByKey(String key, String value){
        Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getStringByKey(String key){
        return preference.getString(key, "");
    }

}
