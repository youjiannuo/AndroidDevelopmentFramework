package com.yn.framework.service;

import android.os.Binder;

/**
 * Created by youjiannuo on 17/1/17
 */

public abstract class YNBinder extends Binder {

    public abstract YNService getYNService();

}
