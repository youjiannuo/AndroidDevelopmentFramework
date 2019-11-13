package com.yn.framework.feedmission;


/**
 * Created by vincent.shi on 2016/12/9.
 */


import android.content.Context;
import android.os.Binder;
import android.os.Build;

import com.yn.framework.system.ContextManager;
import com.yn.framework.system.SystemUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by vincent.shi on 2016/12/12.
 */

public class PermissionsChecker {
    Context mContext;

    public PermissionsChecker(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 0 就代表有权限，
     * 1代表没有权限，
     * -1函数出错啦
     * op值可以通过AppOpsUtils 查找
     **/
    public static int checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService("appops");
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                int a = (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
                return a;
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    //检查是否获取定位权限
    public static boolean isCheckLocation() {
        return check(AppOpsUtils.OP_FINE_LOCATION) && check(AppOpsUtils.OP_GPS);
    }

    public static boolean isCheckCallPhone() {
        return check(AppOpsUtils.OP_CALL_PHONE);
    }

    public static boolean isCheckReadContact() {
        if (Build.VERSION.SDK_INT >= 19) {
            return check(AppOpsUtils.OP_READ_CONTACTS);
        } else {
            return SystemUtil.isReadContactPermission();
        }
    }

    public static boolean isCheckWritContact() {
        return check(AppOpsUtils.OP_WRITE_CONTACTS);
    }

    public static boolean check(int id) {
        int value = checkOp(ContextManager.getContext(), id);
        return value == 0 || value == 4;
    }
}
