package com.yn.framework.review.manager;

import android.view.View;

import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;

/**
 * Created by youjiannuo on 2018/12/12.
 * Email by 382034324@qq.com
 */
public class OnProxyBackListener<T> implements OnBackListener<T> {

    private OnBackListener mOnBackListener;

    public OnProxyBackListener(OnBackListener onBackListener) {
        mOnBackListener = onBackListener;
    }

    @Override
    public boolean checkParams() {
        return mOnBackListener.checkParams();
    }

    @Override
    public String[] getHttpValue() {
        return mOnBackListener.getHttpValue();
    }

    @Override
    public String[] getHttpKey() {
        return mOnBackListener.getHttpKey();
    }

    @Override
    public Object[] getTitleAndMsgValue() {
        return mOnBackListener.getTitleAndMsgValue();
    }

    @Override
    public String[] getButtonString() {
        return mOnBackListener.getButtonString();
    }

    @Override
    public void backRemindAlertDialog(RemindAlertDialog dialog) {
        mOnBackListener.backRemindAlertDialog(dialog);
    }

    @Override
    public boolean onItemClick(View view, int position, T data) {
        return mOnBackListener.onItemClick(view, position, data);
    }

    @Override
    public void onNetworkTask(View view, int position, HttpExecute.NetworkTask task) {
        mOnBackListener.onNetworkTask(view, position, task);
    }

    @Override
    public void onHttpSuccess(View view, int position, T data) {
        mOnBackListener.onHttpSuccess(view, position, data);
    }

    @Override
    public void onHttpFail(View view, int position, T data) {
        mOnBackListener.onHttpFail(view, position, data);
    }
}
