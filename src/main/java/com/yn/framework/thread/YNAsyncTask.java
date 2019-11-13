package com.yn.framework.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.exception.YNOtherException;
import com.yn.framework.system.BuildConfig;
import com.yn.framework.system.SystemUtil;
import com.yn.framework.system.TimeUtil;

import java.util.LinkedList;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 16/11/16
 */
public abstract class YNAsyncTask<V, P, R> {

    private static Th THREAD[];
    private static LinkedList<YNAsyncTask> LINKED_LISTS;
    private static LinkedList<YNAsyncTask> LINKED_LISTS_ONE;
    private static LinkedList<YNAsyncTask> LINKED_LISTS_TWO;
    private static LinkedList<YNAsyncTask> LINKED_LISTS_THREE;
    private static Handler H;
    //最大的子线程，还开了一个单独执行的线程
    private static final int MAX_CHILDREN_THREAD = 6;
    //额外开的单线程
    private static final int EXTRA_SINGLE_THREAD = 3;

    private V[] mVs;
    private boolean mCancel = true;
    private TimeUtil mTimeUtil;
    private String mLog = "";
    private YNCommonActivity mActivity;
    //如果当前的Activity被回收掉了，停止运行当前任务
    private boolean mIsFinishActivityStopRun = false;
    //唯一的标示，如果在消息队列里面不会存在两个以上相同的任务
    private String mTag = "";

    public YNAsyncTask(String tag) {
        this();
        mTag = tag;
    }


    public YNAsyncTask() {
        if (!BuildConfig.ENVIRONMENT) {
            StackTraceElement stackTraceElement[] = Thread.currentThread().getStackTrace();
            if (stackTraceElement.length >= 5) {
                mLog = stackTraceElement[4].getFileName().split("\\.")[0] + ":"
                        + stackTraceElement[4].getLineNumber() + ":"
                        + stackTraceElement[4].getMethodName() + "()";
            }
        }
    }

    public YNAsyncTask setTag(String tag) {
        mTag = tag;
        return this;
    }

    public YNAsyncTask(YNCommonActivity activity, boolean isFinishActivityStopRun, String log) {
        mLog = log;
        this.mActivity = activity;
        this.mIsFinishActivityStopRun = isFinishActivityStopRun;
    }

    public static void init() {
        H = new Handler(Looper.getMainLooper());
        int cpuCount = Runtime.getRuntime().availableProcessors();
        SystemUtil.printlnInfo("获取CPU内核:" + cpuCount);
        cpuCount = cpuCount <= 0 ? MAX_CHILDREN_THREAD : (cpuCount > MAX_CHILDREN_THREAD ? MAX_CHILDREN_THREAD : cpuCount);
        SystemUtil.printlnInfo("开启线程个数:" + (cpuCount + EXTRA_SINGLE_THREAD));
        THREAD = new Th[cpuCount + EXTRA_SINGLE_THREAD];
        for (int i = 0; i < THREAD.length; i++) {
            THREAD[i] = new Th();
            if (i == THREAD.length - 1
                    || i == THREAD.length - 2
                    || i == THREAD.length - 3) {
                THREAD[i].setPriority(Thread.MAX_PRIORITY);
            }
            THREAD[i].start();
        }
        LINKED_LISTS = new LinkedList<>();
        LINKED_LISTS_ONE = new LinkedList<>();
        LINKED_LISTS_TWO = new LinkedList<>();
        LINKED_LISTS_THREE = new LinkedList<>();
    }

    @SafeVarargs
    public final synchronized void executeOnExecutor(V... v) {
        mVs = v;
        boolean is = false;
        onPreExecute();

        if (!BuildConfig.ENVIRONMENT) {
            int run = 0, sleep = 0;
            for (Th mThread : THREAD) {
                if (mThread.isSleep) {
                    sleep++;
                } else {
                    run++;
                }
            }
            print("阻塞线程:" + sleep + "  运行线程:" + run);
        }

        for (int i = 0; i < THREAD.length - EXTRA_SINGLE_THREAD; i++) {
            if (THREAD[i].isSleep) {
                synchronized (THREAD[i]) {
                    THREAD[i].isSleep = false;
                    THREAD[i].ynAsyncTask = this;
                    THREAD[i].list = LINKED_LISTS;
                    THREAD[i].notify();
                    is = true;
                    print("executeOnExecutor 线程已经被唤醒" + THREAD[i].toString() + "   " + mLog);
                    break;
                }
            }
        }

        if (!is) {
            addTask(LINKED_LISTS, this);
            print("executeOnExecutor 线程都已经在执行中需要放入栈中(" + LINKED_LISTS.size() + ")    " + mLog);
        }
    }

    protected void onPreExecute() {

    }


    private void addTask(LinkedList<YNAsyncTask> linkedList, YNAsyncTask task) {
        //扫描队列是否存在一样的消息,如果存在驳回当前请求
        synchronized (linkedList) {
            if (!isEmpty(task.mTag)) {
                for (YNAsyncTask task1 : linkedList) {
                    if (task1.mTag.equals(task.mTag)) {
                        print("存在相同任务 " + mLog + " tag = " + mTag);
                        return;
                    }
                }
            }
            linkedList.add(task);
        }
    }

    private YNAsyncTask getTask(LinkedList<YNAsyncTask> linkedList) {
        synchronized (linkedList) {
            if (linkedList != null && linkedList.size() > 0) {
                return linkedList.remove(0);
            }
        }
        return null;
    }

    @SafeVarargs
    public final synchronized void execute(V... v) {
        mVs = v;
        onPreExecute();
        Th t1 = THREAD[THREAD.length - 1];
        if (t1.isSleep) {
            synchronized (t1) {
                t1.isSleep = false;
                t1.ynAsyncTask = this;
                t1.list = LINKED_LISTS_ONE;
                t1.notify();
            }
        } else {
            addTask(LINKED_LISTS_ONE, this);
        }
    }

    @SafeVarargs
    public final synchronized void execute1(V... v) {
        mVs = v;
        onPreExecute();
        Th t1 = THREAD[THREAD.length - 2];
        if (t1.isSleep) {
            synchronized (t1) {
                t1.isSleep = false;
                t1.ynAsyncTask = this;
                t1.list = LINKED_LISTS_TWO;
                t1.notify();
            }
        } else {
            addTask(LINKED_LISTS_TWO, this);
        }
    }

    @SafeVarargs
    public final synchronized void executeNetwork(V... v) {
        mVs = v;
        onPreExecute();
        Th t1 = THREAD[THREAD.length - 3];
        if (t1.isSleep) {
            synchronized (t1) {
                t1.isSleep = false;
                t1.ynAsyncTask = this;
                t1.list = LINKED_LISTS_THREE;
                t1.notify();
            }
        } else {
            addTask(LINKED_LISTS_THREE, this);
        }
    }


    public void cancel(boolean is) {
        mCancel = is;
    }


    @SafeVarargs
    public final void publishProgress(final P... p) {
        H.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(p);
            }
        });
    }

    protected void onProgressUpdate(P... p) {

    }


    protected void onCancelled() {

    }

    private void background() {
        if (!BuildConfig.ENVIRONMENT) {
            mTimeUtil = new TimeUtil();
            print(mTimeUtil.getPrintInfo("线程执行开始 " + Thread.currentThread().toString()) + "    " + mLog);
        }
        if (mIsFinishActivityStopRun && mActivity != null && mActivity.isFinishing()) {
            if (!BuildConfig.ENVIRONMENT) {
                print("线程被持有的Activity回收掉了，停止运行 " + Thread.currentThread().toString() + "    " + mLog);
            }
            return;
        }
        final R r = doInBackground(mVs);
        if (mIsFinishActivityStopRun && mActivity != null && mActivity.isFinishing()) {
            if (!BuildConfig.ENVIRONMENT) {
                print("线程被持有的Activity回收掉了，停止运行 " + Thread.currentThread().toString() + "    " + mLog);
            }
            return;
        }
        if (mCancel) {
            H.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(r);
                }
            });
        }
        if (mTimeUtil != null) {
            print(mTimeUtil.getPrintInfo("线程执行结束 " + Thread.currentThread().toString()) + "    " + mLog);
            print("栈中剩余的任务数量:" + LINKED_LISTS.size());
            if (!BuildConfig.ENVIRONMENT) {
                int run = 0, sleep = 0;
                for (Th mThread : THREAD) {
                    if (mThread.isSleep) {
                        sleep++;
                    } else {
                        run++;
                    }
                }
                print("阻塞线程:" + sleep + "  运行线程:" + run);
            }
        }
    }

    protected abstract R doInBackground(V... v);

    protected void onPostExecute(R r) {

    }

    private static class Th extends Thread {

        LinkedList<YNAsyncTask> list;
        YNAsyncTask ynAsyncTask;
        boolean isSleep = true;

        public void run() {
            while (true) {
                synchronized (this) {
                    try {
                        isSleep = true;
                        clean();
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ynAsyncTask != null) {
                    isSleep = false;
                    try {
                        ynAsyncTask.background();
                    } catch (Exception e) {
                        new YNOtherException(e).throwException();
                        e.printStackTrace();
                    }
                    while (true) {
                        ynAsyncTask = ynAsyncTask.getTask(list);
                        if (ynAsyncTask == null) {
                            break;
                        }
                        if (!ynAsyncTask.mCancel) {
                            continue;
                        }
                        try {
                            ynAsyncTask.background();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        void clean() {
            ynAsyncTask = null;
        }

    }


    private void print(String s) {
        Log.i("thread", s);
    }


    public static String toStringInfo() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Th th : THREAD) {
            stringBuilder.append("线程:").append(th.toString());
            if (th.isSleep) {
                stringBuilder.append(":休眠");
            } else {
                stringBuilder.append(":正在执行\n");
                stringBuilder.append(th.ynAsyncTask.mLog);
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("普通剩余任务:").append(LINKED_LISTS.size()).append("\n");
        stringBuilder.append("图片下载任务剩余任务:").append(LINKED_LISTS_ONE.size()).append("\n");
        stringBuilder.append("LoadData剩余任务:").append(LINKED_LISTS_TWO.size()).append("\n");
        stringBuilder.append("网络堆栈剩余任务:").append(LINKED_LISTS_THREE.size()).append("\n");
        return stringBuilder.toString();
    }
}
