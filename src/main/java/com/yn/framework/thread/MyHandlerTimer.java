package com.yn.framework.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by youjiannuo on 2019/8/29.
 * Email by 382034324@qq.com
 */
public class MyHandlerTimer {

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private boolean mIs = false;
    private OnTimeListener mOnTimeListener;

    public MyHandlerTimer() {
        mHandlerThread = new HandlerThread("xie");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mOnTimeListener != null) {
                    mOnTimeListener.onTime();
                }
            }
        };

    }

    public void start() {
        if (mIs) return;
        mIs = true;
        mHandler.sendEmptyMessage(0);
    }

    public void setOne() {
        if (mIs) {
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    }

    public void stop() {
        mIs = false;
    }

    public void setOnTimeListener(OnTimeListener l) {
        mOnTimeListener = l;
    }

    public interface OnTimeListener {
        void onTime();
    }

}
