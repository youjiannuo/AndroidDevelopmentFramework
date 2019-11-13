package com.yn.framework.review;

import android.content.Context;
import android.util.AttributeSet;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 18/5/17.
 * Email by 382034324@qq.com
 */
public class YNNullTextView extends YNTextView {
    public YNNullTextView(Context context) {
        super(context);
    }

    public YNNullTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String getTextValue(String value) {
        setVisibility(isEmpty(value) ? GONE : VISIBLE);
        return super.getTextValue(value);
    }
}
