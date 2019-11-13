package com.yn.framework.system;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.yn.framework.thread.YNAsyncTask;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by youjiannuo on 15/7/1.
 */
public abstract class YNApplication extends Application {

    private YNUncaughtExceptionHandler mYNUncaughtExceptionHandler = null;
    private Map<String, Activity> mActivity = new HashMap<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        YNAsyncTask.init();
        ContextManager.setContext(this);
        //获得全局异常
        mYNUncaughtExceptionHandler = YNUncaughtExceptionHandler.init();
        mYNUncaughtExceptionHandler.setContent(getApplicationContext());

        BuildConfig.ENVIRONMENT = getEnvironment();
        BuildConfig.APPLICATION_ID = getApplicationId();
        BuildConfig.HOST = getHost();
        BuildConfig.VERSION_NAME = getVersionName();
        BuildConfig.VERSION_CODE = getVersionCode();
        BuildConfig.HOST1 = getHost1();

    }

    public Activity getActivity(Class cls) {
        return mActivity.get(cls.getName());
    }

    public void setActivity(Activity activity) {
        mActivity.put(activity.getClass().getName(), activity);
    }

    public void clear(Activity nowActivity) {
        for (Activity activity : mActivity.values()) {
            if (activity != null && activity != nowActivity) {
                activity.finish();
            }
        }
        if (nowActivity != null) {
            nowActivity.finish();
        }
        mActivity.clear();
    }

    public void addOnUnCaughtExceptionListener(OnUnCaughtExceptionListener l) {
        if (mYNUncaughtExceptionHandler != null)
            mYNUncaughtExceptionHandler.addOnUnCaughtExceptionListener(l);
    }

    public void removeOnUnCaughtExceptionListener(OnUnCaughtExceptionListener l) {
        if (mYNUncaughtExceptionHandler != null)
            mYNUncaughtExceptionHandler.removeOnUnCaughtExceptionListener(l);
    }

    /**
     * get host
     */
    public abstract String getHost();

    /**
     * get environment
     */
    public abstract boolean getEnvironment();

    public abstract String getApplicationId();

    public abstract String getVersionName();

    public abstract String getVersionCode();

    public abstract String getHost1();
}
