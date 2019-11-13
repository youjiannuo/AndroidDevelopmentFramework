package com.yn.framework.review;


import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.review.manager.OnClickInterceptListener;

/**
 * Created by youjiannuo on 16/3/16.
 */
public interface OnYNOperation {

    void setData(Object obj);

    int getType();

    void setOnBackListener(OnBackListener l);

    void setImageLoaderOperationListener(ImageLoaderOperationListener l);

    void setOnClickInterceptListener(OnClickInterceptListener l);

    void setPosition(int index);

    int getOnClick();

    Object getData();

    OnYNOperation[] getYNOperation();

    void setYNOperation(OnYNOperation[] operations);


}
