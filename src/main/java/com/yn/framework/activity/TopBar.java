package com.yn.framework.activity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by youjiannuo on 2018/7/30.
 * Email by 382034324@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TopBar {

    int titleResourceId() default -1;

    String titleString() default "";

    boolean isCloseLeft() default false;

    int rightButtonResourceId() default -1;

    String rightButtonString() default "";
}
