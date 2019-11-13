package com.yn.framework.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.yn.framework.R;
import com.yn.framework.system.StringUtil;
import com.yn.framework.view.WebViewUtil;

/**
 * Created by youjiannuo on 16/11/9
 */
public class YNWebActivity extends YNCommonActivity {

    public static final int FILE = 0;
    public static final int URL = 0;

    protected WebView mYNWebView;
    private ProgressBar mProgressBar;
    protected WebViewUtil mWebViewUtil;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState, R.layout.activity_web, R.string.yn_load);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        mYNWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebViewUtil = new WebViewUtil(mYNWebView, mProgressBar);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mWebViewUtil.onKeyDown(keyCode, event)) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void setViewData() {
        super.setViewData();
        mWebViewUtil.setUrl(getUrl());
        mWebViewUtil.setOnWebViewInfoListener(new WebViewUtil.OnWebViewInfoListener() {
            @Override
            public void onTitleInfoString(String title, WebView webView) {
                if (!StringUtil.isEmpty(title)) {
                    mBarView.setTitle(title);
                }
            }

            @Override
            public void pageFinish() {
                finish();
            }

            @Override
            public void htmlText(String html) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebViewUtil.runJS("onPause();");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebViewUtil.runJS("onDestroy();");
        mWebViewUtil.onDestroy();
    }

    protected String getUrl() {
        return getIntentString(KEY_URL);
    }



}
