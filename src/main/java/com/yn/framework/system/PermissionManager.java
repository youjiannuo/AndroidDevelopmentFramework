package com.yn.framework.system;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.yn.framework.activity.YNCommonActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by youjiannuo on 2019/5/12.
 * Email by 382034324@qq.com
 * 权限管理
 */
public class PermissionManager {


    private static PermissionManager mManager;

    public static PermissionManager getInstance() {
        if (mManager == null) {
            mManager = new PermissionManager();
        }
        return mManager;
    }

    public boolean requestPermission(YNCommonActivity activity, String... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!isHavePermission(activity, permission)) {
                permissionList.add(permission);
            }
        }
        if (permissionList.size() == 0) {
            return true;
        }
        String[] permissionArray = new String[permissionList.size()];
        permissionList.toArray(permissionArray);
        ActivityCompat.requestPermissions(activity, permissionArray, 0x1);
        return false;
    }


    public boolean isHavePermission(Context activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

    }


}
