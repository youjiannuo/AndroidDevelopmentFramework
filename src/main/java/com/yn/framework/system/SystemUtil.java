package com.yn.framework.system;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yn.framework.feedmission.PermissionsChecker;
import com.yn.framework.remind.ToastUtil;

import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.CALL_PHONE;
import static com.yn.framework.system.ContextManager.getContext;
import static com.yn.framework.system.StringUtil.isEmpty;


public class SystemUtil {

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String deviceId = telephonyManager.getDeviceId();
        return isEmpty(deviceId) ? "1" : deviceId;
    }

    public static boolean isPhone(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }

    public static String getProjectVersion() {
        PackageManager manager;
        PackageInfo info;
        manager = getContext().getPackageManager();
        try {
            info = manager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return info.versionName;
    }


    /**
     * 检查SDCard是否存在
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isStartGPS() {
        LocationManager locationManager = (LocationManager) getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        return (locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }


    public static void startGPS() {
        ToastUtil.showNormalMessage("请选择正确的定位模式");
        // 转到手机设置界面，用户设置GPS
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent); // 设置完成后返回到原来的界面
    }

    public static void backLauncher(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);
    }

    public static boolean isReadContactPermission() {
        // 读取联系人
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, null);
        boolean is = false;
        if (cursor != null && cursor.moveToFirst()) {
            is = true;
            cursor.close();
        }
        return is;
    }

    /**
     * 获取SDCard的地址
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static int[] getPhoneScreenWH() {
        return getPhoneScreenWH(getContext());
    }


    public static int[] getPhoneScreenWH(Context context) {
        int wh[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wh[0] = dm.widthPixels;
        wh[1] = dm.heightPixels;
        return wh;
    }

    public static int[] getPhoneScreenWHForPX(Context context) {
        int wh[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wh[0] = dipTOpx(dm.widthPixels);
        wh[1] = dipTOpx(dm.heightPixels);
        return wh;
    }


    public static int dipTOpx(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pxTodip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int spTopx(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int spTopx(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int pxTosp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 延迟显示
     *
     * @param v
     */
    public static void showInputMethodManagerDelay(View v) {
        showInputMethodManager(v, 200, null);
    }

    public static void showInputMethodManagerEditText(EditText editText) {
        showInputMethodManagerNow(editText, null);
        editText.setSelection(editText.getText().length());
    }

    /**
     * 马上显示
     *
     * @param v
     */
    public static void showInputMethodManagerNow(View v, onInputMethodListener l) {
        showInputMethodManager(v, 0, l);
    }


    public static void showInputMethodManager(final View v, long time, final onInputMethodListener l) {
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    getInputMethodManager(v.getContext()).showSoftInput(v, 0);
                    if (l != null) l.onInputMethodShow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, time);

    }

    //判断软键盘是都出现
    public static boolean isInputMethodShow(View v) {

//        return  context.getWindow().peekDecorView() != null;
        InputMethodManager imm = getInputMethodManager(v.getContext());
        return imm != null && imm.isActive(v);
//        return context.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 关闭输入软键盘
     *
     * @param v
     */
    public static boolean closeInputMethodManager(View v) {
        if (v == null) return false;
        return getInputMethodManager(v.getContext()).hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public interface onInputMethodListener {

        void onInputMethodShow();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copy(String text) {
        ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
    }

    public static String getTextFromClip() {
        ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null && cmb.getText() != null) {
            return cmb.getText().toString();
        }
        return "";
    }


    /**
     * 获取Android的版本号
     *
     * @return
     */
    public static String getAndroidId() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     */
    public static String getPhone() {
        return Build.MODEL;
    }

    /**
     * 获取Android系统的api
     *
     * @return
     */
    public static int getAndroidApi() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获取应用的UID， 优先读设备的IEMI，
     * 如果IMEI不存在,则试着取mac值，如果仍然为空则动态生成一个UID存入sharepreference
     *
     * @return
     */
    private static String readUniqid() {
        String uid = null;
        try {
            uid = getDeviceId(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(uid) && !uid.contains("0000000")) {
            return uid;
        }

        uid = getLocalMacAddress(getContext());
        if (!TextUtils.isEmpty(uid)) {
            return uid;
        }
        return uid;
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (TextUtils.isEmpty(info.getMacAddress())) {
            return "";
        } else {
            return info.getMacAddress().replaceAll(":", "");
        }
    }

    /**
     * 返回mac，如果当前为空，会参数重新获取
     *
     * @return
     */
    public static String getMac() {
        String mac = getLocalMacAddress(getContext());
        if (TextUtils.isEmpty(mac)) {
            mac = readUniqid();
        }
        return mac;
    }


    /**
     * 获取一个随机数
     *
     * @param startNum 从那个位置开始
     * @param endNum   从那个位置结束
     * @return
     */
    public static int getRandom(int startNum, int endNum) {
        return startNum + (int) (Math.random() * endNum);
    }

    public static int getRandom(int endNum) {
        return getRandom(0, endNum);
    }

    public static boolean isMobileNetwork() {
        NetworkInfo networkInfo = getActiveNetworkType();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static NetworkInfo getActiveNetworkType() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w("TAG", "couldn't get connectivity manager");
            return null;
        }

        NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null) {
            return null;
        }
        return activeInfo;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    public static void installApp(Activity activity, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    //启动硬件加速
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void startLayerTypeHardWare(View view) {
        if (view != null)
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    //获取项目的版本
    public static int getAppVersion() {
        Context context = getContext();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }


    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    //跳转到主界面
    public static void startHome(Activity activity) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        activity.startActivity(mHomeIntent);

    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static float getStatusHeight() {
        //获取status_bar_height资源的ID
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    //获取相机的拍摄角度
    public static float getCameraViewAngle() {
        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        float angle = parameters.getHorizontalViewAngle();
        camera.release();
        return angle;
    }

    //跳转到应用市场去评价
    public static void toEvaluationApplication(Activity activity) {
        try {
            //引导评分
            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    //获取项目包名
    public static String getPackageName(Context context) {
        if (context == null) return "you";
        try {
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "you";
    }


    public static void printlnInfo(String text) {
        if (!BuildConfig.ENVIRONMENT) {
            Log.i("youjiannuo1", text);
        }
    }

    public static void printlnInfo(String text, Object... args) {
        if (!BuildConfig.ENVIRONMENT) {
            Log.i("youjiannuo1", String.format(text, args));
        }
    }

    //重新启动app
    public static void reStartApp(Activity activity, Class startClassName) {
//        Intent it = new Intent(activity, cls);
//        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        activity.startActivity(it);

        final Intent intent = new Intent(activity, startClassName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void startNewTaskActivity(Context context, Class cls, String keys[], Object[] objs) {
        if (context == null) return;
        Intent intent = new Intent();
        if (keys != null && objs != null) {
            for (int i = 0; i < keys.length; i++) {
                if (objs[i] instanceof String) {
                    intent.putExtra(keys[i], (String) objs[i]);
                } else if (objs[i] instanceof Integer) {
                    intent.putExtra(keys[i], (Integer) objs[i]);
                } else if (objs[i] instanceof Boolean) {
                    intent.putExtra(keys[i], (Boolean) objs[i]);
                }
            }
        }

        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
        context.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        context.startActivity(intent);
        //    System.exit(0);
        //保存umeng数据
        //  MobclickAgent.onKillProcess(context);
    }


    public String getVersionCode() {
        PackageManager manager;
        PackageInfo info = null;
        manager = getContext().getPackageManager();
        try {
            info = manager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }


    public static boolean checkApkExist(Context context, String packageName) {
//        if (packageName == null || "".equals(packageName))
//            return false;
//        try {
//            ApplicationInfo info = context.getPackageManager()
//                    .getApplicationInfo(packageName,
//                            PackageManager.GET_UNINSTALLED_PACKAGES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageName.equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }


    public static void callPhone(Context context, String number) {
        Intent intent = null;
        try {
            if (PermissionManager.getInstance().isHavePermission(context, CALL_PHONE)) {
                if (PermissionsChecker.isCheckCallPhone()) {
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                }
            } else {
//                if (context instanceof YNCommonActivity) {
//                    PermissionManager.getInstance().requestPermission((YNCommonActivity) context, CALL_PHONE);
//                } else {
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
//                }
            }
        } catch (Exception r) {
//            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        }
        if (intent != null) {
            context.startActivity(intent);
        }
    }


    //短信跳转
    public static void startSms(Context context, String msg, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        context.startActivity(intent);
    }

    //发送 邮件
    public static void startEmail(Context context, String title, String msg, String... toEmails) {
        toEmails = toEmails == null ? new String[0] : toEmails;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        context.startActivity(Intent.createChooser(intent, "请选择邮件类型"));
    }


    public static void startAppDetailSettingActivity(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(context), null));
        context.startActivity(localIntent);
    }


    /**
     * 查询是否具备某一个权限
     *
     * @param permission 权限的名称，例如
     * @return
     */
    public static boolean checkPermission(String permission) {
        PackageManager pm = getContext().getPackageManager();

        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, getPackageName(getContext()));
    }

    public static String getMetaData(String key) {
        Context context = getContext();
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key, "jw");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return "jw";
    }

    public static Class getNameClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        if (getAndroidApi() >= 23) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);

        }
    }


    //是否有这些应用
    public static boolean isPackage(String packageName) {
        final PackageManager packageManager = getContext().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断当前是否为Debug模式
     */
    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = getContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;

    }


    /**
     * 跳转到微信
     */
    public static void startWeChatApi(Activity activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            ToastUtil.showNewSuccessMessage("检查到您手机没有安装微信，请安装后使用该功能");
        }
    }

    //是否打开了消息通知权限
    public static boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        return notificationManagerCompat.areNotificationsEnabled();
    }

    public static void jumpNotificationManager(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName(context));
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName(context));
            intent.putExtra("app_uid", getContext().getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(context), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
