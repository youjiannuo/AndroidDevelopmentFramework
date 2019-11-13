package com.yn.framework.review;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.yn.framework.R;
import com.yn.framework.data.JSON;
import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.imageLoader.ImageViewNetwork;
import com.yn.framework.imageLoader.NetworkPhotoTask;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.review.manager.OnClickInterceptListener;
import com.yn.framework.review.manager.YJNView;
import com.yn.framework.system.SystemUtil;

import java.util.Map;

import static com.yn.framework.system.ContextManager.getString;
import static com.yn.framework.system.StringUtil.isEmpty;
import static com.yn.framework.system.StringUtil.isURL;

/**
 * Created by youjiannuo on 16/3/16.
 */
public class YNImageView extends ImageViewNetwork implements OnYNOperation {
    private YJNView mYJNView;
    protected String mDataKey;
    private int mHeight;
    private int mWidth;
    private int mPosition;
    private boolean mRounded = false;
    private boolean mIsValueEmptyVisible = false;
    private float mCorners = -1;
    private int mDefaultBitmap = -1;
    private Object mData;
    private ImageLoaderOperationListener mImageLoaderOperationListener;
    private OnClickListener mOnClickListener;

    public YNImageView(Context context) {
        super(context);
    }

    public YNImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mWidth = (int) array.getDimension(R.styleable.YNView_image_width, -1);
        mHeight = (int) array.getDimension(R.styleable.YNView_image_height, -1);
        int widthSize = array.getInt(R.styleable.YNView_image_width_size, 0);
        if (widthSize != 0) {
            mWidth = SystemUtil.getPhoneScreenWH(context)[0] / widthSize;
        }
        mYJNView = new YJNView(this, context, attrs);
        if (isEmpty(mDataKey)) {
            mDataKey = mYJNView.getDataKey();
        }

        TypedArray array1 = context.obtainStyledAttributes(attrs, R.styleable.YNImageView);
        mRounded = array1.getBoolean(R.styleable.YNImageView_rounded, false);
        mCorners = array1.getDimension(R.styleable.YNImageView_corners, -1);
        mIsValueEmptyVisible = array.getBoolean(R.styleable.YNImageView_is_value_empty_visible, mIsValueEmptyVisible);
        mDefaultBitmap = array1.getResourceId(R.styleable.YNImageView_default_bitmap, -1);
        if (array1.getBoolean(R.styleable.YNImageView_click_see_larger, false)) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getTask() != null && isURL(getTask().url)) {
                        Class cls = SystemUtil.getNameClass(getString(R.string.yn_see_large_photo_activity_class));
                        String key = getString(R.string.yn_see_large_photo_key);
                        Intent intent = new Intent(getContext(), cls);
                        intent.putExtra(key, getTask().url);
                        getContext().startActivity(intent);
                    }
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(YNImageView.this);
                    }
                }
            });
        }
        array1.recycle();
        if (mDefaultBitmap != -1) {
            setImageResource(mDefaultBitmap);
        }
    }

    @Override
    public void setData(Object obj) {
        if (obj == null || isEmpty(mDataKey)) return;
        mData = obj;
        mYJNView.setData(obj);
        String url;
        if (obj instanceof JSON) {
            url = ((JSON) obj).getStrings(mDataKey);
        } else if (obj instanceof String) {
            url = obj.toString();
        } else url = ((Map<String, String>) obj).get(mDataKey);
        setImageData(url);
    }

    public void setClickSeeLargerImage(OnClickListener largerImage) {
        mOnClickListener = largerImage;
    }

    public void setHttpId(int httpId) {
        mYJNView.setHttpId(httpId);
    }

    public void setImageData(String url) {
        if (mIsValueEmptyVisible) {
            if (isEmpty(url)) {
                setVisibility(GONE);
            } else {
                setVisibility(VISIBLE);
            }
        }
        NetworkPhotoTask task = NetworkPhotoTask.build();
        task.url = url;
        task.height = mHeight;
        task.width = mWidth;
        task.isSetRounded = mRounded;
        task.roundedCornersSize = (int) mCorners;
        task.startDrawId = mDefaultBitmap;
        task.imageLoaderOperationListener = mImageLoaderOperationListener;
        setImageParams(task);
    }


    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void setOnBackListener(OnBackListener l) {
        mYJNView.setOnBackListener(l, mPosition);
    }

    @Override
    public void setImageLoaderOperationListener(ImageLoaderOperationListener l) {
        mImageLoaderOperationListener = l;
    }

    @Override
    public void setOnClickInterceptListener(OnClickInterceptListener l) {
        mYJNView.setOnClickInterceptListener(l);
    }

    @Override
    public void setPosition(int index) {
        mPosition = index;
        mYJNView.setPosition(index);
    }

    @Override
    public int getOnClick() {
        return mYJNView.getOnClick();
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
