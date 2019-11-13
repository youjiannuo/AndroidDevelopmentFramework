package com.yn.framework.review.manager;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.yn.framework.R;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;
import com.yn.framework.review.OnCheckParams;

import static com.yn.framework.review.manager.Util.dealRemind;
import static com.yn.framework.review.manager.Util.getClickTextViews;
import static java.lang.System.arraycopy;

/**
 * Created by youjiannuo on 16/4/26.
 */
public class YNClickBack extends OnYNBackListener {

    private OnCheckParams[] mOnCheckParams;
    public OnBackListener mOnBackListener;
    private YNCommonActivity mActivity;
    private String mClickViewId;
    private int mHttpSuccess;
    private String mRemindTitleAndMsg[] = new String[0];
    private String mRemindButtons[] = new String[0];


    public YNClickBack(Context context,
                       TypedArray array) {
        mClickViewId = array.getString(R.styleable.YNView_onClickValueViewId);
        String remind = array.getString(R.styleable.YNView_remind);
        if (context instanceof YNCommonActivity) {
            mActivity = (YNCommonActivity) context;
        }
        mHttpSuccess = array.getInteger(R.styleable.YNView_httpSuccess, 0);
        Object result[] = dealRemind(remind);
        if (result != null) {
            mRemindButtons = (String[]) result[1];
            mRemindTitleAndMsg = (String[]) result[0];
        }
    }

    public void setOnBackListener(OnBackListener l) {
        mOnBackListener = l;
    }

    @Override
    public boolean checkParams() {
        if (mOnCheckParams == null) {
            if (mActivity != null) {
                mOnCheckParams = getClickTextViews(mActivity.getShowView(), mClickViewId);
            } else {
                mOnCheckParams = new OnCheckParams[0];
            }
        }
//        if (mOnCheckParams.length == 0) return false;
        for (OnCheckParams params : mOnCheckParams) {
            if (params.checkParams()) {
                return true;
            }
        }
        if (mOnBackListener != null) {
            return mOnBackListener.checkParams();
        }
        return false;
    }

    @Override
    public String[] getHttpValue() {
        String[] array = new String[0];
        if (mOnBackListener != null) {
            String extra[] = mOnBackListener.getHttpValue();
            array = new String[extra.length + mOnCheckParams.length];
            arraycopy(extra, 0, array, mOnCheckParams.length, extra.length);
            for (int i = 0; i < mOnCheckParams.length; i++) {
                array[i] = mOnCheckParams[i].getTextString();
            }
        }

        return array;
    }

    @Override
    public String[] getHttpKey() {
        if (mOnBackListener != null) {
            return mOnBackListener.getHttpKey();
        }
        return new String[0];
    }

    @Override
    public Object[] getTitleAndMsgValue() {
        if (mOnBackListener != null) {
            Object objects[] = mOnBackListener.getTitleAndMsgValue();
            if (objects != null && objects.length > 0) {
                return objects;
            }
        }
        return mRemindTitleAndMsg;

    }

    @Override
    public String[] getButtonString() {
        if (mOnBackListener != null) {
            String[] s = mOnBackListener.getButtonString();
            if (s != null && s.length > 0) {
                return s;
            }
        }
        return mRemindButtons;
    }

    @Override
    public void backRemindAlertDialog(RemindAlertDialog dialog) {
        if (mOnBackListener != null) {
            mOnBackListener.backRemindAlertDialog(dialog);
        }
    }

    @Override
    public boolean onItemClick(View view, int position, Object data) {
        if (mOnBackListener != null) {
            return mOnBackListener.onItemClick(view, position, data);
        }
        return false;
    }

    @Override
    public void onHttpSuccess(View view, int position, Object data) {
        if (mOnBackListener != null) {
            mOnBackListener.onHttpSuccess(view, position, data);
        }
        dealEnd(mActivity, mHttpSuccess);
    }

    private static void finish(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onHttpFail(View view, int position, Object data) {
        if (mOnBackListener != null) {
            mOnBackListener.onHttpFail(view, position, data);
        }
    }

    @Override
    public void onNetworkTask(View v, int position, HttpExecute.NetworkTask task) {
        if (mOnBackListener != null) {
            mOnBackListener.onNetworkTask(v, position, task);
        }
    }

    static void dealEnd(Activity activity, int httpSuccess) {
        switch (httpSuccess) {
            case 2:
                finish(activity);
                break;
            case 2 | 4:
                finish(activity);
                break;
        }
    }


}
