package com.yn.framework.login;

import com.yn.framework.R;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.controller.BackTask;
import com.yn.framework.controller.BaseController;

/**
 * Created by youjiannuo on 16/7/15
 */
public class LoginController extends BaseController {

    protected OnPwdErrorListener mOnPwdErrorListener;

    public LoginController(Object methodObj, YNCommonActivity activity) {
        super(methodObj, activity);
    }

    /**
     * @param id     账号
     * @param pwd    密码
     * @param openid 第三方登录的openId
     */
    public void login(String id, String pwd, String openid) {
        sendMessage(R.array.yn_login, id, pwd);
    }

    @Override
    public Object visitSuccess(Object object, BackTask backTask) {
//        JSON json = new JSON(object.toString());
//        UserSharePreferences.saveToken(json.getString("token"));
        return true;
    }

    @Override
    public Object visitFail(Object obj, BackTask backTask) {
        return false;
    }

    public void setOnPwdErrorListener(OnPwdErrorListener l) {
        mOnPwdErrorListener = l;
    }


    public interface OnPwdErrorListener {
        void onError();
    }


}
