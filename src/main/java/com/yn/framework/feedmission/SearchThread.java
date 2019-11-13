package com.yn.framework.feedmission;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.yn.framework.system.ContactUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by vincent.shi on 2016/12/13.
 */

public class SearchThread implements Runnable {
    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };
    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 上下文
     **/
    private Context mContext;
    private Handler mHandler;

    List<Map<String, Object>> mCardInformation;
    ContactUtil contactUtil;
    String contactName;
    String phoneNumber;

    static String mNameKey;
    static Object mNameValue;
    static String mNumberKey;
    static Object mNumberValue;
    static boolean mIsTrue = true;
    static int flag = 0;
    static int messageStatus;

    public SearchThread(Context mContext, Handler mHandler) throws JSONException {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        compareData();
        try {
            while (mIsTrue) {
                getPhoneContacts();
                mIsTrue = compare();
                Log.v("权限", "权限" + mIsTrue);
                Message msg = mHandler.obtainMessage();
                if (mIsTrue == false) {
                    compareData();
                    mIsTrue = compare();
                    Log.v("data", "1" + mIsTrue + "2" + mNameValue);
                    if ((mIsTrue == false) && (mNameValue == null)) {
                        Log.v("獲取權限失敗", "獲取權限失敗");
                        messageStatus = flagStatus(mIsTrue);
                        msg.what = messageStatus;
                        Log.v("1", messageStatus + "确定");
                        mHandler.sendMessage(msg);
                        mIsTrue = true;
                    }
                } else {
                    messageStatus = flagStatus(mIsTrue);
                    msg.what = messageStatus;
                    Log.v("0", messageStatus + "确定");
                    mHandler.sendMessage(msg);
                }
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送message的flag
     * flag 返回状态值
     * 0 权限开启
     * 1 权限关闭
     * 2 权限flag重复,不显示
     **/
    private int flagStatus(boolean b) {
        int i = 0;
        if (b == true) {
            i = 0;
            if (i == flag) {
                return 2;
            }
            flag = 0;
            return 0;
        } else {
            i = 1;
            if (i == flag) {
                return 2;
            }
            flag = 1;
            return 1;
        }
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        contactName = null;
        phoneNumber = null;
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                Log.v("Name", contactName);
                Log.v("phoneNumber", phoneNumber);
                if ((contactName.length() == 0) && (phoneNumber.length() == 0)) {
                    Log.v("判断", "没开权限");
                }
                break;
            }
            phoneCursor.close();
        }
    }

    /**
     * 取得比較名片信息;
     **/
    private void compareData() {
        contactUtil = new ContactUtil(mContext);
        mCardInformation = new ArrayList<Map<String, Object>>();
        mNameValue = null;
        mNumberValue = null;
        try {
            mCardInformation = contactUtil.getContactInfo();
            for (int i = 0; i < mCardInformation.size(); i++) {
                Log.v("count", mCardInformation.size() + "\n");
                Map<String, Object> map = mCardInformation.get(i);
                Set<String> set = map.keySet();
                Iterator<String> its = set.iterator();
                while (its.hasNext()) {
                    // key
                    String key = its.next();
                    Log.v("key", key + "\n");
                    // value
                    Object value = map.get(key);
                    Log.v("value", value + "\n");
                    if (key.equals("name")) {
                        mNameKey = key;
                        mNameValue = value;
                        Log.v("mNameKey", mNameKey + "\n");
                        Log.v("mNameValue", mNameValue + "\n");
                    } else if (key.equals("mobile")) {
                        mNumberKey = key;
                        Object NumberValue = value;
                        mNumberValue = NumberValue.toString().substring(4, NumberValue.toString().length() - 1);
                        Log.v("mNumberKey", mNumberKey + "\n");
                        Log.v("mNumberValue", mNumberValue + "\n");
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 比較第一個名片的name和number
     **/
    public boolean compare() {
        if ((mNameValue != null) && (mNumberValue != null)) {
            if ((mNameValue.equals(contactName)) && (mNumberValue.equals(phoneNumber))) {
                Log.v("compare", "1" + mNameValue + "2" + contactName + "3" + mNumberValue + "4" + phoneNumber);
                return true;
            }
            return false;
        }
        return false;
    }

    //关闭线程
    public void onclose() {
        mIsTrue = false;
    }
}
