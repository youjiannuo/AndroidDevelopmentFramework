package com.yn.framework.review;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yn.framework.R;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.system.TimeUtil;

/**
 * Created by youjiannuo on 16/4/12.
 * 具备定时
 */
public class YNTimerTextView extends YNTextView implements TimeUtil.OnTimeListener {

    private boolean mStop = false;

    private long mTime = 60;

    public YNTimerTextView(Context context) {
        super(context);
    }

    public YNTimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YNTimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnBackListener(final OnBackListener l) {
        if (l != null) {
            super.setOnBackListener(new OnBackListener() {
                @Override
                public boolean checkParams() {
                    return l.checkParams();
                }

                @Override
                public String[] getHttpValue() {
                    return l.getHttpValue();
                }

                @Override
                public String[] getHttpKey() {
                    return new String[0];
                }

                @Override
                public Object[] getTitleAndMsgValue() {
                    return l.getTitleAndMsgValue();
                }

                @Override
                public String[] getButtonString() {
                    return l.getButtonString();
                }

                @Override
                public void backRemindAlertDialog(RemindAlertDialog dialog) {
                    l.backRemindAlertDialog(dialog);
                }

                @Override
                public boolean onItemClick(View view, int position, Object data) {
                    return l.onItemClick(view, position, data);
                }

                @Override
                public void onHttpSuccess(View view, int position, Object data) {
                    l.onHttpSuccess(view, position, data);
                    startTime();
                }

                @Override
                public void onHttpFail(View view, int position, Object data) {
                    l.onHttpFail(view, position, data);
                }

                @Override
                public void onNetworkTask(View view, int position, HttpExecute.NetworkTask task) {
                    l.onNetworkTask(view, position, task);
                }
            });
        }
    }


    public void onStop() {
        mStop = true;
    }

    public void startTime() {
        startTime(0);
    }

    public void startTime(long startTime) {
        mStop = false;
        TimeUtil.startTimer((int) (startTime / 1000), 1000, 0, YNTimerTextView.this);
        setEnabled(false);
    }

    @Override
    public boolean onTime(int index) {
        if (index >= mTime) {
            setText(getContext().getString(R.string.hfh_re_send));
            setEnabled(true);
            return true;
        }
        String text = "重新发送 " + String.valueOf(60 - index) + "s";
        setText(text);
        return mStop;
    }
}
