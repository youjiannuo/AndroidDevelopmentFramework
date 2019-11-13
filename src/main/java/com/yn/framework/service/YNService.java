package com.yn.framework.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yn.framework.system.SystemUtil;

/**
 * Created by youjiannuo on 17/1/17
 */

public class YNService extends Service {

    protected Handler mUIHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new YNBinder() {
            @Override
            public YNService getYNService() {
                return YNService.this;
            }
        };
    }

    public void setHandler(Handler handler) {
        mUIHandler = handler;
    }

    public void updateUI(Runnable runnable) {
        if (mUIHandler != null) {
            mUIHandler.post(runnable);
        }
    }

    public void clear() {
        SystemUtil.printlnInfo("清除了数据");
    }

}
