package com.yn.framework.remind;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yn.framework.R;
import com.yn.framework.system.SystemUtil;


/**
 * 提醒框
 *
 * @author 需要添加下面权限
 */
public class RemindAlertDialog implements View.OnClickListener {

    /**
     * 第一个按钮
     */
    public static final int LEFTBUTTON = 0;

    /**
     * 第二个按钮
     */
    public static final int CENTERBUTTON = 1;

    /**
     * 第三个按钮
     */
    public static final int RIGHTBUTTON = 2;

    /**
     * 提醒卡组件
     */
    private AlertDialog mAlertDialog = null;

    /**
     * 提醒框
     */
    private Context mContext = null;

    /**
     * 每一个按钮的值
     */
    private String mButton[] = null;

    /**
     * 按钮的标题
     */
    private String mTitle = null;

    /**
     * 显示的内容
     */
    private Object mMessage = null;

    /**
     * 图标
     */
    private int mIcon = -1;

    /**
     * 创建一个接口
     */
    private OnClickListener mOnClickListener = null;

    /**
     * 按钮监听
     */
    private OnKeyListener mOnKeyListener = null;

    private View mContextView;

    private View mExtraView;
    /**
     * 按钮类型
     */
    private int mType = -1;

    private int mMargin = 80;

    /**
     * 构造函数，调用该构造函数
     *
     * @param context
     */
    public RemindAlertDialog(Context context) {
        this.mContext = context;
    }

    /**
     * 构造函数
     *
     * @param context
     * @param button          按钮的名称
     * @param title           主题
     * @param message         消息
     * @param Icon            图片
     * @param onClickListener 监听事件
     */
    public RemindAlertDialog(Context context, String button[], String title, Object message, int Icon, OnClickListener onClickListener) {
        this(context);
        set(button, title, message, onClickListener);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param button          按钮的名称
     * @param title           主题
     * @param message         消息
     * @param onClickListener 监听事件
     */
    public RemindAlertDialog(Context context, String button[], String title, String message, OnClickListener onClickListener) {
        this(context, button, title, message, -1, onClickListener);
    }

    /**
     * @param context
     * @param buttonId
     * @param titleId
     * @param messageId
     * @param l
     */
    public RemindAlertDialog(Context context, int buttonId[], int titleId, int messageId, OnClickListener l) {
        this(context, getButton(buttonId, context), titleId < 0 ? "" : context.getString(titleId), messageId < 0 ? "" : context.getString(messageId), l);
    }

    private static String[] getButton(int buttonId[], Context context) {
        if (buttonId == null) return new String[0];
        String button[] = new String[buttonId.length];
        for (int i = 0; i < button.length; i++) {
            button[i] = context.getString(buttonId[i]);
        }
        return button;
    }


    public void set(String button[], String title, Object message, OnClickListener onClickListener) {
        this.mButton = button;
        this.mTitle = title;
        this.mOnClickListener = onClickListener;
        this.mMessage = message;
    }

    public void showSoftInput() {
        show();
        openSoftInput();
    }

    public RemindAlertDialog setMargin(int margin) {
        mMargin = margin;
        return this;
    }

    /**
     * 显示对话框
     */
    public void show() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return;
        }
        //当前的activity是否被回收了
        if (mContext instanceof Activity && ((Activity) (mContext)).isFinishing()) return;
        Installbutton();
        mAlertDialog.setCanceledOnTouchOutside(false);
//        Window window = mAlertDialog.getWindow();
//        window.clearFlags();
        try {
            mAlertDialog.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }


        if (mContextView == null) {
            FrameLayout.LayoutParams params = getLayoutParams();
            params.gravity = Gravity.CENTER;
            mContextView = LayoutInflater.from(mContext).inflate(R.layout.y_view_alter_dialog, null);
            initView(mContextView);
            if (mExtraView != null) {
                addExtra(mExtraView);
            }
            mAlertDialog.setContentView(mContextView, params);
            mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (mOnKeyListener != null) {
                        return mOnKeyListener.onKey(dialog, keyCode, mType, event);
                    }
                    return false;
                }
            });
        }
        setParams();
    }

    public void openSoftInput() {
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void addExtra(View v) {
        mExtraView = v;
        if (mContextView != null) {
            ViewGroup viewGroup = mContextView.findViewById(R.id.extra);
            if (viewGroup.getChildCount() == 0) {
                viewGroup.addView(mExtraView);
            }
        }
    }

    protected void initView(View view) {

    }

    private FrameLayout.LayoutParams getLayoutParams() {
//        if (SystemUtil.getAndroidApi() >= 20) {
//            return new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        } else
        return new FrameLayout.LayoutParams(SystemUtil.getPhoneScreenWH(mContext)[0] - SystemUtil.dipTOpx(mMargin), LayoutParams.MATCH_PARENT);
    }

    /**
     * 注册按键监听
     *
     * @param l
     */
    public void setOnKeyListener(OnKeyListener l) {
        mOnKeyListener = l;
    }

    public void setType(int type) {
        mType = type;
    }

    private void setParams() {

        final Button left = (Button) getView(R.id.leftButton);
        final Button right = (Button) getView(R.id.rightButton);
        final Button center = (Button) getView(R.id.centerButton);


        final TextView title = (TextView) getView(R.id.title);
        final TextView msg = getMsgTextView();
        final View line1 = getView(R.id.line1);
        final View line2 = getView(R.id.line2);

        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
        center.setVisibility(View.VISIBLE);
        line1.setVisibility(View.VISIBLE);
        line2.setVisibility(View.VISIBLE);


//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msg.getLayoutParams();
//        if (SystemUtil.getAndroidApi() >= 21) {
//            params.topMargin = SystemUtil.dipTOpx(10);
//            params.bottomMargin = SystemUtil.dipTOpx(20);
//        } else {
//            params.bottomMargin = SystemUtil.dipTOpx(16);
//        }
//        msg.setLayoutParams(params);
        if (mButton.length == 1) {
            right.setVisibility(View.GONE);
            center.setVisibility(View.GONE);
            left.setText(mButton[0]);
            left.setOnClickListener(this);
            left.setBackgroundResource(R.drawable.yn_button_bottom_left_right_radius_3_white);
            left.setTextColor(0xFFF95732);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        } else if (mButton.length == 2) {
            center.setVisibility(View.GONE);
            right.setText(mButton[1]);
            left.setText(mButton[0]);
            left.setOnClickListener(this);
            line1.setVisibility(View.GONE);
            right.setOnClickListener(this);
            left.setBackgroundResource(R.drawable.yn_button_bottom_left_radius_3_white);
            right.setTextColor(0xFFF95732);
            right.setBackgroundResource(R.drawable.yn_button_bottom_right_radius_3_white);
        } else if (mButton.length == 3) {
            right.setText(mButton[2]);
            left.setText(mButton[0]);
            center.setText(mButton[1]);
            left.setOnClickListener(this);
            right.setOnClickListener(this);
            center.setOnClickListener(this);
            center.setBackgroundResource(R.drawable.hfh_button_white);
            left.setBackgroundResource(R.drawable.yn_button_bottom_left_radius_3_white);
            right.setBackgroundResource(R.drawable.yn_button_bottom_right_radius_3_white);
        } else if (mButton.length == 0) {
            right.setVisibility(View.GONE);
            center.setVisibility(View.GONE);
            left.setVisibility(View.GONE);
        }

        setTextView(title, mTitle);
        setTextView(msg, mMessage);
    }

    public void setMessageTextSize(int size) {
        TextView textView = (TextView) getView(R.id.textView);
        textView.setTextSize(size);
    }

    private void setTextView(TextView tv, Object msg) {
        CharSequence character;
        if (msg instanceof String) {
            character = (CharSequence) msg;
        } else {
            character = (Spanned) msg;
        }
        if (msg == null || character.length() == 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(character);
        }
    }

    public TextView getMsgTextView() {
        return (TextView) getView(R.id.message);
    }

    public Button getLeftButton() {
        return (Button) getView(R.id.leftButton);
    }

    public Button getRightButton() {
        return (Button) getView(R.id.rightButton);
    }

    public TextView getTitleView() {
        return (TextView) getView(R.id.title);
    }

    private View getView(int id) {
        return mContextView.findViewById(id);
    }

    /**
     * 显示对话框
     *
     * @param button  按钮的名称
     * @param title   主题
     * @param message 消息
     * @param icon    图片
     */
    public void show(String button[], String title, Object message, int icon, OnClickListener onClickListener) {
        set(button, title, message, onClickListener);
        show();
    }

    /**
     * 重新设置消息
     *
     * @param message 消息
     */
    public void show(String message) {
        this.mMessage = message;
        show();
    }


    /**
     * 初始化alert
     */
    private void Installbutton() {
        if (mAlertDialog == null) {
            //设置
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.yn_dialog).create();
        }
    }

    /**
     * 关闭
     */
    public void close() {
        if (mAlertDialog == null) return;
        mAlertDialog.cancel();
    }


    public boolean isShowing() {
        return mAlertDialog != null && mAlertDialog.isShowing();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (mOnClickListener == null) return;
        int status = LEFTBUTTON;
        if (v.getId() == R.id.centerButton) {
            status = CENTERBUTTON;
        } else if (v.getId() == R.id.rightButton) {
            status = RIGHTBUTTON;
        }
        if (!mOnClickListener.onRemindItemClick(status, mType)) {
            close();
        }

    }

    public interface OnKeyListener {
        boolean onKey(DialogInterface dialog, int keyCode, int type, KeyEvent event);
    }

    /**
     * 接口
     *
     * @author onRemindItemClick
     */
    public interface OnClickListener {
        boolean onRemindItemClick(int position, int type);
    }


}
