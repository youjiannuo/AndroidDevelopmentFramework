package com.yn.framework.system;

import com.yn.framework.remind.ToastUtil;

import static com.yn.framework.data.EnvironmentSharePreferences.isTest;

/**
 * Created by youjiannuo on 16/7/15
 */
public class BuildConfig {

    public static String HOSTS[];

    public static String HOST = "";

    public static String HOST1 = "";

    public static boolean ENVIRONMENT;

    public static String APPLICATION_ID;

    public static String VERSION_NAME;

    public static String VERSION_CODE;

    public static String getHost(int index) {
        if (HOSTS == null || HOSTS.length == 0) {
            HOSTS = HOST.split("@@");
        }

        if (index < 0 && index >= HOSTS.length && !ENVIRONMENT) {
            ToastUtil.showNormalMessageHandler("请输入正确的域名index");
            return "";
        }
        //测试环境，切换正式环境
        if (!ENVIRONMENT && !isTest()) {
            return HOST1.split("@@")[index];
        }
        return HOSTS[index];
    }


}
