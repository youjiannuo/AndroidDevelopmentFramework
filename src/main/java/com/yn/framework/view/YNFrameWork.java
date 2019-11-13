package com.yn.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yn.framework.R;


/**
 * @author yjn
 *         整体布局框架
 */
public class YNFrameWork extends FrameLayout {


    public YNFrameWork(Context context) {
        super(context);
    }

    public YNFrameWork(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //设置操作界面的位置
    public void setOperationPosition(int top) {
        View view = findViewById(R.id.operation);
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.topMargin = top;
        view.setLayoutParams(params);
    }


}
