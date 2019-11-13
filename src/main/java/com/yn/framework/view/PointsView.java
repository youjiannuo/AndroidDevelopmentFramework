package com.yn.framework.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yn.framework.system.SystemUtil;

import lombok.Data;

/**
 * Created by youjiannuo on 2019/3/30.
 * Email by 382034324@qq.com
 */
public class PointsView extends LinearLayout {

    private Params mParams;

    public PointsView(Context context) {
        super(context);
    }

    public PointsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void setParams(Params params) {
        mParams = params;
        for (int i = 0; i < params.size; i++) {
            createPointView();
        }
        getChildAt(0).setSelected(true);
    }

    public void setSelect(int index) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(false);
        }
        getChildAt(index).setSelected(true);
    }

    private void createPointView() {
        ImageView pointView = new ImageView(getContext());
        pointView.setImageResource(mParams.drawable);
        LayoutParams params = new LayoutParams(mParams.wh, mParams.wh);
        params.leftMargin = mParams.left;
        addView(pointView, params);
        pointView.setSelected(false);
    }

    @Data
    public static class Params {
        int drawable;
        int size;
        int wh;
        int left = SystemUtil.dipTOpx(9);
    }

}
