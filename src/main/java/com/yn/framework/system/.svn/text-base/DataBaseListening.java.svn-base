package com.yn.framework.system;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;

/**
 * Created by youjiannuo on 16/11/3
 */
public class DataBaseListening {

    private OnDataBaseChangeListener mOnDataBaseChangeListener;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            SystemUtil.printlnInfo("数据库发送改变");
            if (mOnDataBaseChangeListener != null) {
                mOnDataBaseChangeListener.change();
            }
        }
    };


    public void startContactListener(OnDataBaseChangeListener l) {
        startListener(ContactsContract.Contacts.CONTENT_URI, l);
    }

    public void startListener(Uri uri, OnDataBaseChangeListener l) {
        mOnDataBaseChangeListener = l;
        ContextManager.getContext().getContentResolver()
                .registerContentObserver(uri, true, mObserver);
    }


    public interface OnDataBaseChangeListener {
        void change();
    }

}
