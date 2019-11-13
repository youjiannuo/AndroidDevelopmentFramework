package com.yn.framework.review.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yn.framework.R;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.BuildConfig;
import com.yn.framework.system.ContextManager;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.SystemUtil;

import static com.yn.framework.system.StringUtil.isURL;
import static com.yn.framework.system.StringUtil.parseInt;


/**
 * Created by youjiannuo on 16/3/17.
 */
public class ActivityIntent {

    public static void startActivity(int params, Context context, String values[]) {
        if (params == 0) {
            return;
        }
        String[] args = context.getResources().getStringArray(params);
        String className = "";
        String result = "";
        String keys[] = new String[0];
        String fixedValues[] = new String[0];
        int host = 0;
        for (String arg : args) {
            if (arg.contains("class:")) {
                className = get(arg);
            } else if (arg.contains("key:")) {
                String s = get(arg);
                keys = s.split(",");
            } else if (arg.contains("value:")) {
                String s = get(arg);
                fixedValues = s.split(",");
            } else if (arg.contains("result:")) {
                result = get(arg);
            } else if (arg.contains("host:")) {
                host = parseInt(get(arg));
            }
        }

        values = getValue(fixedValues, values);
        if ("web".equals(className)) {
            if (keys.length == 0) {
                keys = new String[]{"KEY_URL"};
            }
            if (!isURL(values[0])) {
                values[0] = BuildConfig.getHost(host) + values[0];
            }
            //打开网页
            className = ContextManager.getString(R.string.yn_web_class);
        } else {
            className = BuildConfig.APPLICATION_ID + className;
        }
        if (((YNCommonActivity) context).startCheckoutActivity(className)) {
            return;
        }
        try {
            Intent intent = new Intent(context, Class.forName(className));
            if (keys.length != values.length) {
                throw new NullPointerException("keys.size() != value.size()");
            }
            StringBuilder logSB = new StringBuilder("\nclass:").append(className).append("\n携带参数:");
            for (int i = 0; i < values.length; i++) {
                logSB.append(keys[i]).append("=").append(values[i]).append("\n");
                intent.putExtra(keys[i], values[i]);
            }
            SystemUtil.printlnInfo(logSB.toString());
            if (StringUtil.isEmpty(result)) {
                context.startActivity(intent);
            } else {
                int i = StringUtil.parseInt(result);
                ((Activity) context).startActivityForResult(intent, i);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            if (!BuildConfig.ENVIRONMENT) {
                ToastUtil.showNormalMessage("跳转Activity，设置的Class有问题");
            }
        }
    }

    private static String[] getValue(String values1[], String values2[]) {
        if (values1 == null && values2 == null) return new String[0];
        else if (values1 == null) return values2;
        else if (values2 == null) return values1;
        String values[] = new String[values2.length + values1.length];
        System.arraycopy(values1, 0, values, 0, values1.length);
        System.arraycopy(values2, 0, values, values1.length, values2.length);
        return values;
    }

    private static String get(String arg) {
        int i = arg.indexOf(":");
        return arg.substring(i + 1);
    }

}
