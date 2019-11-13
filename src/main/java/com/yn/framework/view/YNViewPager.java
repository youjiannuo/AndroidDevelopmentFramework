package com.yn.framework.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by youjiannuo on 17/2/10.
 */

public class YNViewPager extends ViewPager {

    private int mSelect = 0;
    private OnScrollerListener mOnScrollerListener;
    private boolean mIsCheck = true;

    public YNViewPager(Context context) {
        super(context);
    }

    public YNViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                SystemUtil.printlnInfo(mSelect + "   " + position + " " + positionOffset + "    " + positionOffsetPixels);
                if (mIsCheck && mOnScrollerListener != null && positionOffsetPixels != 0) {
                    if (mSelect == 0) {
                        //当前在第一张
                        mOnScrollerListener.onScroller(positionOffset, 0, 1);
                    } else if (mSelect == getAdapter().getCount() - 1) {
                        //当前在最后一张
                        mOnScrollerListener.onScroller(1 - positionOffset, mSelect, mSelect - 1 < 0 ? 0 : mSelect - 1);
                    } else {
                        if (mSelect <= position) {
                            //向右滑动了
                            mOnScrollerListener.onScroller(positionOffset, mSelect, mSelect + 1);
                        } else {
                            //向左滑动了
                            mOnScrollerListener.onScroller(1 - positionOffset, mSelect, mSelect - 1);
                        }
                    }
                }
                if (positionOffsetPixels == 0) {
                    mSelect = position;
                }
            }

            @Override
            public void onPageSelected(int position) {
                mSelect = position;
                if (mOnScrollerListener != null) {
                    mOnScrollerListener.onSelect(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    mIsCheck = true;
                }
            }
            
        });
    }

    @Override
    public void setCurrentItem(int item) {
        if (item == mSelect) return;
        mIsCheck = false;
        super.setCurrentItem(item, false);
//        if (mOnScrollerListener != null) {
//            mOnScrollerListener.onScroller(1, mSelect, item);
//        }
    }

    public void setOnScrollerListener(OnScrollerListener l) {
        mOnScrollerListener = l;
    }

    public int getSelect() {
        return mSelect;
    }

    public interface OnScrollerListener {
        void onScroller(float ratio, int from, int to);

        void onSelect(int position);
    }

}
