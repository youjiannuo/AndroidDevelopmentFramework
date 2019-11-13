package com.yn.framework.data;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 2018/9/1.
 * Email by 382034324@qq.com
 */
public class EnvironmentSharePreferences {

    private final static String FILE = "Environment";

    private final static String KEY = "Environment_Key";

    public static void changeTest(){
        YNSharedPreferences.saveInfo(KEY , "" , FILE);
    }

    public static void changeProduct(){
        YNSharedPreferences.saveInfo(KEY , "1" , FILE);
    }

    public static boolean isTest(){
        return isEmpty(YNSharedPreferences.getInfo(KEY , FILE));
    }

}
