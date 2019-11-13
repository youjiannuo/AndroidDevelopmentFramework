package com.yn.framework.system;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by youjiannuo on 16/1/13.
 */
public class TimeUtil {

    private HandlerThread mHandlerThread;
    private Handler mHandler;


    private long mTime;
    private boolean mIsFirst = true;

    private static TimeUtil TIME_UTIL;


    public static void see(String key) {
        if (TIME_UTIL == null) {
            TIME_UTIL = new TimeUtil();
            SystemUtil.printlnInfo("開始");
        } else {
            TIME_UTIL.println(key);
        }
    }

    public TimeUtil() {
        mTime = new Date().getTime();
    }

    public TimeUtil(long initTime) {
        mTime = initTime;
    }

    public void init() {
        mTime = 0;
    }

    public void println(String key) {
        SystemUtil.printlnInfo(key + "  消耗时间:" + (new Date().getTime() - mTime));
        mTime = new Date().getTime();
    }

    public static String getMonth() {
        return getString(new Date().getMonth() + 1);
    }

    public static String getDate() {
        return getString(new Date().getDate());
    }

    public static String getString(int value) {
        String s = value + "";
        if (s.length() == 2) return s;
        return "0" + s;
    }


    public String getPrintInfo(String key) {
        String info = key + "  消耗时间:" + (new Date().getTime() - mTime);
        mTime = new Date().getTime();
        return info;
    }

    public static String getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public static String getTime(String time) {
        Date date = getDate(time, false);
        return getString(date.getMonth() + 1) + "." + getString(date.getDate()) + " " + getString(date.getHours()) + ":" + getString(date.getMinutes()) + ":" + getString(date.getSeconds());
    }

    public static String getMonthAndDate(String time) {
        Date date = getDate(time, false);
        return getString(date.getMonth() + 1) + "月" + getString(date.getDate()) + "日";
    }

    public static String getYearAndMonth(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(new Date(time));
    }

    public static String getYear(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(new Date(time));
    }

    public static String getMonth(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(new Date(time));
    }

    public static String getTime(String inputTimeFormat, String time, String toFormat) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(inputTimeFormat).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(toFormat);
        return simpleDateFormat.format(date);
    }


    public static String getTimeSecond(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(getDate(time, true));
    }

    public static Date getDate(String time, boolean second) {
        if (StringUtil.isEmpty(time)) {
            return new Date();
        }
        long value;
        try {
            value = Long.parseLong(time) * (second ? 1000 : 1);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
        return new Date(value);
    }

    public static boolean isSameMonth(Date date1, Date date) {
        return date1.getYear() == date.getYear() && date1.getMonth() == date.getMonth();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return isSameMonth(date1, date2) && date1.getDate() == date2.getDate();
    }

    public static String getMonthAndDay(String time) {
        Date date = getDate(time, false);
        return getString(date.getMonth() + 1) + "." + getString(date.getDate());
    }

    public void checkTime(final EditText editText, long time, final OnTimeEditTextListener l) {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("thread");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        mHandler.postDelayed(new Runnable() {
            final String mText = editText.getText().toString();

            @Override
            public void run() {
                if (l != null && editText.getText().toString().equals(mText)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            l.onTimeTextView(mText);
                        }
                    });
                }
            }
        }, time);
    }

    public static Timer startTimer(final int startTime, long time, long delay, final OnTimeListener l) {
        final Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            int i = startTime;

            @Override
            public void run() {
                if (l != null) {
                    new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (l.onTime(++i)) {
                                timer.cancel();
                            }
                        }
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, delay, time);
        return timer;
    }

    /**
     * 超过时间
     *
     * @param runTime
     * @return false 表示已经超时，true表示没有超时
     */
    public boolean checkoutTime(long runTime) {
        long a = new Date().getTime();
        boolean is = a - mTime < runTime;
        SystemUtil.printlnInfo("is = " + is);
        if (!is) {
            mTime = a;
        }
        return is;
    }



    public interface OnTimeEditTextListener {
        void onTimeTextView(String text);
    }

    public interface OnTimeListener {
        boolean onTime(int index);
    }

    public static long getDay(int day) {
        return day * getHours(24);
    }

    public static long getHours(int hours) {
        return hours * 60 * 60 * 1000;
    }

    public static String getTime(long time, String pattren) {
        return new SimpleDateFormat(pattren).format(new Date(time));
    }

    public static Date parseDate(String time, String pattren) {
        Date date;
        if (StringUtil.isEmpty(time)) {
            return new Date();
        }
        try {
            date = new SimpleDateFormat(pattren).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
        return date;
    }


    public static String getTimeTwoCH(long time) {
        time = time < 60 * 1000 ? 60 * 1000 : time;
        int d, h, m;
        long result[] = getTime1(time, 24 * 60 * 60 * 1000);
        d = (int) result[0];
        result = getTime1(result[1], 60 * 60 * 1000);
        h = (int) result[0];
        result = getTime1(result[1], 60 * 1000);
        m = (int) result[0];
        return getTime2(d, "天") + getTime2(h, "时") + getTime2(m, "分");
    }

    private static String getTime2(int d, String ch) {
        if (d == 0) return "";
        return d + ch;
    }

    private static long[] getTime1(long time, long device) {
        int d = (int) (time * 1.0f / device);
        return new long[]{d, time - device * d};
    }

    public static Date parseDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String formatDate(long time, String pattern) {
        return formatDate(new Date(time), pattern);
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

}
