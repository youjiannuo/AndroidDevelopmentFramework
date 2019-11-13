package com.yn.framework.system;

import android.annotation.TargetApi;
import android.os.Build;

import com.yn.framework.data.SerializedName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by youjiannuo on 15/10/28.
 */
public class MethodUtil {

    public static Object invoke(String clsString, String method, Class<?> paramsClass[], Object[] objects) {
        try {
            return invoke(Class.forName(clsString), method, paramsClass, objects);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invoke(Class<?> cls, String method, Class<?> paramsClass[], Object[] objects) {
        Method me;
        try {
            me = cls.getMethod(method, paramsClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return me.invoke(null, objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invoke(Object obj, String method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invoke(obj, method, (Object) null);
    }

    public static Object invoke(Object obj, String method, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class cl[] = null;
        if (params != null) {
            cl = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                cl[i] = params[i].getClass();
            }
        }
        return invoke(obj, method, params, cl);
    }

    public static Object invoke(Object obj, String method, Object[] params, Class cl[]) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (StringUtil.isEmpty(method)) return null;
        Method m = obj.getClass().getMethod(method, cl);
        return m.invoke(obj, params);
    }


    public static Object getParam(Object object, String method) {
        Class t = object.getClass();
        for (Class cl = t; cl != Object.class; cl = cl.getSuperclass()) {
            try {
                Field field;
                try {
                    field = cl.getDeclaredField(method);
                } catch (NoSuchFieldException e) {
                    continue;
                }
                if (field != null) {
                    field.setAccessible(true);
                    return field.get(object);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void getParam(Object obj, List<String> keys, List<Object> values) {
        Class t = obj.getClass();
        try {
            for (Class cl = t; cl != Object.class; cl = cl.getSuperclass()) {
                Field fields[] = cl.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    keys.add(getFiledName(field));
                    values.add(field.get(obj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldValue(Object obj, String key) {
        int index = key.indexOf(".");
        String name = key;
        if (index == 0) {
            name = key.substring(1, key.length());
        } else if (index > 0) {
            name = key.substring(0, index);
        }
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Object valueObject = field.get(obj);
            if (valueObject != null) {
                if (index <= 0) {
                    return valueObject;
                }
                return getFieldValue(valueObject, key.substring(index, key.length()));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static void getParams(Object obj, List<String> keys, List<String> values) {
        Class t = obj.getClass();
        try {
            for (Class cl = t; cl != Object.class; cl = cl.getSuperclass()) {
                Field fields[] = cl.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    keys.add(getFiledName(field));
                    Object objValue = field.get(obj);
                    String value = "";
                    if (objValue != null) {
                        value = objValue.toString();
                    }
                    values.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setParamValue(Object obj, String name, Object value, Class<?> ca) {
        try {

            Field field = ca.getDeclaredField(name);
//            for (Class c = obj.getClass(); c != Object.class; c = c.getSuperclass()) {
//                Field[] fields = c.getDeclaredFields();
//                for (int i = 0; i < fields.length; i++) {
//                    if (fields[i].getName().equals(name)) {
//                        field = fields[i];
//                        break;
//                    }
//                }
//            }

            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getFiledName(Field field) {
        String name = field.getName();
        //读取注解字段
        if (field.isAnnotationPresent(SerializedName.class)) {
            SerializedName inject = field.getAnnotation(SerializedName.class);
            name = inject.value();
        }
        return name;
    }

    public static <T> T newInstance(Class<T> cla, Object[] params, Class<?>... classParams) throws Exception {
        Constructor<T> constructor = cla.getDeclaredConstructor(classParams);
        constructor.setAccessible(true);
        return constructor.newInstance(params);
    }

}
