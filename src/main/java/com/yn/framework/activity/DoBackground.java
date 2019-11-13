package com.yn.framework.activity;

/**
 * Created by youjiannuo on 2018/7/31.
 * Email by 382034324@qq.com
 */
public @interface DoBackground {

    boolean isSingle() default false;

    boolean isProgress() default true;
}
