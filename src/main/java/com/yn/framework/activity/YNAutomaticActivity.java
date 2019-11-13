package com.yn.framework.activity;

import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.yn.framework.review.OnHttpListener;
import com.yn.framework.review.YNHttpOperation;
import com.yn.framework.review.YNListView;

/**
 * Created by youjiannuo on 16/4/20.
 * 自动化activity
 */
public class YNAutomaticActivity extends YNCommonActivity implements OnHttpListener, SwipeRefreshLayout.OnRefreshListener {

    //是否已经获取数据
//    private boolean mGetInfo = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mRefreshNum = 0;


    @Override
    protected void initView() {
        super.initView();
        if (getSwipeRefreshLayoutId() != 0) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(getSwipeRefreshLayoutId());
        }
    }

    @Override
    protected void setViewData() {
        super.setViewData();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }


    @Override
    protected void onResume() {
        if (!mIsResume || isHttpResume()) {
            sendHttp();
        }
        super.onResume();
    }


    public void sendHttp() {
        int http[] = getHttpId();
        String values[][] = getHttpValue();
        for (int i = 0; i < http.length; i++) {
            String value[];
            if (i < values.length)
                value = values[i];
            else value = new String[0];
            startHttpId(http[i], value);
        }
    }

    protected void startHttpId(int id, String[] value) {
        YNHttpOperation httpOperation = findHttpOperationById(id);
        httpOperation.showErrorView();
        httpOperation.setOnHttpListener(this);
        httpOperation.startHttp(value);
    }

    protected YNHttpOperation findHttpOperationById(int id) {
        return findView(id);
    }

    /**
     * 是否只发送一次http请求
     *
     * @return true，可以发送多次，false只发送一次
     */
    protected boolean isHttpResume() {
        return true;
    }

    //需要发送的请求
    protected int[] getHttpId() {
        if (mAnnotationController != null) {
            return mAnnotationController.mHttpId;
        }
        return new int[0];
    }

    protected String[][] getHttpValue() {
        if (mAnnotationController != null) {
            return mAnnotationController.mValues;
        }
        return new String[0][0];
    }

    protected int getSwipeRefreshLayoutId() {
        if (mAnnotationController != null) {
            return mAnnotationController.mSwipeRefreshLayoutId;
        }
        return 0;
    }

    @Override
    protected void onReLoadDataFromNetwork() {
        super.onReLoadDataFromNetwork();
        sendHttp();
    }


    @Override
    public void showLoadFailDialog() {
//        if (!mGetInfo) {
        super.showLoadFailDialog();
        //将展示View隐藏起来
        mShowView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void closeLoadFailDialog() {
        super.closeLoadFailDialog();
        mShowView.setVisibility(View.VISIBLE);
    }


    @Override
    @Deprecated
    public void onHttpSuccess(Object obj) {
//        mGetInfo = true;
        mRefreshNum++;
        closeRefresh();
    }

    @Override
    public void onHttpFail(Object obj) {
        mRefreshNum++;
        closeRefresh();
    }

    @Override
    public void onHttpSuccess(Object obj, View view) {
//        mGetInfo = true;
        mRefreshNum++;
        closeRefresh();
    }

    @Override
    public void onHttpFail(Object obj, View view) {
        mRefreshNum++;
        closeRefresh();
    }

    @Override
    public Object onHttpDetailSuccess(Object obj, View view) {
        return obj;
    }

    protected void closeRefresh() {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null && mRefreshNum >= getHttpId().length) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 500);
    }

    @Override
    public void onRefresh() {
        mRefreshNum = 0;
        sendHttp();
    }

    @Override
    protected void notRunStartResume() {
        super.notRunStartResume();
    }

    protected String getKeyString(String key) {
        return getIntent().getStringExtra(key);
    }

    public String getTextViewString(int id) {
        return ((TextView) findViewById(id)).getText().toString();
    }


    protected void reSendHttpList(YNListView listView) {
        listView.setFirstHttp(false);
        sendHttp();
        listView.setFirstHttp(true);
    }


}
