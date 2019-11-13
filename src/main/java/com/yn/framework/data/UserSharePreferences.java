package com.yn.framework.data;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yn.framework.R;
import com.yn.framework.system.ContextManager;
import com.yn.framework.system.StringUtil;

import java.util.Map;

/**
 * Created by youjiannuo on 16/7/12
 */

public class UserSharePreferences {

    private static String TOKEN_KEY = ContextManager.getString(R.string.yn_save_token_key);
    private static String FILE_INFO = ContextManager.getString(R.string.yn_save_user_info_file);
    private static String USER_NAME = ContextManager.getString(R.string.yn_save_name_key);
    private static String USER_PWD = ContextManager.getString(R.string.yn_save_pwd_key);
    private static String OPEN_ID = ContextManager.getString(R.string.yn_open_id);
    private static String USER_ID = "user_id";

    private static String IS_LOAD = "is_load";
    private static String IS_CHECK = "is_check";

    public static void saveIsCheck(boolean check) {
        YNSharedPreferences.saveInfoBoolean(IS_CHECK, check, FILE_INFO);
    }

    public static boolean getIsCheck() {
        return YNSharedPreferences.getInfoBoolean(IS_CHECK, FILE_INFO);
    }

    //是否已经鄧麗
    public static boolean isLoad() {
        String isLoad = UserSharePreferences.get(IS_LOAD);
        return "true".equals(isLoad);
    }

    public static void setLoad(boolean is) {
        set(IS_LOAD, is + "");
    }

    //存储token
    public static String getToken() {
        return YNSharedPreferences.getInfo(TOKEN_KEY, FILE_INFO);
    }

    public static void saveToken(String token) {
        YNSharedPreferences.saveInfo(TOKEN_KEY, token, FILE_INFO);
    }


    public static void saveIdAndPwd(String id, String pwd, String userId) {
        YNSharedPreferences.saveInfo(USER_NAME, id, FILE_INFO);
        YNSharedPreferences.saveInfo(USER_PWD, pwd, FILE_INFO);
        YNSharedPreferences.saveInfo(USER_ID, userId, FILE_INFO);
    }

    public static void savePwd(String pwd) {
        YNSharedPreferences.saveInfo(USER_PWD, pwd, FILE_INFO);
    }

    public static void saveIdAndPwd(String id) {
        YNSharedPreferences.saveInfo(USER_NAME, id, FILE_INFO);
    }

    public static void saveOpenId(String openId) {
        YNSharedPreferences.saveInfo(OPEN_ID, openId, FILE_INFO);
    }

    public static void set(String key, String value) {
        YNSharedPreferences.saveInfo(key, value, FILE_INFO);
    }

    public static int getInt(String key) {
        return StringUtil.parseInt(get(key));
    }

    public static float getFloat(String key) {
        return StringUtil.parseFloat(get(key));
    }


    public static String get(String key) {
        return YNSharedPreferences.getInfo(key, FILE_INFO);
    }

    public static String getId() {
        return YNSharedPreferences.getInfo(USER_NAME, FILE_INFO);
    }


    public static String getPwd() {
        return YNSharedPreferences.getInfo(USER_PWD, FILE_INFO);
    }

    public static String getOpenId() {
        return YNSharedPreferences.getInfo(OPEN_ID, FILE_INFO);
    }


    public static String getAllDataJSON() {
        SharedPreferences sharedPreferences = YNSharedPreferences.getSharedPreferences(FILE_INFO);
        Map<String, ?> map = sharedPreferences.getAll();
        return new Gson().toJson(map);
    }

    //退出登录
    public static void loginOut() {
        setLoad(false);
        saveToken("");
    }

    //清除缓存
    public static void clear() {
        YNSharedPreferences.clear(FILE_INFO);
    }

}
