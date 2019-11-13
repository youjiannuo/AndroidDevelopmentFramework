package com.yn.framework.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.yn.framework.system.TimeUtil;

import java.util.HashSet;

/**
 * Created by youjiannuo on 18/1/23.
 * Email by 382034324@qq.com
 * 发车收集器，间隔某一个时间段发车一次
 */

public class DepartThread extends HandlerThread {

    private volatile HashSet<Object> mObjects;
    private HashSet<Object> mMessageObjects;
    //时间间隔
    private static final long mSpaceTime = 200;
    private OnTaskCallBack mOnTaskCallBack;
    private Handler mHandler;
    private TimeUtil mTimeUtil;

    public DepartThread(String name) {
        super(name);
        mObjects = new HashSet<>();
        mTimeUtil = new TimeUtil();
        mTimeUtil.init();
    }

    private void init() {
        if (mHandler != null) return;
        start();
        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mOnTaskCallBack != null && mMessageObjects != null) {
                    mOnTaskCallBack.onTask(mMessageObjects);
                }
            }
        };
    }

    public void sendTask(final Object object, final OnTaskCallBack callBack) {
        init();
        addTask(object);
        mOnTaskCallBack = callBack;
        if (!mTimeUtil.checkoutTime(mSpaceTime)) {
            getNewObject();
            mHandler.sendEmptyMessage(0);
        }
    }

    private synchronized void addTask(Object object) {
        mObjects.add(object);
    }

    private synchronized void cleanTask() {
        if (mObjects != null) {
            mObjects.clear();
        }
    }

    private synchronized void getNewObject() {
        mMessageObjects = new HashSet<>();
        for (Object obj : mObjects) {
            mMessageObjects.add(obj);
        }
        mObjects.clear();
    }

    public interface OnTaskCallBack {
        void onTask(HashSet<Object> objects);
    }

}