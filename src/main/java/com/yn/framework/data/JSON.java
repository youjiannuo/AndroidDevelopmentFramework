package com.yn.framework.data;


import com.google.gson.JsonParseException;
import com.yn.framework.cache.RedisItem;
import com.yn.framework.exception.YNJSONException;
import com.yn.framework.model.JSONItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import lombok.Data;

import static com.yn.framework.cache.RedisClient.getItem;
import static com.yn.framework.cache.RedisClient.putItem;
import static com.yn.framework.system.StringUtil.isEmpty;
import static com.yn.framework.system.StringUtil.md5;

@Data
public class JSON {

    private String html = "";
    private String tag = "";
    private JSONObject jsonObject = null;
    private JSONArray jsonArray = null;
    private int n = 0;  //有多少行数据
    private int i = -1;  //当前第几个
    private int n_n = 0; //一行有多少个数据
    private int byteSize = -1;
    private Map<String, String> map = null;

    public JSON() {
        jsonObject = new JSONObject();
    }

    public JSON(String html, String tag) {
        this.html = html;
        this.tag = tag;
        byteSize = html.getBytes().length;
        try {
            JSONObject jo1 = getJSONObjectA(new JSONArray(dealString(html)), 0);
            jsonArray = new JSONArray(jo1.get(tag).toString());
            n = jsonArray.length();
            jsonObject = getJSONObjectA(jsonArray, 0);
            n_n = jsonObject.length();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        if (jsonObject != null) {
            jsonObject.remove(key);
        }
    }

    public JSON(String html) {
        try {
            byteSize = html.getBytes().length;
            jsonArray = new JSONArray(dealString(html));
            jsonObject = getJSONObjectA(jsonArray, 0);
            n_n = jsonObject.length();
            n = jsonArray.length();
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }

    public JSON(String[] array) {
        try {
            jsonArray = new JSONArray(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getByteSize(){
        return byteSize;
    }

    public void toKeyAndValue(List<String> keys, List<String> values) {
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = getString(key);
            keys.add(key);
            values.add(value);
        }
    }

    /*
     * 检查字符串
     */
    private String dealString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') continue;
            if (s.charAt(i) != '[') {
                s = '[' + s;
            }
            break;
        }
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == ' ') continue;
            if (s.charAt(i) != ']') {
                s += ']';
            }
            break;

        }
        return s;

    }


    public long getItemLong(String key) {
        try {
            return jsonObject == null ? 0 : jsonObject.getLong(key);
        } catch (JSONException e) {
//            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return 0;
    }

    public String getArrayIndex(int i) {
        try {
            return jsonArray.get(i).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }


    public double getItemFloat(String key) {
        try {
            return jsonObject == null ? 0.0f : jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return 0.0f;
    }

    public boolean getItemBoolean(String key) {
        try {
            return jsonObject != null && jsonObject.getBoolean(key);
        } catch (JSONException e) {
//            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return false;
    }

    public int getItemInt(String key) {
        try {
            return jsonObject == null ? 0 : jsonObject.getInt(key);
        } catch (JSONException e) {
            new YNJSONException(e).throwException();
        }
        return 0;
    }

    public double getItemDouble(String key) {
        try {
            return jsonObject == null ? 0 : jsonObject.getDouble(key);
        } catch (JSONException e) {
//            e.printStackTrace();
//            new YNJSONException(e).throwException();
        }
        return 0;
    }


    public Object getItemObject(String key) {
        try {
            return jsonObject == null ? "" : jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return null;
    }

    public JSON(String keys[], String values[]) {

        if (keys == null || values == null || keys.length != values.length)
            return;

        map = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        jsonObject = new JSONObject(map);

    }

    public JSON(List<String> keys, List<String> values) {
        if (keys == null || values == null || keys.size() != values.size()) {
            return;
        }

        map = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        jsonObject = new JSONObject(map);
    }

    public static boolean isGoodJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JsonParseException | JSONException e) {
            System.out.println("bad json: " + json);
        }
        return false;
    }

    public void put(String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
    }

    public String toString() {
        return jsonObject == null ? "" : jsonObject.toString();

    }

    public String getAllString() {
        if (jsonArray == null) return "";
        return jsonArray.toString();
    }

    /*
     * 多少列
     */
    public int Row() {
        return n_n;
    }

    /*
     * 多少行
     */
    public int size() {
        return n;
    }

    public String getHtml() {
        return html;
    }

    public String getString(String key) {
        try {
            return jsonObject == null ? "" : jsonObject.get(key).toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return "";
    }

    public JSONObject getJsonObject(String key) {
        try {
            return jsonObject == null ? new JSONObject() : jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Object getObject(String key) {
        try {

            return jsonObject == null ? new JSONObject() : jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public JSONArray getArray(String s) {
        try {
            return jsonObject.getJSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public String getItemString(String key) {
        try {
            return jsonObject == null ? "" : jsonObject.getString(key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return "";
    }

    public String getItemString() {
        String value = jsonObject == null ? "" : jsonObject.toString();
        if (!isEmpty(value) && !"{}".equals(value)) {
            return value;
        }
        if (jsonArray != null) {
            try {
                return jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public Object getStringObject(String key) {
        try {
            return jsonObject == null ? "" : jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return new Object();
    }

    /**
     * 修改数据
     *
     * @param key   修改的键
     * @param value 需要修改的值
     */
    public JSON changeData(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return this;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultInt) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            // TODO: handle exception
//            e.printStackTrace();
//            new YNJSONException(e).throwException();
        }
        return defaultInt;
    }

    public boolean next() {

        if ((++i) >= n) return false;
        try {
            jsonObject = getJSONObjectA(jsonArray, i);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }

        return true;
    }

    public void moveFirst() {
        i = -1;
    }


    public String getRowString(int index) {
        try {
            return getJSONObjectA(jsonArray, index).toString();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }

        return "";

    }

    public JSONObject getRowObject(int index) {
        try {
            return getJSONObjectA(jsonArray, index);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            new YNJSONException(e).throwException();
        }
        return new JSONObject();
    }

    /*
     * 将传入的标记返回出对应的字符串数组
     */
    public String[] getArrayString(String keys[]) {
        return getArrayString(keys, keys == null ? 0 : keys.length);
    }

    private String[] getArrayString(String keys[], int n) {
        String values[] = new String[n];

        for (int i = 0; i < keys.length; i++) {
            values[i] = getString(keys[i]);
        }

        return values;
    }

    /**
     * @param keys
     * @param values
     * @return
     */
    public String[] getArrayString(String keys[], String values[]) {
        String result[] = getArrayString(keys,
                (keys == null ? 0 : keys.length) +
                        (values == null ? 0 : values.length));

        for (int i = 0; i < values.length; i++)
            result[keys.length + i] = values[i];

        return result;

    }


    public JSONObject getJSONObjectA(JSONArray jSONArray, int n)
            throws JSONException {
        try {
            return (JSONObject) jSONArray.get(n);
        } catch (ClassCastException e) {
            System.out.println("JSON异常数据：" + jsonArray.get(n).toString());
            new YNJSONException(e).throwException();
        }
        return new JSONObject();
    }

    public static String getString(String[] keys, String[] values) {
        return new JSON(keys, values).toString();
    }


    public String getStrings(String key) {
        if (key == null || key.length() == 0) return "";
        String keys[] = key.split("\\.");
        return getStrings(keys);
    }


    public String getStrings(String keys[]) {
        String result = getString(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            result = json(result).getString(keys[i]);
        }
        return result;
    }


//    public String getStrings(String keys[]) {
//        JSONObject json = jsonObject;
//        for (String key : keys) {
//            Object obj = null;
//            try {
//                obj = json.get(key);
//            } catch (JSONException e) {
////                e.printStackTrace();
//            }
//            if (obj == null) {
//                return "";
//            }
//            if (obj instanceof JSONObject) {
//                json = (JSONObject) obj;
//            } else if (obj instanceof JSONArray) {
//                try {
//                    json = ((JSONArray) obj).getJSONObject(0);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//
//            }
//        }
//        return json != null ? json.toString() : "";
//    }

//    public String getStrings(String keys[]) {
//        return valueOf(getStrings1(jsonObject, keys));
//    }
//
//    public Object getStringObjects(String keys[]) {
//        return getStrings1(jsonObject, keys);
//    }
//
//    private Object getStrings1(JSONObject jsonObject, String keys[]) {
//        Object result = jsonObject;
//        try {
//            for (int i = 0; i < keys.length; i++) {
//                if (!jsonObject.has(keys[i])) {
//                    return "";
//                }
//                Object value = jsonObject.get(keys[i]);
//                result = value;
//                if (value instanceof JSONObject) {
//                    jsonObject = (JSONObject) value;
//                } else if (value instanceof JSONArray) {
//                    if (i != keys.length - 1) {
//                        JSONArray jsonArray = (JSONArray) value;
//                        jsonObject = jsonArray.getJSONObject(0);
//                    }
//                } else {
//                    return value;
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return result;
//    }


    public static JSON json(String s) {
        String key = md5(s);
        RedisItem redisItem = getItem(key);
        JSONItem jsonItem;
        if (redisItem == null || !(redisItem instanceof JSONItem)) {
            jsonItem = new JSONItem(new JSON(s));
            putItem(key, jsonItem);
        } else {
            jsonItem = (JSONItem) redisItem;
        }
        return jsonItem.getJson();
    }

}
