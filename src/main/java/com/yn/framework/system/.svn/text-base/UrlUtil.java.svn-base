package com.yn.framework.system;

import com.yn.framework.data.UserSharePreferences;
import com.yn.framework.http.HttpExecute;

/**
 * Created by youjiannuo on 16/7/15
 */
public class UrlUtil {


    public static String getUrl(HttpExecute.NetworkTask task ) {
        return BuildConfig.getHost(task.hostIndex) + task.url;
    }

    public static String getRedirectUrl(String url) {
        String token = UserSharePreferences.getToken();
        if (url.contains("token=")) {
            return url;
        }
        if (url.contains("?")) {
            url += "&token=" + token + "&version=" + BuildConfig.VERSION_NAME;
        } else {
            url += "?token=" + token + "&version=" + BuildConfig.VERSION_NAME;
        }
        return url;
    }

}
