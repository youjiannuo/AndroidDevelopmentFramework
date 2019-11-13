package com.yn.framework.review.manager;

import android.view.View;

import com.yn.framework.remind.ToastUtil;
import com.yn.framework.review.OnCheckParams;
import com.yn.framework.review.model.ReplaceModel;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 16/4/26.
 */
public class Util {

    public static OnCheckParams[] getClickTextViews(View view, String mViewIds) {
        if (isEmpty(mViewIds)) {
            return new OnCheckParams[0];
        }
        String ids[] = mViewIds.split(",");
        OnCheckParams textViews[] = new OnCheckParams[ids.length];
        for (int i = 0; i < ids.length; i++) {
            textViews[i] = (OnCheckParams) view.findViewById(YNResourceUtil.getId(ids[i]));
        }
        return textViews;
    }

    static Object[] dealRemind(String remind) {
        if (isEmpty(remind)) {
            return null;
        }
        String reminds[] = remind.split("&");
        Object[] result = new Object[2];
        result[0] = new String[2];
        result[1] = new String[2];
        for (String r : reminds) {
            String arr[] = r.split("=");
            switch (arr[0]) {
                case "button":
                    if (arr.length == 1) {
                        ToastUtil.showFailMessage("设置按钮错误，请检查");
                        break;
                    }
                    result[1] = arr[1].split(",");
                    break;
                case "title":
                    if (arr.length == 1) {
                        ToastUtil.showFailMessage("设置title错误，请检查");
                        break;
                    }
                    ((String[]) result[0])[0] = arr[1];
                    break;
                case "msg":
                    if (arr.length == 1) {
                        ToastUtil.showFailMessage("设置msg错误，请检查");
                        break;
                    }
                    ((String[]) result[0])[1] = arr[1];
                    break;
            }
        }
        return result;
    }

    public static int getInt(String result) {
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {

        }
        return 0;

    }

    public static ReplaceModel[] getCodeReplace(String code) {
        ReplaceModel params[] = new ReplaceModel[0];
        if (!isEmpty(code)) {
            String a[] = code.split(",");
            if (a.length != 2) {
                throw new NullPointerException("app:replace please input right code ,for example old:A,replace:B");
            }
            params = new ReplaceModel[2];
            params[0] = new ReplaceModel(getParam(a[0]), 0);
            params[1] = new ReplaceModel(getParam(a[1]), (a[1].contains("replace all:") ? 1 : 0));
        }

        return params;
    }

    public static String getParam(String param) {
        int a = param.indexOf(":");
        if (a == -1) return param;
        return param.substring(a + 1, param.length());
    }

}
