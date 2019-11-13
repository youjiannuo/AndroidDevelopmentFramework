package com.yn.framework.controller;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.SystemUtil;
import com.yn.framework.view.YNTextWatcher;

/**
 * Created by youjiannuo on 2018/11/29.
 * Email by 382034324@qq.com
 */
public class EditController {

    private EditText mEditText;
    private TextView mNumTextView;
    private int mMax;

    public EditController(EditText editText, TextView textView, int max) {
        mEditText = editText;
        mNumTextView = textView;
        mMax = max;
    }

    public void init() {
        mEditText.addTextChangedListener(new YNTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mNumTextView.setText(mEditText.getText().length() + "/" + mMax);
                if (mEditText.getText().length() > mMax) {
                    mNumTextView.setTextColor(Color.RED);
                } else {
                    mNumTextView.setTextColor(0xFF999999);
                }
            }
        });
    }

    public void setHintText(String hintText) {
        mEditText.setHint(hintText);
    }

    public String getInputText() {
        if (mEditText.getText().length() > mMax) {
            ToastUtil.showNormalMessage("输入的字数不可以超过" + mMax + "个");
            return null;
        } else if (mEditText.getText().length() == 0) {
            ToastUtil.showNormalMessage("请输入内容");
            SystemUtil.showInputMethodManagerNow(mEditText, null);
            return null;
        }
        return mEditText.getText().toString();
    }
}
