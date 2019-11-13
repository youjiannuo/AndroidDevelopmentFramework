package com.yn.framework.system;

import android.graphics.Color;

/**
 * Created by youjiannuo on 2018/12/18.
 * Email by 382034324@qq.com
 */
public class ColorUtil {

    public static int parseColor(String s) {
        if (!s.contains("#")) {
            s = "#" + s;
        }
        try {
            return Color.parseColor(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.WHITE;
    }

}
