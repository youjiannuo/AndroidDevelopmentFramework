package com.yn.framework.system;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.yn.framework.data.UserSharePreferences;

import java.util.Locale;

/**
 * Created by youjiannuo on 17/1/19
 * 语言管理
 */

public class LanguageManager {

    public final static int CHINESE = 0;
    public final static int ENGLISH = 1;
    public final static int KOREAN = 2;

    private final String KEY = "language";

    private Context mContext;

    public LanguageManager(Context context) {
        mContext = context;
    }

    public void switchLanguage() {
        int language = getLanguage();
        switch (language) {
            case CHINESE:
                switchLanguage(Locale.CHINA);
                break;
            case ENGLISH:
                switchLanguage(Locale.US);
                break;
            case KOREAN:
                switchLanguage(Locale.KOREA);
                break;
        }
    }

    public void switchEnglish() {
        setLanguage(1);
        switchLanguage(Locale.US);
    }

    public void switchKorean() {
        setLanguage(2);
        switchLanguage(Locale.KOREA);
    }

    public void switchChinese() {
        setLanguage(0);
        switchLanguage(Locale.CHINA);
    }

    public void switchLanguage(Locale locale) {
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = (locale);
        resources.updateConfiguration(configuration, dm);
    }

    //获取系统的语言
    public Locale getDefault() {
        return Locale.getDefault();
    }

    public int getLanguage() {
        String language = UserSharePreferences.get(KEY);
        if (!StringUtil.isEmpty(language)) {
            return StringUtil.parseInt(language);
        } else {
            Locale locale = getDefault();
            if (locale.equals(Locale.US)) {
                setLanguage(1);
                return 1;
            } else if (locale.equals(Locale.CHINA)) {
                setLanguage(0);
                return 0;
            } else if (locale.equals(Locale.KOREA)) {
                setLanguage(2);
                return 2;
            }
            setLanguage(1);
            return 1;
        }
    }

    public String getLang() {
        int lang = getLanguage();
        switch (lang) {
            case 1:
                return "en";
            case 2:
                return "kr";
            case 0:
                return "cn";
        }
        return "en";
    }


    public void setLanguage(int language) {
        UserSharePreferences.set(KEY, String.valueOf(language));
    }

}
