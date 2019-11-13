package com.yn.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yn.framework.R;

/**
 * Created by youjiannuo on 2019/9/25.
 * Email by 382034324@qq.com
 */
public class DownAndUpView extends FrameLayout implements View.OnClickListener {

    private String[] mTexts = {"展开", "收起"};
    private int mOpenImageResource, mCloseImageResource;
    private TextView mTextView;
    private ImageView mImageView;
    private boolean mIsOpen = false;
    private OnSwitchListener mOnSwitchListener;

    public DownAndUpView(@NonNull Context context) {
        super(context);
    }

    public DownAndUpView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DownAndUpView);
        int arrayId = array.getResourceId(R.styleable.DownAndUpView_text, 0);
        mOpenImageResource = array.getResourceId(R.styleable.DownAndUpView_open_image, 0);
        mCloseImageResource = array.getResourceId(R.styleable.DownAndUpView_close_image, 0);
        int status = array.getInt(R.styleable.DownAndUpView_default_status, 0);
        if (arrayId != 0) {
            mTexts = getResources().getStringArray(arrayId);
        }
        array.recycle();
        LayoutInflater.from(context).inflate(R.layout.view_down_and_up, this);
        mImageView = findViewById(R.id.downImage);
        mTextView = findViewById(R.id.downText);
        setOnClickListener(this);
        if (status == 0) {
            mIsOpen = false;
            mImageView.setImageResource(mOpenImageResource);
            mTextView.setText(mTexts[0]);
        } else {
            mIsOpen = true;
            mImageView.setImageResource(mCloseImageResource);
            mTextView.setText(mTexts[1]);
        }

    }


    @Override
    public void onClick(View view) {
        if (!mIsOpen) {
            mImageView.setImageResource(mCloseImageResource);
            mTextView.setText(mTexts[1]);
        } else {
            mImageView.setImageResource(mOpenImageResource);
            mTextView.setText(mTexts[0]);
        }
        mIsOpen = !mIsOpen;
        if (mOnSwitchListener != null) {
            mOnSwitchListener.onSwitch(mIsOpen);
        }
    }


    public void setOnSwitchListener(OnSwitchListener mOnSwitchListener) {
        this.mOnSwitchListener = mOnSwitchListener;
    }

    public interface OnSwitchListener {
        void onSwitch(boolean isOpen);
    }

}
