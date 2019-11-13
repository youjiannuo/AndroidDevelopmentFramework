package com.yn.framework.http;

import com.yn.framework.controller.BackTask;

/**
 * Created by youjiannuo on 18/1/5.
 * Email by 382034324@qq.com
 */

public class HttpVisitCallBackImp implements HttpVisitCallBack {
    @Override
    public void visitNetworkStart(BackTask backTask) {

    }

    @Override
    public boolean visitAllNetworkSuccess(Object obj, BackTask backTask) {
        return false;
    }

    @Override
    public void visitNetworkSuccess(Object obj, BackTask backTask) {

    }

    @Override
    public void visitNetworkProgress(int project) {

    }

    @Override
    public void visitNetworkFail(BackTask backTask) {

    }

    @Override
    public void visitNetworkFail(Object obj, BackTask backTask) {

    }

    @Override
    public void visitNetworkCancel(BackTask backTask) {

    }

    @Override
    public void visitTokenFailure(HttpExecute.NetworkTask task) {

    }

    @Override
    public boolean getCache(BackTask task) {
        return false;
    }
}
