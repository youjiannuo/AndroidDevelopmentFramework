package com.yn.framework.model;

import com.yn.framework.system.StringUtil;

/**
 * Created by youjiannuo on 16/12/20
 */
public class UserShareModel {

    private String value1;
    private String value2;
    private String value3;

    public UserShareModel() {

    }

    public UserShareModel(String value1, String value2, String value3) {
        setValue1(value1);
        setValue2(value2);
        setValue3(value3);
    }


    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue1() {
        return StringUtil.getString(value1);
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }
}
