package com.yn.framework.review;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yn.framework.animation.Animation;
import com.yn.framework.data.DataUtil;

/**
 * Created by youjiannuo on 2018/6/19.
 * Email by 382034324@qq.com
 */
public class YNMoneyTextView extends TextView {

    private ValueAnimator mValueAnimator;

    public YNMoneyTextView(Context context) {
        super(context);
    }

    public YNMoneyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMoney(double value) {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mValueAnimator = Animation.valueAnimator(0, (float) value, 1000, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                YNMoneyTextView.super.setText(DataUtil.getMoneyString(value));
            }
        });
    }
}
