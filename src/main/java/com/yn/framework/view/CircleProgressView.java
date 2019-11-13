package com.yn.framework.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yn.framework.animation.Animation;
import com.yn.framework.system.SystemUtil;

import static java.lang.String.valueOf;

/**
 * Created by youjiannuo on 2018/6/11.
 * Email by 382034324@qq.com
 */
public class CircleProgressView extends View {
    private final float mStrokeWidth = SystemUtil.dipTOpx(10);
    private final float mTopLineWidth = SystemUtil.dipTOpx(2);
    private float mCircleAngle;
    private float mStartAngle = 135;
    private float mSweepAngle = 270;
    //最大分数
    private final float MAX = 110.0f;
    private final int MAX_INT = (int) MAX;

    private final float mSpace = SystemUtil.dipTOpx(6) + mStrokeWidth / 2;
    //小字的大小
    private final int mSmallFontSize = SystemUtil.dipTOpx(12);
    private final int mLargerFontSize = SystemUtil.dipTOpx(14);
    private final int mGrayColor = 0xFFEAEAEA;
    private final int mRedColor = 0xFFF6583B;
    //刻度尺的长度
    private final float mCalibrationWidth = SystemUtil.dipTOpx(4);
    //大刻度尺的长
    private final float mCalibrationItemLargerWidth = SystemUtil.dipTOpx(7);
    //大刻度尺的宽
    private final float mCalibrationItemLargerHeight = SystemUtil.dipTOpx(3);
    //当前的值
    private float mSelectScore = 0;
    //需要绘制的读书
    private float mDrawAngle = 0;
    private boolean isReSetWH = true;
    //保持设置的分值
    private int mSetScore = 0;

    private OnProgressListener mOnProgressListener;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final float score = (float) msg.obj;
            Animation.valueAnimator(mSelectScore, score, 1000, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float a = (float) animation.getAnimatedValue();
                    setScore(a);
                }

            }).addListener(new Animation.AnimationObjectListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mSelectScore = score;
                    setScore(mSelectScore);
                    if (mOnProgressListener != null) {
                        mOnProgressListener.onEnd();
                    }

                }
            });
        }
    };


    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mCircleAngle = (float) Math.atan(mStrokeWidth * 1.0f / 2 / ((getWidth() - mSpace) / 2.0f));
                mCircleAngle = (float) ((mCircleAngle / Math.PI) * 180);
                removeOnLayoutChangeListener(this);
                int height = (int) ((getWidth() / 2.0f) * (1 + (1.0f / Math.sqrt(2))) + mStrokeWidth);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                params.height = height;
                setLayoutParams(params);
            }
        });
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.mOnProgressListener = onProgressListener;
    }

    public void setAnimationScore(int score) {
        mSetScore = score;
        float score1 = decodeScore(score);
        Message msg = mHandler.obtainMessage();
        msg.obj = score1;
        if (getWidth() == 0) {
            mHandler.sendMessageDelayed(msg, 500);
        } else {
            mHandler.sendMessage(msg);
        }
    }

    //分数转入
    private float decodeScore(float score) {
        if (mSetScore <= 60) {
            score = decodeNum(score, 1, 60);
        } else if (mSetScore > 60 & mSetScore <= 90) {
            score = decodeNum(score, 2, 90);
        } else {
            score = decodeNum(score, 3, MAX_INT);
        }
        return score;
    }


    private float encodeScore(float score) {
        if (mSetScore <= 60) {
            score = encodeNum(score, 1, 60);
        } else if (mSetScore > 60 & mSetScore <= 90) {
            score = encodeNum(score, 2, 90);
        } else {
            score = encodeNum(score, 3, MAX_INT);
        }
        return Math.round(score);
    }

    //将分数转换成转盘的分值
    private float decodeNum(float value, int index, int max) {
        return ((MAX / 3) * index / max) * value;
    }

    //将转盘的分值转换成分数
    private float encodeNum(float value, int index, int max) {
        return (3 * max * value) / (MAX * index);
    }

    private void setScore(float score) {
        if (mOnProgressListener != null) {
            mOnProgressListener.onProgress(encodeScore(score));
        }
        float allProgress = (mSweepAngle - ((int) score == MAX_INT ? 0 : 2 * mCircleAngle));
        mDrawAngle = allProgress * ((score > MAX ? MAX : score) / MAX);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCalibration(canvas);
        drawProgress(canvas, mSweepAngle, mGrayColor, mTopLineWidth, true);
        //绘制背景进度条
        drawProgress(canvas, mSweepAngle, mGrayColor, mStrokeWidth);
        drawCircle(canvas, mGrayColor, mStartAngle);
        drawCircle(canvas, mGrayColor, mStartAngle + mSweepAngle);
        if (mDrawAngle > 0) {
            drawProgress(canvas, mDrawAngle, mRedColor, mStrokeWidth);
            drawCircle(canvas, mRedColor, mStartAngle);
            drawCircle(canvas, mRedColor, mStartAngle + mDrawAngle);
        }

    }

    private void drawProgress(Canvas canvas, float progress, int color, float lineWidth) {
        drawProgress(canvas, progress, color, lineWidth, false);
    }

    private void drawProgress(Canvas canvas, float progress, int color, float lineWidth, boolean line) {
        @SuppressLint("DrawAllocation")
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setAntiAlias(true);
        RectF rectF;

        if (!line) {
            rectF = new RectF(mSpace, mSpace, getWidth() - mSpace, getWidth() - mSpace);
        } else {
            rectF = new RectF(lineWidth, lineWidth, getWidth() - lineWidth, getWidth() - lineWidth);
        }
        canvas.drawArc(rectF, mStartAngle, progress, false, paint);
    }

    private void drawCalibration(Canvas canvas) {
        float space1 = mSpace + mStrokeWidth / 2;
        float space2 = space1 + mCalibrationWidth;
        float space3 = space1 + mCalibrationItemLargerWidth;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setColor(0xFFFF0000);
        paint.setColor(mGrayColor);
        for (int i = 0; i < 90; i++) {
            if (i == 15 || i == 45 || i == 75) continue;
            float angle = 45 + 3 * i;
            drawCalibrationSmallItem(angle, canvas, space1, space2, paint);
        }
        drawCalibrationSmallItem(90.00001f, canvas, space1, space2, paint);
        drawCalibrationSmallItem(360f, canvas, space1, space2, paint);
        drawCalibrationSmallItem(180f, canvas, space1, space2, paint);

        drawCalibrationLargerItem(mStartAngle + mSweepAngle / 3, 45, canvas, space1, paint);
        drawCalibrationLargerItem(mStartAngle + 2 * mSweepAngle / 3, 135, canvas, space1, paint);
        drawText("60", mStartAngle + mSweepAngle / 3, space1 + 30, paint, mSmallFontSize, canvas, 0);
        drawText("90", mStartAngle + 2 * mSweepAngle / 3, space1 + 30, paint, mSmallFontSize, canvas, 1);
        Paint paint1 = new Paint();
        paint1.setColor(0xFF8E939A);
        drawText("差", 180f, space1 + 25, paint1, mLargerFontSize, canvas, 0);
        drawText("良", 360f, space1 + 20, paint1, mLargerFontSize, canvas, 2);
        drawText("优", 90f, space1 + 25, paint1, mLargerFontSize, canvas, 1);
    }

    private void drawText(String text, float angle, float space1, Paint paint, float fontSize, Canvas canvas, int oration) {
        //绘制60度
        float xy4[] = calcLeftAndRight(angle, space1);
        paint.setTextSize(fontSize);
        if (oration == 0) {
            canvas.drawText(text, xy4[0], xy4[1] + fontSize, paint);
        } else if (oration == 1) {
            canvas.drawText(text, xy4[0] - fontSize, xy4[1] + fontSize, paint);
        } else {
            canvas.drawText(text, xy4[0] - fontSize / 2, xy4[1] + fontSize, paint);
        }
    }

    private void drawCalibrationSmallItem(float angle, Canvas canvas, float space1, float space2, Paint paint) {
        float xy1[] = calcLeftAndRight(angle, space1);
        float xy2[] = calcLeftAndRight(angle, space2);
        canvas.drawLine(xy1[1], xy1[0], xy2[1], xy2[0], paint);
    }

    private void drawCalibrationLargerItem(float angler, float degrees, Canvas canvas, float space1, Paint paint) {
        float a = mCalibrationItemLargerWidth / 2;
        float b = mCalibrationItemLargerHeight / 2;
        float xy3[] = calcLeftAndRight(angler, space1 + a);
        canvas.save();
        canvas.rotate(degrees, xy3[0], xy3[1]);
        canvas.drawRect(xy3[0] - a, xy3[1] - b, xy3[0] + a, xy3[1] + b, paint);
        canvas.drawCircle(xy3[0] + a, xy3[1], b, paint);
        canvas.restore();

    }

    private void drawCircle(Canvas canvas, int color, float angle) {
        if (angle > 360) {
            angle = angle - 360;
        }
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        float[] lt = calcLeftAndRight(angle, mSpace);
        canvas.drawCircle(lt[0], lt[1], mStrokeWidth / 2.0f, paint);
    }


    private float[] calcLeftAndRight(float angle, float space) {
        float r = (getWidth() - space * 2) / 2.0f;
        int l = 1;
        int t = 1;
        //如果图片出现问题，可以看看这里
        if (angle >= 0 & angle <= 180) {
            t = -1;
        }
        if ((angle <= 90 && angle >= 0) || (angle >= 270 && angle <= 360)) {
            l = -1;
        }
        float left;
        float top;
        float a = (float) (((angle - ((int) angle / 90) * 90) / 180.0f) * Math.PI);
        if ((angle >= 0 && angle <= 90) || (angle >= 180 && angle <= 270)) {
            left = Math.abs((float) (r * (l - Math.cos(a))));
            top = Math.abs((float) (r * (t - Math.sin(a))));
        } else {
            left = Math.abs((float) (r * (l - Math.sin(a))));
            top = Math.abs((float) (r * (t - Math.cos(a))));
        }
        return new float[]{left + space, top + space};
    }


    public interface OnProgressListener {
        void onProgress(float progress);

        void onEnd();
    }


    public static void main(String[]args){
        System.out.println(valueOf(Math.sqrt(2) * 80 + 80));

    }

}
