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
public @interface Layout {


    int layoutId();

    int swipeRefreshLayoutId() default 0;

    int[] httpId() default {};

    String[] values() default {};

    String[] values1() default {};

    String[] values2() default {};

    String[] values3() default {};
}
