package com.yn.framework.review;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.yn.framework.R;
import com.yn.framework.file.IDCard;
import com.yn.framework.review.manager.Util;
import com.yn.framework.system.SystemUtil;

import static com.yn.framework.remind.ToastUtil.showFailMessage;
import static com.yn.framework.system.StringUtil.isEmpty;
import static com.yn.framework.system.StringUtil.isPhoneNum;

/**
 * Created by youjiannuo on 16/4/26.
 * 編輯框
 */
public class YNEditText extends EditText implements OnCheckParams {
    //编辑类型
    private int mInputType = 0;
    private String mCheckParameters;
    private boolean isEmpty;

    public YNEditText(Context context) {
        super(context);
    }

    public YNEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mInputType = array.getInt(R.styleable.YNView_inputType, 0);
        mCheckParameters = array.getString(R.styleable.YNView_checkParameters);
        isEmpty = array.getBoolean(R.styleable.YNView_is_empty, false);
        array.recycle();

    }

    public YNEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean checkParams() {
        String text = getText().toString().trim();
        //身份证
        switch (mInputType) {
            case 1:
                String result = IDCard.IDCardValidate(text);
                if (!isEmpty(result)) {
                    showFailMessage(result);
                    SystemUtil.showInputMethodManagerEditText(this);
                    return true;
                }
                return false;
            case 2:
                if (!isPhoneNum(text)) {
                    showFailMessage("请输入正确的手机号");
                    SystemUtil.showInputMethodManagerEditText(this);
                    return true;
                }
                return false;
            default:
                return checkoutParameters(text);
        }
    }

    @Override
    public String getTextString() {
        return getText().toString().trim();
    }

    private boolean checkoutParameters(String text) {

        int length = text.length();
        if (!isEmpty && length == 0) {
            showFailMessage(getHint().toString());
            SystemUtil.showInputMethodManagerEditText(this);
            return true;
        }
        if (isEmpty(mCheckParameters)) return false;
        String resluts[] = mCheckParameters.split(",");
        for (String result : resluts) {
            if (result.contains("max:")) {
                if (!checkoutLength(length, result, -1, "max:")) {
                    return true;
                }
            } else if (result.contains("min:")) {
                if (!checkoutLength(length, result, 1, "min:")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkoutLength(int length, String data, int opeartion, String key) {
        String params[] = new String[2];
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == ' ') {
                params[0] = data.substring(0, i);
                params[1] = data.substring(i + 1, data.length());
                break;
            }
        }


        if (params.length == 0) return true;
        if (params.length == 1) {
            showFailMessage("没有添加Commit");
            return false;
        }
        String parameter = params[0];
        int a = Util.getInt(getValue(parameter, key));
        String commit = getValue(params[1], "commit:");
        if (length * opeartion < a * opeartion) {
            showFailMessage(commit);
            return false;
        }
        return true;
    }


    private String getValue(String data, String key) {
        int index = data.indexOf(key) + key.length();
        if (index == -1) return "";
        return data.substring(index, data.length());
    }


}
