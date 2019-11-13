package com.yn.framework.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.system.SystemUtil;

/**
 * Created by youjiannuo on 2019/1/5.
 * Email by 382034324@qq.com
 */
public class TopBarHeadView extends View {
    public TopBarHeadView(Context context) {
        super(context);
    }

    public TopBarHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }


    public void init() {
        if (((YNCommonActivity) getContext()).isStatusBar()) {
            SystemUtil.setTranslucentStatus(((YNCommonActivity) getContext()), true);
            setVisibility(VISIBLE);
            if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                params.height = (int) SystemUtil.getStatusHeight();
                setLayoutParams(params);
            } else {
                LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) getLayoutParams();
                params.height = (int) SystemUtil.getStatusHeight();
                setLayoutParams(params);
            }
        }
    }


}
