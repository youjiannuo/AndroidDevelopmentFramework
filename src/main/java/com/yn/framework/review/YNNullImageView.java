package com.yn.framework.review;

import android.content.Context;
import android.util.AttributeSet;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 18/5/17.
 * Email by 382034324@qq.com
 */
public class YNNullImageView extends YNImageView {
    public YNNullImageView(Context context) {
        super(context);
    }

    public YNNullImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageData(String url) {
        if (isEmpty(url)) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        super.setImageData(url);
    }
}
