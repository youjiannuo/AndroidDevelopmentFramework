package com.yn.framework.controller;

import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.http.HttpExecute;

/**
 * Created by youjiannuo on 18/5/9.
 * Email by 382034324@qq.com
 * 连续发生网络请求
 */
public class BaseHttpListController extends BaseController {

    private int mCount;

    public BaseHttpListController(YNCommonActivity activity) {
        super(activity);
    }

    public void sendHttp(int https[], String[][] value, OnSendHttpsListener l) {
        sendHttp(https, value, true, l);
    }

    public void sendHttp(int https[], String[][] value, boolean isProgress, OnSendHttpsListener l) {
        if (isProgress && mActivity != null) {
            mActivity.showProgressDialog();
        }
        sendHttp(new Item(https, value, 0, l));
    }

    private void sendHttp(final Item item) {
        sendMessage(new OnCreateNewNetworkTaskListener() {
            @Override
            public void onNewNetworkTask(HttpExecute.NetworkTask backTask) {
                backTask.backTask.obj = item;
                backTask.backTask.method = "";
                backTask.backTask.methodError = "";
                backTask.backTask.methodStart = "";
                backTask.backTask.isProgress = false;
            }
        }, "", item.https[item.index], item.values[item.index]);
    }

    @Override
    public Object visitSuccess(Object object, BackTask backTask) {
        if (mActivity != null
                && !mActivity.isFinishing()
                && backTask.obj != null) {
            Item item = (Item) backTask.obj;
            if (item.onSendHttpsListener != null) {
                item.onSendHttpsListener.progress(object, item.https[item.index], item.values[item.index]);
            }
            item.index++;
            if (item.index < item.https.length) {
                sendHttp(item);
            } else {
                if (mActivity != null) {
                    mActivity.closeProgressDialog();
                }
                if (item.onSendHttpsListener != null) {
                    item.onSendHttpsListener.success(item.https, item.values);
                }
            }

        }
        return super.visitSuccess(object, backTask);
    }

    @Override
    public Object visitFail(Object obj, BackTask backTask) {
        if (mActivity != null
                && !mActivity.isFinishing()
                && backTask.obj != null) {
            Item item = (Item) backTask.obj;
            if (item != null && item.onSendHttpsListener != null) {
                item.onSendHttpsListener.error(obj, item.https[item.index], item.values[item.index]);
            }
            if (mActivity != null) {
                mActivity.closeProgressDialog();
            }
        }
        return super.visitFail(obj, backTask);
    }

    class Item {
        int https[];
        String[][] values;
        int index;
        OnSendHttpsListener onSendHttpsListener;

        public Item(int https[], String[][] values, int index, OnSendHttpsListener l) {
            this.https = https;
            this.values = values;
            this.index = index;
            onSendHttpsListener = l;
        }
    }

    public static class OnSendHttpsImpListener implements OnSendHttpsListener {

        @Override
        public void progress(Object successObj, int http, String[] values) {

        }

        @Override
        public void error(Object errorObj, int http, String[] values) {

        }

        @Override
        public void success(int[] https, String[][] value) {

        }
    }

    public interface OnSendHttpsListener {
        void progress(Object successObj, int http, String values[]);

        void error(Object errorObj, int http, String values[]);

        void success(int https[], String[][] value);
    }

}
