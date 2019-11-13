package com.yn.framework.review;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import com.yn.framework.R;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by youjiannuo on 16/4/22.
 */
public class YNTimeTextView extends YNTextView {
    String mName = "yyyy-MM-dd HH:mm:ss";
    String mInputTime = "long";


    public YNTimeTextView(Context context) {
        super(context);
    }

    public YNTimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNTimeTextView);
        String name = array.getString(R.styleable.YNTimeTextView_textTime);
        String time = array.getString(R.styleable.YNTimeTextView_inputTime);
        if (!StringUtil.isEmpty(time)) {
            mInputTime = time;
        }
        if (!StringUtil.isEmpty(name)) {
            mName = name;
        }
        array.recycle();
    }

    @Override
    public void setText(String startString, String value, String endString) {
        Date date;
        if (StringUtil.isEmpty(value)) {
            setText(new SpannableStringBuilder());
            return;
        }
        if ("long".equals(mInputTime) || "int".equals(mInputTime)) {
            date = TimeUtil.getDate(value, "int".equals(mInputTime));
        } else {
            try {
                date = new SimpleDateFormat(mInputTime).parse(value);
            } catch (ParseException e) {
//                e.printStackTrace();
                return;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mName);
        value = simpleDateFormat.format(date);
        super.setText(startString, value, endString);
    }


}
