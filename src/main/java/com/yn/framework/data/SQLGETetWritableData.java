package com.yn.framework.data;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.BuildConfig;
import com.yn.framework.system.MethodUtil;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.SystemUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * 对手机数据库的读取数据的凌乱操作,不可以被实例化，只可以继承
 */

public abstract class SQLGETetWritableData {

    public static final String TAG = "SQLGETetWritableData";

    private SQLiteOpenHelper mSqlLite = null;
    protected Cursor cursor = null;
    protected String sql = "";
    //对数据库实行操作
    protected SQLiteDatabase sqLiteDatabase = null;

    /**
     * @param context
     */
    public SQLGETetWritableData(Context context, SQLiteOpenHelper sql) {
        mSqlLite = sql;
        sqLiteDatabase = mSqlLite.getWritableDatabase();
    }

    public void CursorClose() {
        if (cursor != null) cursor.close();
    }

    public void closeConnection() {
        if (sqLiteDatabase != null) sqLiteDatabase.close();
    }

    public void close() {
        this.CursorClose();
        this.closeConnection();
    }


    /*
     * 判断数据库里面是否存在要查询的哪一张表
     * 返回true 表示存在着一张表
     */
    public boolean isTableHave(String tablename) {

        Cursor cursor = null;
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tablename.trim() + "' ";
        cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                this.CursorClose();
                return true;
            }
        }
        this.CursorClose();
        return false;
    }

    /*
     * 判断手机数据库里面是否存对应的类名
     * 返回true表示存在这个数据
     *
     */
    public boolean isHaveData(String sql) {
        boolean is = false;
        cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToNext()) return is = true;
        CursorClose();
        return is;
    }

    public String getString(String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    /*
     * 插入数据
     */
    public void insert(ContentValues contentValues) {
        sqLiteDatabase.insert(getTableName(), null, contentValues);
    }

    public void insert(Object obj) {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        MethodUtil.getParams(obj, keys, values);
        insert(keys, values);
    }

    public void insert(List<String> keys, List<String> values) {
        ContentValues contentValues = new ContentValues();
        if (keys == null || keys == null || keys.size() != values.size()) {
            if (!BuildConfig.ENVIRONMENT) {
                ToastUtil.showNormalMessage("插入的键值不一样");
            }
            return;
        }
        for (int i = 0; i < keys.size(); i++) {
            if (!StringUtil.isEmpty(values.get(i))) {
                contentValues.put(keys.get(i), values.get(i));
            }
        }
        insert(contentValues);
    }

    public void insert(String keys[], String values[]) {
        ContentValues contentValues = new ContentValues();
        if (keys == null || values == null || keys.length != values.length)
            return;
        for (int i = 0; i < keys.length; i++) {
            contentValues.put(keys[i], values[i]);
        }
        insert(contentValues);
    }

    public <T> void update(T t, T params) {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> whereKeys = new ArrayList<>();
        List<String> whereValues = new ArrayList<>();
        MethodUtil.getParams(t, keys, values);
        MethodUtil.getParams(params, whereKeys, whereValues);

        for (int i = 0; i < whereValues.size(); i++) {
            if (StringUtil.isEmpty(whereValues.get(i))) {
                whereValues.remove(i);
                whereKeys.remove(i);
                i--;
            }
        }
        update(toArrayString(keys), toArrayString(values), toArrayString(whereKeys), toArrayString(whereValues));
    }

    private String[] toArrayString(List<String> data) {
        String[] arr = new String[data.size()];
        data.toArray(arr);
        return arr;
    }


    public void update(String key[], String value[], String whereKey, String whereValue) {
        update(key, value, new String[]{whereKey}, new String[]{whereValue});
    }

    public void update(String key, String value, String whereKey, String whereValue) {
        update(new String[]{key}, new String[]{value}, new String[]{whereKey}, new String[]{whereValue});
    }


    public void update(String keys[], String values[], String wherekey[], String whereValus[]) {
        ContentValues contentValues = new ContentValues();
        if (keys == null || values == null || keys.length != values.length)
            return;
        for (int i = 0; i < keys.length; i++) {
            if (!StringUtil.isEmpty(values[i])) {
                contentValues.put(keys[i], values[i]);
            }
        }
        try {
            sqLiteDatabase.update(getTableName(), contentValues, makeWhere(wherekey), whereValus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 删除数据
     */
    public void delete(String table, String where, String whereArsg[]) {
        for (int i = 0; i < whereArsg.length; i++) {
            System.out.println("where = " + whereArsg[i]);
        }
        sqLiteDatabase.delete(table, where, whereArsg);
    }

    public void delete(String table, String whereKeys[], String whereArsg[]) {
        String s = makeWhere(whereKeys);
        Log.i(TAG, s);
        delete(table, s, whereArsg);
    }


//    public void createTable(String sql) {
//        List<String> lists = new ArrayList<String>();
//        lists.add(sql);
//        sqllite.createTable(sqLiteDatabase, lists);
//    }

    public String[] AddTagToArrayInfo(String info[], String tag) {
        String INFO[] = new String[info.length + 1];
        for (int i = 0; i < info.length; i++) {
            INFO[i] = info[i];
        }
        INFO[INFO.length - 1] = tag;

        return INFO;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public <T> List<T> getDataObject(T t, Class<?> cs) {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        MethodUtil.getParams(t, keys, values);
        String[] keyStrings = new String[keys.size()];
        String[] valueStrings = new String[values.size()];
        keys.toArray(keyStrings);
        values.toArray(valueStrings);
        cursor = rawQuery(getTableName(), null, keyStrings, valueStrings);
        List<T> data = new ArrayList<>();
        try {
            data = (List<T>) getCursor(cs, cursor);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return data;
    }


    public <T> List<T> getDataObject(String sql, Class<?> cs) throws InstantiationException, IllegalAccessException {
        return (List<T>) getCursor(cs, rawQuery(sql));
    }

    private List<Object> getCursor(Class<?> cs, final Cursor cur) throws InstantiationException, IllegalAccessException {
        return new ClassSetAndGetKeyAndValue(cs, new ClassSetAndGetKeyAndValue.OnSetCallBack() {

            @Override
            public String setCall(String key, boolean isStart) {
                // TODO Auto-generated method stub
                if (isStart && !cur.moveToNext()) {
                    cur.close();
                    return null;
                }
                int index = cur.getColumnIndex(key);
                String value = "";
                if (index != -1) {
                    value = cur.getString(index);
                }
                return value == null ? "" : value;
            }
        }).startRunDatas();
    }

    public Cursor rawQuery(String[] getArays, String[] whereArays, String[] whereValues, String order) {
        return rawQuery(getTableName(), getArays, whereArays, whereValues, order);
    }


    public Cursor rawQuery(String tableName, String[] getArays, String[] whereArays, String[] whereValues) {
        return rawQuery(tableName, getArays, whereArays, whereValues, "");
    }

    /*
         * 查询数据
         */
    public Cursor rawQuery(String tablename, String[] getArays, String[] whereArays, String[] whereValues, String order) {
        StringBuilder sb = new StringBuilder("select ");
        sb.append(makeQueryGet(getArays))
                .append(" from ")
                .append(tablename)
                .append("  ")
                .append(makeQueryWhere(whereArays, whereValues));
        if (!StringUtil.isEmpty(order)) {
            sb.append(" order by ").append(order);
        }
        SystemUtil.printlnInfo("sql = "+sb.toString());
        return rawQuery(sb.toString());
    }

    public Cursor rawQuery(String sql) {
        return sqLiteDatabase.rawQuery(sql, null);
    }

    public void execSql(String sql) {
        sqLiteDatabase.execSQL(sql);
    }


    //	private String makeInsertWhere(String []WhereArays,String []whereValues){
    //		if(whereValues == null || whereValues.length == 0)  return "values()";
    //		String result = StringTool.ArrayToString(WhereArays, ',', "(", ")", " ")+" values"+
    //		                        StringTool.ArrayToString(whereValues, ',', "(", ")", "'");
    //		return result;
    //	}
    private String makeQueryGet(String[] getArays) {
        if (getArays == null) return " * ";
        else return ArrayToString(getArays, ',', "", "", "");
    }

    private String makeQueryWhere(String[] whereArays, String[] whereValues) {

        String result = makeWhereNew(whereArays, whereValues, "'");

        return result.equals("") ? result : "where " + result;
    }

    private String makeWhere(String[] whereArays) {
        String whereValues[] = new String[whereArays.length];
        for (int i = 0; i < whereArays.length; i++) {
            whereValues[i] = "?";
        }
        return makeWhereNew(whereArays, whereValues, " ");
    }

    @Deprecated
    private String makeWheres(String[] whereArays, String[] whereValues, String c) {
        if (whereArays == null || whereValues == null) return "";

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < whereArays.length; i++) {
            if (whereArays[i] == null || whereArays[i].length() == 0 || whereValues[i] == null || whereValues[i].length() == 0)
                continue;
            result.append(whereArays[i]).append("  =  ").append(c).append(whereValues[i]).append(c);
            if (i == whereArays.length - 1) break;
            result.append(" and ");
        }
        return result.toString();
    }

    private String makeWhereNew(String[] whereArays, String[] whereValues, String c) {
        if (whereArays == null || whereValues == null) return "";

        StringBuilder result = new StringBuilder();
        boolean is = true;
        for (int i = 0; i < whereArays.length; i++) {
            if (whereArays[i] == null ||
                    whereArays[i].length() == 0 ||
                    whereValues[i] == null ||
                    whereValues[i].length() == 0) {
                continue;
            }
            if (!is) {
                result.append(" and ");
            }
            is = false;
            result.append(whereArays[i]).append(" = ").append(c).append(whereValues[i].equals("@NULL") ? "" : whereValues[i]).append(c);
        }
        return result.toString();
    }

    public static String ArrayToString(String[] s, char c, String firstString, String endString, String StringZW) {

        if (s == null || s.length == 0) return "";

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < s.length; i++) {
            result.append(StringZW).append(s[i]).append(StringZW);
            if (i == s.length - 1) break;
            result.append(c);
        }
        StringBuffer sb = new StringBuffer(firstString);

        return sb.append(result.toString()).append(endString).toString();
    }


    public static String toJSON(Cursor cursor) {
        String key[] = cursor.getColumnNames();
        List<Map<String, String>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(key[i], cursor.getString(i));
            }
            list.add(map);
        }
        return new JSONArray(list).toString();
    }


    protected abstract String getTableName();

}
