package com.yn.framework.review;

import android.content.Context;
import android.util.AttributeSet;

import com.yn.framework.system.StringUtil;

/**
 * Created by youjiannuo on 17/6/7.
 */

public class YNCapitalizeTextView extends YNTextView {

    public YNCapitalizeTextView(Context context) {
        super(context);
    }

    public YNCapitalizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String getTextValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }

        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
