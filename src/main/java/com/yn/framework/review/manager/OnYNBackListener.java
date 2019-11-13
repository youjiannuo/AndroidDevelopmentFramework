package com.yn.framework.review.manager;

import android.view.View;

import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;

/**
 * Created by youjiannuo on 16/3/17.
 */
public class OnYNBackListener implements OnBackListener {

    public View mButton;
    public boolean isButton = true;

    @Override
    public boolean checkParams() {
        return false;
    }

    @Override
    public String[] getHttpValue() {
        return new String[0];
    }

    @Override
    public String[] getHttpKey() {
        return new String[0];
    }

    @Override
    public Object[] getTitleAndMsgValue() {
        return new Object[0];
    }

    @Override
    public String[] getButtonString() {
        return new String[0];
    }

    @Override
    public void backRemindAlertDialog(RemindAlertDialog dialog) {

    }

    @Override
    public boolean onItemClick(View view, int position, Object data) {
        return false;
    }

    @Override
    public void onNetworkTask(View view, int position, HttpExecute.NetworkTask task) {

    }

    @Override
    public void onHttpSuccess(View view, int position, Object data) {

    }

    @Override
    public void onHttpFail(View view, int position, Object data) {

    }


}
