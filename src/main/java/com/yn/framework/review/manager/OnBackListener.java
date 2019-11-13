package com.yn.framework.review.manager;

import android.view.View;

import com.yn.framework.http.HttpExecute;
import com.yn.framework.remind.RemindAlertDialog;

/**
 * Created by youjiannuo on 16/3/17.
 */
public interface OnBackListener<T> {

    boolean checkParams();

    String[] getHttpValue();

    String[] getHttpKey();

    Object[] getTitleAndMsgValue();

    String[] getButtonString();

    void backRemindAlertDialog(RemindAlertDialog dialog);

    boolean onItemClick(View view, int position, T data);

    void onNetworkTask(View view, int position, HttpExecute.NetworkTask task);

    void onHttpSuccess(View view, int position, T data);

    void onHttpFail(View view, int position, T data);


}
