package com.yn.framework.review.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yn.framework.R;
import com.yn.framework.activity.BaseFragment;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.controller.OnCreateNewNetworkTaskListener;
import com.yn.framework.data.JSON;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;
import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.BuildConfig;
import com.yn.framework.system.SystemUtil;

import static com.yn.framework.data.JSON.json;
import static com.yn.framework.review.manager.YNClickBack.dealEnd;
import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 16/3/17.
 */
public class YJNView implements RemindAlertDialog.OnKeyListener,
        RemindAlertDialog.OnClickListener, OnCreateNewNetworkTaskListener {

    private String mDataKey;
    private String mDataKeys;
    private String mOnClickKey;
    private int mOnClick = 0;
    private int onClickValue;
    private String mValue = "";
    private View mView;
    private Object mData;
    private JSON mDataJson;
    private Context mContext;
    private BaseFragment mBaseFragment;
    private OnBackListener mOnBackListener;
    private OnBackListener mInitOnBackListener;
    private Class mClass;
    private int mPosition = 0;
    private RemindAlertDialog mRemindAlertDialog;
    private int mIndex;
    private int mHttpSuccess = 0;
    //是否显示错误信息
    private boolean mShowErrorView = false;
    //是否进度条
    private boolean mShowProgress = true;
    //发送http请求
    private YNController mYNController;
    private String mUmengKey = "";
    private OnClickInterceptListener mOnClickInterceptListener;

    public YJNView(View view, Context context, AttributeSet attrs) {
        this(view, context, attrs, null);
    }

    public YJNView(View view, Context context, AttributeSet attrs, TypedArray array) {
        if (array == null)
            array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mDataKey = array.getString(R.styleable.YNView_set_data_name);
        mDataKeys = array.getString(R.styleable.YNView_set_data_names);
        mOnClick = array.getInt(R.styleable.YNView_onClick, 0);
        mOnClickKey = array.getString(R.styleable.YNView_onClickKey);
        onClickValue = array.getResourceId(R.styleable.YNView_onClickValue, 0);
        mValue = array.getString(R.styleable.YNView_value);
        mHttpSuccess = array.getInt(R.styleable.YNView_httpSuccess, 0);
        mUmengKey = array.getString(R.styleable.YNView_umeng_key);
        String className = array.getString(R.styleable.YNView_back_class);
        mView = view;
        mContext = context;
        try {
            if (!isEmpty(className)) {
                mClass = Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (mOnClick != 0) {
            mOnBackListener = new YNClickBack(mContext, array);
            setOnBackListener(mOnBackListener);
        }
        array.recycle();
    }

    public void setOnClick(int onClick) {
        mOnClick = onClick;
    }

    public void setBaseFragment(BaseFragment fragment) {
        mBaseFragment = fragment;
    }

    public void setData(Object data) {
        mData = data;
    }

    public void setHttpId(int http) {
        onClickValue = http;
    }

    public int getHttpId() {
        return onClickValue;
    }

    public void setOnClickInterceptListener(OnClickInterceptListener l) {
        mOnClickInterceptListener = l;
    }

    public void reSetOnBackListener() {
        setOnBackListener(new OnYNBackListener());
    }

    public void setOnBackListener(OnBackListener l) {
        setOnBackListener(l, mPosition);
    }

    public void setOnBackListener(final OnBackListener l, final int index) {
        if (mOnBackListener != null && mOnBackListener instanceof YNClickBack && mOnBackListener != l) {
            ((YNClickBack) mOnBackListener).setOnBackListener(l);
        } else {
            mOnBackListener = l;
        }
        mIndex = index;
        mPosition = index;
        if (l != null) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStartClick();
                    if (!isEmpty(mUmengKey)) {
                        ((YNCommonActivity) mContext).addUmengClickStatistics(mUmengKey);
                    }
                }
            });
        }
    }

    public void setActivityBackListener(final YNCommonActivity activity) {
        setOnBackListener(new OnBackListener() {
            @Override
            public boolean checkParams() {
                return activity.checkParams(mView, mPosition);
            }

            @Override
            public String[] getHttpValue() {
                return activity.getHttpValue(mView, mPosition);
            }

            @Override
            public String[] getHttpKey() {
                return activity.getHttpKey(mView, mPosition);
            }

            @Override
            public Object[] getTitleAndMsgValue() {
                return activity.getTitleAndMsgValue(mView, mPosition);
            }

            @Override
            public String[] getButtonString() {
                return activity.getButtonString(mView, mPosition);
            }

            @Override
            public void backRemindAlertDialog(RemindAlertDialog dialog) {
                activity.backRemindAlertDialog(dialog);
            }

            @Override
            public boolean onItemClick(View view, int position, Object data) {
                return activity.onItemClick(view, position, data);
            }

            @Override
            public void onNetworkTask(View view, int position, HttpExecute.NetworkTask task) {
                activity.onNetworkTask(view, position, task);
            }

            @Override
            public void onHttpSuccess(View view, int position, Object data) {
                activity.onHttpSuccess(view, position, data);
            }

            @Override
            public void onHttpFail(View view, int position, Object data) {
                activity.onHttpFail(view, position, data);
            }
        });
    }

    //确定发送
    public void onStartClick() {
        if (mOnBackListener instanceof YNClickBack) {
            YNClickBack ynClickBack = (YNClickBack) mOnBackListener;
            if (ynClickBack.mOnBackListener != null
                    && ynClickBack.mOnBackListener instanceof OnYNBackListener) {
                ((OnYNBackListener) ynClickBack.mOnBackListener).mButton = mView;
            }
        } else if (mOnBackListener instanceof OnYNBackListener) {
            ((OnYNBackListener) mOnBackListener).mButton = mView;
        }
        if (mOnClick == 4) {
            mOnBackListener.onItemClick(mView, mIndex, mData);
            return;
        }
        if (mOnBackListener.checkParams()) return;
        String buttons[] = mOnBackListener.getButtonString();
        Object msg[] = mOnBackListener.getTitleAndMsgValue();
        if (buttons.length != 0 && msg.length != 0) {
            showRemindBox(buttons, msg[1], (String) msg[0], -1, -1);
        } else if (!mOnBackListener.onItemClick(mView, mIndex, mData)) {
            startClick();
        }
    }

    public YNController startClick(OnBackListener l) {
        mOnBackListener = l;
        return startClick();
    }

    private YNController startClick() {
        String codeValues[] = mOnBackListener.getHttpValue();
        if (!isEmpty(mValue)) {
            codeValues = mValue.split(",");
        }
        String values[];
        String keys[] = null;
        if (mOnClickKey != null && mOnClickKey.length() != 0) {
            keys = mOnClickKey.split(",");
        }


        if (keys != null && keys.length != 0) {
            JSON json;

            if (mData == null) {
                mData = "";
            }
//            if (mDataJson == null) {
            if (mData instanceof JSON) {
                json = (JSON) mData;
            } else {
                json = json(mData.toString());
                mDataJson = json;
            }
//            } else {
//                json = mDataJson;
//            }
            values = new String[keys.length + (codeValues == null ? 0 : codeValues.length)];
            for (int i = 0; i < keys.length; i++) {
                String s = json.getStrings(keys[i]);
                if (!isEmpty(s)) {
                    values[i] = s;
                } else {
                    values[i] = ((YNCommonActivity) mContext).getIntentString(keys[i]);
                    if (isEmpty(values[i])) {
                        //获取控件资源
                        TextView textView = (TextView) ((YNCommonActivity) mContext).findViewById(YNResourceUtil.getId(keys[i]));
                        if (textView != null) {
                            values[i] = textView.getText().toString().trim();
                        } else if (!BuildConfig.ENVIRONMENT) {
                            String error = "参数key:" + keys[i] + "无法确认从那么获取";
//                            ToastUtil.showNormalMessage(error);
                            SystemUtil.printlnInfo("错误信息:" + error);
                        }
                    }
                }
            }

            for (int i = keys.length; codeValues != null && i < values.length; i++) {
                values[i] = codeValues[i - keys.length];
            }
            codeValues = values;
        }

        if (mOnClick == 1) {
            getYNController();
            //发送http请求
            mYNController.sendMessage(this, "onSuccessResult", onClickValue, codeValues);
        } else if (mOnClick == 2) {
            //点击事件被拦截掉了
            if (mOnClickInterceptListener != null && mOnClickInterceptListener.onClickIntercept(mPosition, mData.toString())) {
                return mYNController;
            }
            //activity 跳转
            ActivityIntent.startActivity(onClickValue, mContext, codeValues);
            dealEnd((Activity) mContext, mHttpSuccess);
        } else {
            ToastUtil.showNormalMessage("请在控件设置好app:onClick");
        }
        return mYNController;
    }

    public RemindAlertDialog getRemindAlertDialog() {
        return mRemindAlertDialog;
    }

    @Override
    public void onNewNetworkTask(HttpExecute.NetworkTask backTask) {
        //设置回调错误的接口
        backTask.backTask.methodError = "onFailResult";
        if (mOnBackListener != null) {
            String keys[] = mOnBackListener.getHttpKey();
            if (keys != null && keys.length > 0) {
                backTask.keys = keys;
            }
            mOnBackListener.onNetworkTask(mView, mPosition, backTask);
        }
    }

    public void setShowProgress(boolean is) {
        mShowProgress = is;
    }

    public void onSuccessResult(String result) {
        if (mOnBackListener != null) {
            mOnBackListener.onHttpSuccess(mView, mPosition, result);
        }
        closeTopProgress();
    }

    public void getYNController() {
        if (mYNController == null) {
            if (mBaseFragment != null) {
                mYNController = new YNController(mBaseFragment, YJNView.this);
            } else {
                mYNController = new YNController(YJNView.this, (YNCommonActivity) mContext);
            }
        }
        mYNController.setShowProgress(mShowProgress);
        mYNController.showError(mShowErrorView);
    }

    private void closeTopProgress() {
        if (mBaseFragment != null) {
            mBaseFragment.closeTopProgress();
        } else {
            ((YNCommonActivity) mContext).closeTopProgress();
        }
    }

    public void onFailResult(String result) {
        if (mOnBackListener != null) {
            mOnBackListener.onHttpFail(mView, mPosition, result);
        }
        closeTopProgress();
    }


    protected void showRemindBox(String[] button, Object message, String title, int icon, int type) {
        if (((Activity) mContext).isFinishing()) return;
        if (mRemindAlertDialog == null) {
            mRemindAlertDialog = new RemindAlertDialog(mContext);
            mRemindAlertDialog.setOnKeyListener(this);
        }
        mRemindAlertDialog.setType(type);
        try {
            mRemindAlertDialog.show(button, title, message, icon, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnBackListener.backRemindAlertDialog(mRemindAlertDialog);
    }

    public void showErrorView(boolean is) {
        mShowErrorView = is;
    }

    public Object getData() {
        return mData;
    }

    public String getDataKey() {
        return mDataKey;
    }

    public String getDataKeys() {
        if (!isEmpty(mDataKeys)) {
            return mDataKeys;
        }
        return "";
    }

    public String[] getSetDataNames() {
        return getSetDataNames(getDataKey(), getDataKeys().split(","));
    }


    public static String[] getSetDataNames(String setDataName, String[] setDataNames) {
        String keys[] = new String[0];
        if (!isEmpty(setDataName)) {
            keys = setDataName.split("\\.");
        } else if (setDataNames != null && setDataNames.length != 0) {
            for (String setDataName1 : setDataNames) {
                String key[] = getSetDataNames(setDataName1, null);
                if (key != null && key.length != 0) {
                    keys = key;
                    break;
                }
            }
        }
        return keys;
    }

    public int getOnClick() {
        return mOnClick;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public boolean onRemindItemClick(int position, int type) {
        if (position == RemindAlertDialog.RIGHTBUTTON) {
            if (!mOnBackListener.onItemClick(mView, position, mData)) {
                startClick(mOnBackListener);
            }
        }
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, int type, KeyEvent event) {
        return false;
    }


}
