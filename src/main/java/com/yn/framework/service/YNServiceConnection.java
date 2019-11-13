package com.yn.framework.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.yn.framework.activity.YNCommonActivity;

/**
 * Created by youjiannuo on 17/1/17
 */

public abstract class YNServiceConnection implements ServiceConnection {

    private YNService mYNService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        YNBinder binder = (YNBinder) service;
        if (service == null) {
            bindSuccess(null);
            return;
        }
        mYNService = binder.getYNService();
        bindSuccess(mYNService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    protected abstract void bindSuccess(YNService service);

    public void unbindService(YNCommonActivity activity) {
        if (mYNService != null) {
            mYNService.clear();
        }
        if (activity != null) {
            activity.unbindService(this);
        }
    }
}
