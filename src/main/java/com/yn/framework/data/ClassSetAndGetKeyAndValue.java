package com.yn.framework.data;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassSetAndGetKeyAndValue {

    private OnGetCallBack mGetCall;
    private OnSetCallBack mSetCall;
    private Class<?> mClass;
    private Object mObj;

    private ClassSetAndGetKeyAndValue(Class<?> cs) {
        mClass = cs;
    }

    public ClassSetAndGetKeyAndValue(Object obj, OnGetCallBack call) {
        mObj = obj;
        mGetCall = call;
    }

    public ClassSetAndGetKeyAndValue(Class<?> cs, OnSetCallBack call) {
        this(cs);
        mSetCall = call;
    }

    /**
     * 获取集合
     *
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<Object> startRunDatas() throws InstantiationException, IllegalAccessException {
        boolean is = true;
        List<Object> lists = new ArrayList<Object>();
        while (is) {
            mObj = mClass.newInstance();
            Object obj = startRunData();
            if (obj == null) {
                return lists;
            }
            lists.add(obj);
        }
        return lists;
    }

    /**
     * 加载数据
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object startRunData() throws InstantiationException, IllegalAccessException {
        boolean is = true;
        if (mObj == null)
            mObj = mClass.newInstance();
        Class<? extends Object> c = mObj.getClass();

        for (; c != Object.class; c = c.getSuperclass()) {
            Field fs[] = c.getDeclaredFields();
            for (Field f : fs) {
                String name = f.getName();
                String value = "";
                f.setAccessible(true);
                if (mSetCall != null) {
                    Object obj = mSetCall.setCall(name, is);
                    is = false;
                    if (obj == null) return null;
                    if (obj instanceof String) {
                        String v = obj.toString();
                        if (v.length() != 0)
                            f.set(mObj, v);
                        continue;
                    } else {
                        f.set(mObj, obj);
                    }
                } else {
                    if (f.get(mObj) instanceof String) {
                        value = (String) f.get(mObj);
                    }
                }
                if (mGetCall != null) {
                    mGetCall.onCall(name, value);
                }

            }
        }
        return mObj;
    }

    public static void getObjectListKeyAndValue(Object obj, final List<String> keys, final List<String> values) throws InstantiationException, IllegalAccessException {
        new ClassSetAndGetKeyAndValue(obj, new OnGetCallBack() {

            @Override
            public void onCall(String key, String value) {
                // TODO Auto-generated method stub
                if (keys != null)
                    keys.add(key);
                if (values != null)
                    values.add(value);
            }
        }).startRunData();
    }


    public interface OnSetCallBack {
        /**
         * @param key     需要获取值的key
         * @param isStart 是否是重新开始的
         * @return
         */
        public Object setCall(String key, boolean isStart);
    }

    public interface OnGetCallBack {
        public void onCall(String key, String value);
    }
}
