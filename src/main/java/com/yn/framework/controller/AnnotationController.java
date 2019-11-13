package com.yn.framework.controller;

import android.content.Intent;
import android.graphics.Bitmap;

import com.yn.framework.activity.BaseFragment;
import com.yn.framework.activity.DoBackground;
import com.yn.framework.activity.Layout;
import com.yn.framework.activity.TopBar;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.data.JSON;
import com.yn.framework.system.ContextManager;
import com.yn.framework.system.StringUtil;
import com.yn.framework.view.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.yn.framework.system.BitmapUtils.getRightDrawable;
import static com.yn.framework.system.StringUtil.getString;
import static com.yn.framework.system.StringUtil.isEmpty;
import static java.lang.String.format;

/**
 * Created by youjiannuo on 2018/7/30.
 * Email by 382034324@qq.com
 */
public class AnnotationController {

    public int mLayoutId = 0;
    public int mSwipeRefreshLayoutId = 0;
    public int[] mHttpId = new int[0];
    public String[][] mValues;

    private String mTitle = "";
    private String mRightButton = "";
    public NavigationBarView mBarView;
    private Intent mIntent;
    private Object mTargetObj;
    private YNCommonActivity mActivity;
    public int mThread = -1;
    public boolean mIsThreadShowProgress = true;


    public AnnotationController(YNCommonActivity activity) {
        mTargetObj = activity;
        mActivity = activity;
        mIntent = activity.getIntent();
        init();
    }

    public AnnotationController(BaseFragment fragment) {
        mTargetObj = fragment;
        mIntent = fragment.getActivity().getIntent();
        init();
    }


    private void init() {
        Class cls = mTargetObj.getClass();
        if (cls.isAnnotationPresent(Layout.class)) {
            Layout layout = ((Layout) cls.getAnnotation(Layout.class));
            mLayoutId = layout.layoutId();
            mSwipeRefreshLayoutId = layout.swipeRefreshLayoutId();
            mHttpId = layout.httpId();
            List<String[]> list = new ArrayList<>();
            values(layout.values(), list);
            values(layout.values1(), list);
            values(layout.values2(), list);
            values(layout.values3(), list);
            for (int i = list.size(); i < mHttpId.length; i++) {
                list.add(new String[]{});
            }
            mValues = new String[list.size()][];
            list.toArray(mValues);
        }
        if (cls.isAnnotationPresent(DoBackground.class)) {
            mThread = 1;
            DoBackground doBackground = (DoBackground) cls.getAnnotation(DoBackground.class);
            if (doBackground.isSingle()) {
                mThread = 2;
            }
            mIsThreadShowProgress = doBackground.isProgress();
        }
    }


    private void values(String values[], List<String[]> list) {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            if (values[i].contains("$")) {
                values[i] = getString(mIntent.getStringExtra(values[i].substring(2, values[i].length() - 1)));
            } else if (values[i].contains("#")) {
                values[i] = mIntent.getStringExtra("KEY_" + values[i].substring(2, values[i].length() - 1).toUpperCase());
            }
        }
        list.add(values);
    }

    public boolean isTopBar() {
        Class cls = mTargetObj.getClass();
        return cls.isAnnotationPresent(TopBar.class);
    }

    public void initTopBar() {
        Class cls = mTargetObj.getClass();
        if (mBarView != null && cls.isAnnotationPresent(TopBar.class)) {
            TopBar topBar = (TopBar) cls.getAnnotation(TopBar.class);
            mBarView.setVisibility(VISIBLE);
            if (topBar.titleResourceId() != -1) {
                mBarView.setTitle(topBar.titleResourceId());
            } else if (!isEmpty(topBar.titleString())) {
                String names[] = StringUtil.get$Params(topBar.titleString());
                if (names == null || names.length <= 1) {
                    mBarView.setTitle(topBar.titleString());
                } else {
                    JSON json = null;
                    if (mActivity.getAnnotationString() != null) {
                        json = new JSON(mActivity.getAnnotationString());
                    }
                    String values[] = new String[names.length - 1];
                    for (int i = 0; i < values.length; i++) {
                        if (json != null) {
                            values[i] = json.getStrings(names[i + 1]);
                        } else {
                            values[i] = mIntent.getStringExtra(names[i + 1]);
                        }

                    }
                    mBarView.setTitle(format(names[0], (Object[]) values));
                }
            }
            if (topBar.isCloseLeft()) {
                mBarView.getLeftView().setVisibility(GONE);
            }
            if (topBar.rightButtonResourceId() > 0) {
                Bitmap drawable = getRightDrawable(topBar.rightButtonResourceId());
                if (drawable == null || drawable.getWidth() == 0) {
                    mBarView.setRightTextView(ContextManager.getString(topBar.rightButtonResourceId()));
                } else {
                    mBarView.setRightImageButton(topBar.rightButtonResourceId());
                }
            } else {
                mBarView.setRightTextView(topBar.rightButtonString());
            }

        } else if (mBarView != null) {
            mBarView.setVisibility(GONE);
        }
    }


}
