package com.yn.framework.review;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import com.yn.framework.R;
import com.yn.framework.data.JSON;
import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.model.BaseModel;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.review.manager.OnClickInterceptListener;
import com.yn.framework.review.manager.Util;
import com.yn.framework.review.manager.YJNView;
import com.yn.framework.review.model.ReplaceModel;
import com.yn.framework.system.MethodUtil;
import com.yn.framework.system.StringUtil;

import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static com.yn.framework.data.DataUtil.getMoneyString;
import static com.yn.framework.system.ContextManager.getArrayString;
import static com.yn.framework.system.StringUtil.get$Params;
import static com.yn.framework.system.StringUtil.getString;
import static com.yn.framework.system.StringUtil.isNumeric;
import static com.yn.framework.system.StringUtil.parseInt;
import static java.lang.String.format;
import static java.lang.String.valueOf;


/**
 * Created by youjiannuo on 16/3/16.
 */
public class YNTextView extends android.support.v7.widget.AppCompatTextView implements OnYNOperation {
    //普通文本
    public final int TYPE_TEXT = 0;
    //金额
    public final int TYPE_MONEY = 1;
    private String mArrays[];
    protected String[] mDataKeys;
    private String[] mFormatKeys;
    private String mFormat;
    private YJNView mYJNView;
    protected int mPosition;
    protected String mStartString = "";
    protected String mEndString = "";
    protected String mStartKeys[] = null;
    protected String mEndKeys[] = null;
    protected String mValue = "";
    private int mType = 1;
    protected Object mData;
    private String mNotSetData = ""; //到达某一个字符不需要设置
    private ReplaceModel mPlace[];
    private String mDefaultText = "";
    private int mStartStringColor = Integer.MAX_VALUE;
    private int mStartStringSize = 0;
    private int mEndStringColor = Integer.MAX_VALUE;
    private int mEndStringSize = 0;
    private int mOperaNum = 0;
    private char mOpera = ' ';
    //文本类型
    private int mTextType = 0;
    private String mSelectValue = null;

    public YNTextView(Context context) {
        super(context);
    }

    public YNTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mStartString = array.getString(R.styleable.YNView_startString);
        mEndString = array.getString(R.styleable.YNView_endString);
        mNotSetData = array.getString(R.styleable.YNView_not_set_data);
        mPlace = Util.getCodeReplace(array.getString(R.styleable.YNView_replace));
        mYJNView = new YJNView(this, context, attrs, array);
        mDataKeys = mYJNView.getSetDataNames();

        TypedArray array1 = context.obtainStyledAttributes(attrs, R.styleable.YNTextView);

        String setDataNameString = array1.getString(R.styleable.YNTextView_set_data_name_string);
        mTextType = array1.getInt(R.styleable.YNTextView_text_type, mTextType);
        mDefaultText = getString(array1.getString(R.styleable.YNTextView_is_null_default));
        mStartStringColor = array1.getColor(R.styleable.YNTextView_startStringColor, mStartStringColor);
        mEndStringColor = array1.getColor(R.styleable.YNTextView_endStringColor, mEndStringColor);
        mStartStringSize = array1.getDimensionPixelSize(R.styleable.YNTextView_startStringTextSize, mStartStringSize);
        mEndStringSize = array1.getDimensionPixelSize(R.styleable.YNTextView_endStringTextSize, mEndStringSize);
        mSelectValue = array1.getString(R.styleable.YNTextView_select_value);
        String opera = array1.getString(R.styleable.YNTextView_operator_num);
        int arrayId = array1.getResourceId(R.styleable.YNTextView_array, -1);
        if (arrayId != -1) {
            mArrays = getArrayString(arrayId);
        }
        if (!isEmpty(opera)) {
            mOpera = opera.charAt(0);
            mOperaNum = parseInt(opera.substring(1));
        }
        if (!isEmpty(setDataNameString)) {
            mFormatKeys = StringUtil.get$Params(setDataNameString);
            mFormat = mFormatKeys[0];
        }
        array1.recycle();

        if (mStartString == null) {
            mStartString = "";
        } else {
            mStartKeys = get$Params(mStartString);
        }
        if (mEndString == null) {
            mEndString = "";
        } else {
            mEndKeys = get$Params(mEndString);
        }

        if (!StringUtil.isEmpty(mEndString) || !StringUtil.isEmpty(mStartString)) {
            String start = "", end = "";
            if (mStartKeys != null) {
                start = mStartKeys[0].replaceAll("%s", "");
            }
            if (mEndKeys != null) {
                end = mEndKeys[0].replaceAll("%s", "");
            }
            setTextStartAndEnd(start, defaultValue(), end);
        }

//        array = context.obtainStyledAttributes(attrs, R.styleable.YNTextView);
//        if (array.getBoolean(R.styleable.YNLinearLayout_onClickMethod, false)) {
//            mYJNView.setActivityBackListener((YNCommonActivity) getContext());
//        }
//        array.recycle();

    }

    public void setStartStringColor(int mStartStringColor) {
        this.mStartStringColor = mStartStringColor;
    }

    public void setStartStringSize(int mStartStringSize) {
        this.mStartStringSize = mStartStringSize;
    }

    public void setEndStringColor(int mEndStringColor) {
        this.mEndStringColor = mEndStringColor;
    }

    public void setEndStringSize(int mEndStringSize) {
        this.mEndStringSize = mEndStringSize;
    }

    public YNTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHttpId(int http) {
        mYJNView.setHttpId(http);
    }

    public void setOnClick(int click) {
        mYJNView.setOnClick(click);
    }

    public int getHttpId() {
        return mYJNView.getHttpId();
    }

    protected String defaultValue() {
        if (isEmpty(mDefaultText)) {
            mDefaultText = getText().toString();
        }
        return mDefaultText;
    }


    public void setDefaultValue(String defaultValue) {
        mDefaultText = defaultValue;
    }

    public void setDataNameKeys(String setNameData) {
        mDataKeys = new String[]{setNameData};
    }

    public void setStartString(String startString) {
        mStartString = startString;
    }

    @Override
    public void setData(Object obj) {
        mYJNView.setData(obj);
        mData = obj;

        String startString;
        String endString = mEndString;
        if (mPlace != null && mPlace.length != 0) {
            if (mPlace[0].value.equals(mValue.trim())) {
                if (mPlace[1].type == 1) {
                    endString = "";
                }
                mValue = mPlace[1].value;
            }
        }
        startString = getKeyValue(mStartKeys, obj);
        endString = getKeyValue(mEndKeys, obj);

        if (!(mDataKeys == null || mDataKeys.length == 0)) {
            if (obj instanceof BaseModel) {
                Object valueObj = MethodUtil.getFieldValue(obj, mYJNView.getDataKey());
                mValue = valueOf(valueObj);
                if (valueObj instanceof SpannableStringBuilder) {
                    setText((SpannableStringBuilder) valueObj);
                    return;
                }

            } else if (obj instanceof Map) {
                mValue = ((Map<String, String>) obj).get(mYJNView.getDataKey());
            } else if (obj instanceof String) {
                mValue = obj.toString();
            } else if (obj instanceof JSON) {
                JSON json = (JSON) obj;
                mValue = json.getStrings(mDataKeys);
            } else {
                Object valueObj = MethodUtil.getFieldValue(obj, mYJNView.getDataKey());
                mValue = valueOf(valueObj);
            }
            if (mOperaNum != 0 && isNumeric(mValue)) {
                switch (mOpera) {
                    case '/':
                        mValue = valueOf(parseInt(mValue) / (mOperaNum * 1.0f));
                        break;
                    case '+':
                        mValue = valueOf(parseInt(mValue) + mOperaNum);
                        break;
                    case '-':
                        mValue = valueOf(parseInt(mValue) - mOperaNum);
                }
            }
            setText(startString, mValue, endString);
        } else {
            setText(startString, defaultValue(), endString);
        }

        if (((mTextType & 4) > 0 || (mTextType & 8) > 0)) {
            if (isEmpty(mValue)) {
                if ((mTextType & 4) > 0) {
                    setVisibility(GONE);
                } else {
                    setVisibility(INVISIBLE);
                }
            } else {
                setVisibility(VISIBLE);
            }
            return;
        }
        if ((mTextType & 64) > 0) {
            View parentView = ((View) getParent());
            if (isEmpty(mValue)) {
                parentView.setVisibility(GONE);
            } else {
                parentView.setVisibility(VISIBLE);
            }
            return;
        }


        if (!isEmpty(mFormat)) {
            setText(startString, getKeyValue(mFormatKeys, obj), endString);
        }
    }

    protected String getKeyValue(String keys[], Object obj) {
        if (keys == null) return "";
        String values[] = new String[keys.length - 1];

        for (int i = 0; i < values.length; i++) {
            String key = keys[i + 1];
            //是否需要处理
            int index = keys[i + 1].indexOf(",");
            if (index != -1) {
                key = keys[i + 1].substring(0, index);
            }

            if (obj instanceof BaseModel) {
                values[i] = valueOf(MethodUtil.getFieldValue(obj, key));
            } else {
                values[i] = ((JSON) obj).getStrings(key);
            }
            //后台处理
            if (index != -1) {
                String end = keys[i + 1].substring(index).replace(",sub:", "");
                String subNum[] = end.split("--");
                int startIndex = parseInt(subNum[0]);
                int length = parseInt(subNum[1]);
                length = values[i].length() < length ? values[i].length() : length;
                if (length != 0) {
                    if (startIndex >= 0) {
                        values[i] = values[i].substring(startIndex, startIndex + length);
                    } else {
                        int beginIndex = values[i].length() + startIndex;
                        int endIndex = values[i].length() + startIndex + length;
                        if (beginIndex < endIndex && beginIndex >= 0 && endIndex <= values[i].length()) {
                            values[i] = values[i].substring(beginIndex, endIndex);
                        }
                    }
                }
            }
        }
        return format(keys[0], (Object[]) values);
    }


    public void setText(String startString, String value, String endString) {
        if (!StringUtil.isEmpty(mNotSetData) && mNotSetData.equals(value)) {
            return;
        }
        //输入类型
        if ((mTextType & TYPE_MONEY) > 0) {
            value = getMoneyString(value);
        } else if (mTextType == 16) {
            int index = parseInt(value);
            index = index < 0 ? 0 : index;
            value = mArrays[index];
        }
        if ((mTextType & 32) > 0) {
            int i = parseInt(value);
            if (i > 0) {
                value = "+" + value;
            }
        }
        value = getTextValue(value);
        if (!isEmpty(mSelectValue)) {
            setSelected(mSelectValue.equals(value));
        }
        setTextStartAndEnd(startString, value, endString);
    }

    public String getTextValue(String value) {
        return value;
    }

    public void setText(String text) {
        setText(mStartString, text, mEndString);
    }

    public void setTextStartAndEnd(String startString, String value, String endString) {
        if ("ignore".equals(mDefaultText)) {
            return;
        }
        value = isEmpty(value) ? mDefaultText : value;

        value = startString + value + endString;


        if ((mTextType & 2) > 0) {
            //显示网页
            setText(Html.fromHtml(value));
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(value);
        if (!isEmpty(startString)) {
            if (mStartStringColor != Integer.MAX_VALUE) {
                builder.setSpan(new ForegroundColorSpan(mStartStringColor), 0, startString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mStartStringSize != 0) {
                builder.setSpan(new AbsoluteSizeSpan(mStartStringSize), 0, startString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (!isEmpty(endString)) {
            if (mEndStringSize != 0) {
                builder.setSpan(new AbsoluteSizeSpan(mEndStringSize), value.length() - endString.length(), value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (mEndStringColor != Integer.MAX_VALUE) {
                builder.setSpan(new ForegroundColorSpan(mEndStringColor), value.length() - endString.length(), value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        setText(builder);

    }

    public String getValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = obj.toString();
        } else if (mDataKeys != null && mDataKeys.length != 0) {
            JSON json = (JSON) obj;
            value = json.getStrings(mDataKeys);
        }
        return value;
    }


    public void onStartClick() {
        mYJNView.onStartClick();
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public void setOnBackListener(OnBackListener l) {
        mYJNView.setOnBackListener(l, mPosition);
    }

    @Override
    public void setImageLoaderOperationListener(ImageLoaderOperationListener l) {

    }

    @Override
    public void setOnClickInterceptListener(OnClickInterceptListener l) {
        mYJNView.setOnClickInterceptListener(l);
    }


    @Override
    public void setPosition(int index) {
        mPosition = index;
    }

    @Override
    public int getOnClick() {
        return mYJNView.getOnClick();
    }

    public YJNView getYJNView() {
        return mYJNView;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public OnYNOperation[] getYNOperation() {
        return new OnYNOperation[0];
    }

    @Override
    public void setYNOperation(OnYNOperation[] operations) {

    }
}
