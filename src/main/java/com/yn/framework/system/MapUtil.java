package com.yn.framework.system;

import android.content.Context;
import android.content.Intent;

import com.yn.framework.remind.ToastUtil;

import java.net.URISyntaxException;

/**
 * Created by youjiannuo on 16/11/24
 */
public class MapUtil {

    public static void startMap(Context context, String name) {

        if (SystemUtil.isPackage("com.autonavi.minimap")) {
            //高德地图
            Intent intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse("androidamap://route?dname=" + name + "&dev=0&m=0&t=1&showType=1"));
            intent.setPackage("com.autonavi.minimap");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
            return;
        } else if (SystemUtil.isPackage("com.baidu.BaiduMap")) {
            //百度地图
            try {
                Intent intent = Intent.getIntent("intent://map/place/search?query=" + name + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                context.startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                return;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        ToastUtil.showNormalMessage("没有找到到第三方的app地图应用");
    }

}
