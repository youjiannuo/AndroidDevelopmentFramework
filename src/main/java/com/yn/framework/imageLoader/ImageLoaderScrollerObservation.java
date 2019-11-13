package com.yn.framework.imageLoader;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.yn.framework.system.SystemUtil;
import com.yn.framework.system.TimeUtil;

/**
 * Created by youjiannuo on 17/6/15.
 */

public class ImageLoaderScrollerObservation implements AbsListView.OnScrollListener {

    //可以开始加载图片
    public final static int TYPE_START = 1;
    public final static int TYPE_STOP = 2;

    //滑动速度在这个之内可以是加载图片
    private final static int MIX_SPEED = 1;
    private int mState = TYPE_START;
    private TimeUtil mTimeUtl;
    private OnListViewProxy mOnListViewProxy;

    public ImageLoaderScrollerObservation(OnListViewProxy proxy) {
        mOnListViewProxy = proxy;
    }

    public void changeState(int state) {
        if (AbsListView.OnScrollListener.SCROLL_STATE_FLING == state) {
            mState = TYPE_STOP;
        } else if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == state ||
                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == state) {
            mState = TYPE_START;
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == state) {
                //需要刷新list
                mOnListViewProxy.notifyChangeSet();
            }
        }
    }

    public void changeRecyclerState(int state) {
        SystemUtil.printlnInfo(state + "");
        if (RecyclerView.SCROLL_STATE_IDLE == state) {
            mState = TYPE_START;
            //需要刷新list
            mOnListViewProxy.notifyChangeSet();
        } else if (RecyclerView.SCROLL_STATE_SETTLING == state) {
            //自由滚动
            mState = TYPE_STOP;
        }
    }


    public int getState() {
        SystemUtil.printlnInfo("get statu = " + mState);
        return mState;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        changeState(scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface OnListViewProxy {
        void notifyChangeSet();
    }


}
