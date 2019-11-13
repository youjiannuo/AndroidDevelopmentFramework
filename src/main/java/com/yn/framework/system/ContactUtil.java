package com.yn.framework.system;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;

import com.yn.framework.model.ContactKey;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author larson
 */
public class ContactUtil {
    private Context context;
    private List<Map<String, Object>> contactData;
    private Map<String, Object> jsonObject;
    //是否存在电话
    private boolean mIsMobile = false;


    public ContactUtil() {
        this(ContextManager.getContext());
    }

    public ContactUtil(Context context) {
        this.context = context;
    }

    // ContactsContract.Contacts.CONTENT_URI= content://com.android.contacts/contacts;
    // ContactsContract.Data.CONTENT_URI = content://com.android.contacts/data;

    /**
     * 获取联系人信息，并把数据转换成json数据
     *
     * @return
     * @throws JSONException
     */
    public List<Map<String, Object>> getContactInfo() throws JSONException {

        contactData = new ArrayList<>();
        String mimetype = "";
        int oldrid = -1;
        int contactId = -1;
        // 1.查询通讯录所有联系人信息，通过id排序，我们看下android联系人的表就知道，所有的联系人的数据是由RAW_CONTACT_ID来索引开的
        // 所以，先获取所有的人的RAW_CONTACT_ID
        Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
                null, null, null, Data.RAW_CONTACT_ID);
        if (cursor == null) return new ArrayList<>();
        int count = 0;
        while (cursor.moveToNext()) {
            contactId = cursor.getInt(cursor
                    .getColumnIndex(Data.RAW_CONTACT_ID));
            if (oldrid != contactId) {
                jsonObject = new HashMap<>();
                contactData.add(jsonObject);
                oldrid = contactId;
                jsonObject.put("contextId", oldrid);
            }
            mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面
            // 1.1,拿到联系人的各种名字
            if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                count++;
                cursor.getString(cursor
                        .getColumnIndex(StructuredName.DISPLAY_NAME));
//                String prefix = cursor.getString(cursor
//                        .getColumnIndex(StructuredName.PREFIX));
//                jsonObject.put("prefix", prefix);
                String firstName = StringUtil.getString(cursor.getString(cursor
                        .getColumnIndex(StructuredName.FAMILY_NAME)));

                jsonObject.put("firstName", firstName);
                String middleName = StringUtil.getString(cursor.getString(cursor
                        .getColumnIndex(StructuredName.MIDDLE_NAME)));
                jsonObject.put("middleNameEn", middleName);
                String lastname = StringUtil.getString(cursor.getString(cursor
                        .getColumnIndex(StructuredName.GIVEN_NAME)));
                jsonObject.put("lastName", lastname);
//                String suffix = cursor.getString(cursor
//                        .getColumnIndex(StructuredName.SUFFIX));
//                jsonObject.put("suffix", StringUtil.getString(suffix));
//                String phoneticFirstName = cursor.getString(cursor
//                        .getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
//                jsonObject.put("phoneticFirstName", phoneticFirstName);
                String name = firstName + middleName + lastname;
                jsonObject.put("name", name);
//                String phoneticMiddleName = cursor.getString(cursor
//                        .getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
//                jsonObject.put("phoneticMiddleName", StringUtil.getString(phoneticMiddleName));
//                String phoneticLastName = cursor.getString(cursor
//                        .getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
//                jsonObject.put("phoneticLastName", StringUtil.getString(phoneticLastName));
            }
            // 1.2 获取各种电话信息
            else if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                int phoneType = cursor
                        .getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
//                if (phoneType == Phone.TYPE_MOBILE) {
                String value = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                if (StringUtil.isEmpty(value)) continue;
                if (value.indexOf("+86") == 0) {
                    value = value.substring(3, value.length());
                }
                if (StringUtil.isEmpty(jsonObject.get("name"))) {
                    jsonObject.put("name", value);
                }
                value = "+86" + StringUtil.getFilterPhoneNum(value);
                if (!StringUtil.isPhoneNum86(value)) continue;
                Object obj = jsonObject.get("mobile");
                Map<String, String> map;
                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("mobile", map);
                } else map = (Map<String, String>) obj;

                if (phoneType == Phone.TYPE_MOBILE) {
                    push(map, ContactKey.TEL_HOME, value);
                }
                // 住宅电话
                else if (phoneType == Phone.TYPE_HOME) {
                    push(map, ContactKey.TEL_HOME, value);
                }
                // 单位电话
                else if (phoneType == Phone.TYPE_WORK) {
                    push(map, ContactKey.TEL_COMPANY, value);
                }
                // 单位传真
                else if (phoneType == Phone.TYPE_FAX_WORK) {
                    push(map, ContactKey.TEL_BUSINESS, value);
                }
                // 住宅传真
                else if (phoneType == Phone.TYPE_FAX_HOME) {
                    push(map, ContactKey.TEL_BUSINESS, value);
                }
                // 公司总机
                else if (phoneType == Phone.TYPE_COMPANY_MAIN) {
                    push(map, ContactKey.TEL_BUSINESS, value);
                }
                // 总机
                else if (phoneType == Phone.TYPE_MAIN) {
                    push(map, ContactKey.TEL_BUSINESS, value);
                } else {
                    push(map, ContactKey.TEL_HOME, value);
                }
            }
            //查找邮件
            else if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                int emailType = cursor.getInt(cursor.getColumnIndex(Email.TYPE));
                String value = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
                Object obj = jsonObject.get("email");
                Map<String, String> map;
                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("email", map);
                } else map = (Map<String, String>) obj;

                if (emailType == Email.TYPE_HOME) {
                    push(map, ContactKey.EMAIL_HOME, value);
                } else if (emailType == Email.TYPE_WORK) {
                    push(map, ContactKey.EMAIL_WORK, value);
                } else {
                    push(map, ContactKey.EMAIL_OTHER, value);
                }
            }


            // 查找event地址
            else if (ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出时间类型
                Object obj = jsonObject.get("anniversary");
                Map<String, String> map;
                String value = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("anniversary", map);
                } else map = (Map<String, String>) obj;

                int eventType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE)); // 生日
                if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
                    push(map, ContactKey.AN_BIRTH, value);
                }
                // 周年纪念日
                else if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY) {
                    push(map, ContactKey.AN_YEAR, value);
                }
            }
            // 获取即时通讯消息
            if (ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
                Object obj = jsonObject.get("im");
                Map<String, String> map;
                String value = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("im", map);
                } else map = (Map<String, String>) obj;

                int protocal = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                if (ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK == protocal) {
                    push(map, ContactKey.IM_GOOGLE_TALK, value);
                } else if (ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ == protocal) {
                    push(map, ContactKey.IM_QQ, value);
                } else if (ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE == protocal) {
                    push(map, ContactKey.IM_SKYTPE, value);
                } else if (ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO == protocal) {
                    push(map, ContactKey.IM_MM, value);
                } else {
                    push(map, ContactKey.IM_WEICHAT, value);
                }
            }
            // 获取备注信息
            if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                String remark = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                jsonObject.put("remark", remark);
            }
            // 获取昵称信息
            if (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
                String nickName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                jsonObject.put("nickName", nickName);
            }
            // 获取组织信息
            if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
                int orgType = cursor.getInt(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE)); // 单位
                if (orgType == ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM) { // if (orgType ==
                    // Organization.TYPE_WORK)
                    // {
                    String company = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                    jsonObject.put("company", StringUtil.getString(company));
                    String jobTitle = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    jsonObject.put("title", StringUtil.getString(jobTitle));
                    String department = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                    jsonObject.put("department", StringUtil.getString(department));
                }
            }
            // 获取网站信息
            if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
                Object obj = jsonObject.get("website");
                Map<String, String> map;
                String value = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("website", map);
                } else map = (Map<String, String>) obj;

                int webType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE)); // 主页
                if (webType == ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM) {
                    push(map, ContactKey.WEB_BBS, value);
                } // 主页
                else if (webType == ContactsContract.CommonDataKinds.Website.TYPE_HOME) {
                    push(map, ContactKey.WEB_BBS, value);
                }
                // 个人主页
                else if (webType == ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE) {
                    push(map, ContactKey.WEB_OWN, value);
                }
                // 工作主页
                else if (webType == ContactsContract.CommonDataKinds.Website.TYPE_OTHER) {
                    push(map, ContactKey.WEB_TECHNOLOGY_BLOG, value);
                }
            }
            // 查找通讯地址
            if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出邮件类型
                int postalType = cursor.getInt(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)); // 单位通讯地址
                Object obj = jsonObject.get("address");
                Map<String, String> map;
                String value = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));

                if (obj == null) {
                    map = new HashMap<>();
                    jsonObject.put("address", map);
                } else map = (Map<String, String>) obj;


                if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) {

                    push(map, ContactKey.ADDRESS_COMPANY, value);
//                    String ciry = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//                    jsonObject.put("ciry", ciry);
//                    String box = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//                    jsonObject.put("box", box);
//                    String area = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
//                    jsonObject.put("area", area);
//
//                    String state = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//                    jsonObject.put("state", state);
//                    String zip = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//                    jsonObject.put("zip", zip);
//                    String country = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//                    jsonObject.put("country", country);
                }
                // 住宅通讯地址
                else if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME) {

                    push(map, ContactKey.ADDRESS_HOME, value);
//                    String homeCity = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//                    jsonObject.put("homeCity", homeCity);
//                    String homeBox = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//                    jsonObject.put("homeBox", homeBox);
//                    String homeArea = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
//                    jsonObject.put("homeArea", homeArea);
//                    String homeState = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//                    jsonObject.put("homeState", homeState);
//                    String homeZip = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//                    jsonObject.put("homeZip", homeZip);
//                    String homeCountry = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//                    jsonObject.put("homeCountry", homeCountry);
                } else push(map, ContactKey.ADDRESS_OTHER, value);
            }
        }
        SystemUtil.printlnInfo("数据库:" + count);
        cursor.close();

        //过滤掉不存在正确电话
        for (int i = 0; i < contactData.size(); i++) {
            Map<String, Object> map = contactData.get(i);
            Map<String, String> mobile = (Map<String, String>) map.get("mobile");
            if (mobile == null
                    || mobile.size() == 0) {
                contactData.remove(i);
                i--;
            }
        }

        return contactData;
    }

    private void push(Map<String, String> jsonObject, String key, String value) {
        jsonObject.put(key, getString(jsonObject, key, value));
    }

    //存在多个
    private String getString(Map<String, String> jsonObject, String key, String value) {
        String oldValue = jsonObject.get(key);
        if (!StringUtil.isEmpty(oldValue)) {
            value = oldValue + "@@@" + value;
        }
        return value;
    }


}